package com.github.emmanueltouzery.crony;

import java.time.*;

import io.vavr.collection.Set;
import io.vavr.collection.List;
import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;

public class CronyArbitrary {

    public static Arbitrary<Cron> cron() {
        return intSet(0, 59)
            .flatMap(mins -> intSet(0, 23)
                     .flatMap(hour -> dayOfMonthSet()
                              .flatMap(dayMonth -> months()
                                       .flatMap(months -> daysOfWeek()
                                                .map(daysOfWeek -> Cron.build(mins, hour, dayMonth, months, daysOfWeek).get())))));
    }

    private static Arbitrary<Set<Integer>> dayOfMonthSet() {
        return intSet(DayOfMonthSpec.LAST_DAY_OF_MONTH, 31)
            .map(set -> set.filter(x -> x != 0));
    }

    private static Arbitrary<Set<Integer>> intSet(int min, int max) {
        return set(Gen.choose(min, max).arbitrary());
    }

    public static Arbitrary<Set<DayOfWeek>> daysOfWeek() {
        return set(Gen.choose(DayOfWeek.values()).arbitrary());
    }

    public static Arbitrary<Set<Month>> months() {
        return set(Gen.choose(Month.values()).arbitrary());
    }

    public static <T> Arbitrary<Set<T>> set(Arbitrary<T> input) {
        return Arbitrary.list(input).map(List::toSet);
    }

    public static Arbitrary<ZonedDateTime> zonedDateTime(RangeType rangeType) {
        return localDateTime(rangeType)
            .flatMap(local -> zoneId().map(zone -> ZonedDateTime.of(local, zone)));
    }

    public static Arbitrary<ZoneId> zoneId() {
        return Gen.choose(ZoneId.getAvailableZoneIds()).arbitrary().map(ZoneId::of);
    }

    /**
     * If you ask for the full range type, you may
     * be given a time at the edge of the allowed
     * values, and eg adding one day could fail...
     * The reasonnable range is +-10.000 years
     */
    public enum RangeType {
        Full,
        Reasonnable
    }

    public static Arbitrary<LocalDateTime> localDateTime(RangeType rangeType) {
        LocalDateTime rangeStart = rangeType == RangeType.Full
            ? LocalDateTime.MIN : LocalDateTime.of(-10000, 1, 1, 0, 0);
        long minEpochSecond = rangeStart.toEpochSecond(ZoneOffset.UTC);
        LocalDateTime rangeEnd = rangeType == RangeType.Full
            ? LocalDateTime.MAX : LocalDateTime.of(10001, 1, 1, 0, 0);
        long maxEpochSecond = rangeEnd.toEpochSecond(ZoneOffset.UTC);
        return Gen.choose(minEpochSecond, maxEpochSecond)
            .flatMap(sec -> Gen.choose(0, 1000000000)
                     .map(nano -> LocalDateTime.ofEpochSecond(sec, nano, ZoneOffset.UTC)))
            .arbitrary();
    }
}
