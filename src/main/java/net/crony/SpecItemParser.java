package net.crony;

import javaslang.Function1;
import javaslang.Tuple2;
import javaslang.collection.HashSet;
import javaslang.collection.Set;
import javaslang.control.Option;
import javaslang.control.Try;

public class SpecItemParser {

    public static Option<Set<Integer>> parseSpecItem(String value, int maxValue) {
        if (value.contains(",")) {
            Set<Option<Set<Integer>>> parsedList = HashSet.of(value.split(","))
                .map(v -> parseSpecItem(v, maxValue));
            return Option
                .sequence(parsedList)
                .map(s -> s.flatMap(Function1.identity()).toSet());
        } else if (value.equals("*")) {
            return Option.of(HashSet.empty());
        } else if (value.startsWith("*/")) {
            return Try.of(() -> buildInterval(Integer.parseInt(value.substring(2)), maxValue)).getOption();
        } else if (value.contains("-")) {
            return toIntPair(value.split("-")).map(p -> HashSet.range(p._1, p._2));
        }
        return Option.none();
    }

    private static Option<Tuple2<Integer, Integer>> toIntPair(String[] elements) {
        if (elements.length != 2) {
            return Option.none();
        }
        Tuple2<String, String> strPair = new Tuple2(elements[0], elements[1]);
        return Try.of(() -> strPair.map(Integer::parseInt, Integer::parseInt)).getOption();
    }

    private static Set<Integer> buildInterval(int step, int maxValue) {
        return HashSet.rangeBy(0, maxValue, step);
    }
}
