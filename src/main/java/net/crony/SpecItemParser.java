package net.crony;

import javaslang.Function1;
import javaslang.Tuple2;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.control.Option;
import javaslang.control.Try;
import javaslang.control.Validation;

public class SpecItemParser {

    public static Validation<String, Set<Integer>> parseSpecItem(String value, int maxValue) {
        if (value.contains(",")) {
            Set<Validation<String, Set<Integer>>> parsedList = HashSet.of(value.split(","))
                .map(v -> parseSpecItem(v, maxValue));
            return Javaslang.sequenceS(parsedList)
                .map(s -> s.flatMap(Function1.identity()).toSet());
        } else if (value.equals("*")) {
            return Validation.valid(HashSet.empty());
        } else if (Try.of(() -> Integer.parseInt(value)).isSuccess()) {
            return Validation.valid(HashSet.of(Integer.parseInt(value)));
        } else if (value.contains("/")) {
            return parseSlash(value, maxValue);
        } else if (value.contains("-")) {
            return parseRange(value)
                .map(p -> HashSet.rangeClosed(p._1, p._2));
        }
        return Validation.invalid("Value improperly formatted: " + value);
    }

    private static Validation<String, Tuple2<Integer,Integer>> parseRange(String rangeStr) {
        String[] elements = rangeStr.split("-");
        if (elements.length != 2) {
            return Validation.invalid("Invalid range, expected 2 items, got " + elements.length);
        }
        Tuple2<String, String> strPair = new Tuple2<>(elements[0], elements[1]);
        return Javaslang.tryValidation(
            () -> strPair.map(Integer::parseInt, Integer::parseInt),
            "Invalid range, item is not a number: " + strPair);
    }

    // this is borderline to move to JParsec but don't want to
    // take in a dependency for so littl.
    private static Validation<String, Set<Integer>> parseSlash(String value, int maxValue) {
        String[] elements = value.split("/");
        if (elements.length != 2) {
            return Validation.invalid("Expected 2 elements in a / rule, got " + elements.length);
        }
        Validation<String,Integer> intervalValidation = Javaslang.tryValidation(
            () -> Integer.parseInt(elements[1]), "Can't parse " + elements[1]);
        return Validation.combine(parseSlashLeft(elements[0], maxValue), intervalValidation).ap(
            (minMax, interval) -> HashSet.rangeClosedBy(minMax._1, minMax._2, interval))
            .leftMap(l -> l.mkString(", ")).map(HashSet::narrow);
    }

    private static Validation<String, Tuple2<Integer,Integer>> parseSlashLeft(String rangeString, int maxValue) {
        if (rangeString.contains("-")) {
            return parseRange(rangeString);
        } else if (rangeString.equals("*")) {
            return Validation.valid(new Tuple2<>(0, maxValue));
        } else {
            return Javaslang.tryValidation(() -> Integer.parseInt(rangeString),
                                           "Can't parse as integer: " + rangeString)
                .map(s -> new Tuple2<>(s, maxValue));
        }
    }
}
