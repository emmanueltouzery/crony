package com.github.emmanueltouzery;

import javaslang.Function1;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Validation;

// parsing with jparsec would be neater, but it's such a small-scale
// parsing, it would be a shame to incur a dependency for that.
/*package*/ class SpecItemParser {

    /*package*/ static Validation<String, Set<Integer>> parseSpecItem(
        String value, int maxValue) {
        return parseSpecItem(value, maxValue, HashMap.empty());
    }

    /*package*/ static Validation<String, Set<Integer>> parseSpecItem(
        String value, int maxValue, Map<String, Integer> stringMap) {
        if (value.contains(",")) {
            Set<Validation<String, Set<Integer>>> parsedList = HashSet.of(value.split(","))
                .map(v -> parseSpecItem(v, maxValue, stringMap));
            return Javaslang.sequenceS(parsedList)
                .map(s -> s.flatMap(Function1.identity()).toSet());
        } else if (value.equals("*")) {
            return Validation.valid(HashSet.empty());
        } else if (value.contains("/")) {
            return parseSlash(value, maxValue);
        } else if (value.contains("-")) {
            return parseRange(value)
                .map(p -> HashSet.rangeClosed(p._1, p._2));
        } else if (stringMap.containsKey(value)) {
            return Validation.valid(HashSet.of(stringMap.get(value).get()));
        }
        return Javaslang.validationParseInt(value).map(HashSet::of);
    }

    private static Validation<String, Tuple2<Integer,Integer>> parseRange(String rangeStr) {
        return Javaslang.splitValidate(rangeStr, "-", 2)
            .flatMap(elements -> Javaslang.tryValidation(
                         () -> new Tuple2<>(Integer.parseInt(elements[0]), Integer.parseInt(elements[1])),
                         "Invalid range, one item is not a number: " + rangeStr));
    }

    private static Validation<String, Set<Integer>> parseSlash(String value, int maxValue) {
        return Javaslang.splitValidate(value, "/", 2)
            .flatMap(elements -> parseSlashLeft(elements[0], maxValue)
                     .flatMap(minMax -> Javaslang.validationParseInt(elements[1])
                              .map(interval -> HashSet.rangeClosedBy(minMax._1, minMax._2, interval))));
    }

    private static Validation<String, Tuple2<Integer,Integer>> parseSlashLeft(String rangeString, int maxValue) {
        if (rangeString.contains("-")) {
            return parseRange(rangeString);
        } else if (rangeString.equals("*")) {
            return Validation.valid(new Tuple2<>(0, maxValue));
        } else {
            return Javaslang.validationParseInt(rangeString)
                .map(s -> new Tuple2<>(s, maxValue));
        }
    }
}
