package com.github.emmanueltouzery.crony;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Validation;

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
            return Vavr.sequenceS(parsedList)
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
        return Vavr.validationParseInt(value).map(HashSet::of);
    }

    private static Validation<String, Tuple2<Integer,Integer>> parseRange(String rangeStr) {
        return Vavr.splitValidate(rangeStr, "-", 2)
            .flatMap(elements -> Vavr.tryValidation(
                         () -> Tuple.of(Integer.parseInt(elements[0]), Integer.parseInt(elements[1])),
                         "Invalid range, one item is not a number: " + rangeStr));
    }

    private static Validation<String, Set<Integer>> parseSlash(String value, int maxValue) {
        return Vavr.splitValidate(value, "/", 2)
            .flatMap(elements -> parseSlashLeft(elements[0], maxValue)
                     .flatMap(minMax -> Vavr.validationParseInt(elements[1])
                              .map(interval -> HashSet.rangeClosedBy(minMax._1, minMax._2, interval))));
    }

    private static Validation<String, Tuple2<Integer,Integer>> parseSlashLeft(String rangeString, int maxValue) {
        if (rangeString.contains("-")) {
            return parseRange(rangeString);
        } else if (rangeString.equals("*")) {
            return Validation.valid(Tuple.of(0, maxValue));
        } else {
            return Vavr.validationParseInt(rangeString)
                .map(s -> Tuple.of(s, maxValue));
        }
    }
}
