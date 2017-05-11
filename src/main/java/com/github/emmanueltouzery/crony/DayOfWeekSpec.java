package com.github.emmanueltouzery.crony;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import io.vavr.control.Validation;

/**
 * Part of the cron specification describing the day of the week.
 */
public class DayOfWeekSpec {

    /**
     * The days of the week at which the spec triggers.
     * Contains 0-8 (sunday is both 0 and 8).
     * Empty means any day is accepted.
     */
    public final Set<DayOfWeek> days;

    private DayOfWeekSpec(Set<DayOfWeek> days) {
        this.days = days;
    }

    /**
     * Build a {@link DayOfWeekSpec} from the list of days of the week
     * that it matches.
     * @param days Accepted days of the week, or empty set for 'accept all'
     * @return a new {@link DayOfWeekSpec} or an error message.
     */
    public static Validation<String, DayOfWeekSpec> build(Set<DayOfWeek> days) {
        return Validation.valid(new DayOfWeekSpec(days));
    }

    private static final Map<String, Integer> dayMap = Array.of(
        "mon", "tue", "wed", "thu", "fri", "sat", "sun")
        .zip(Stream.from(1)).toMap(Function1.identity());

    /*package*/ static Validation<String, DayOfWeekSpec> parse(String cronSpec) {
        Function1<Integer, Validation<String, DayOfWeek>> parseDow = item ->
            Vavr.tryValidation(() -> item == 0 ? DayOfWeek.SUNDAY : DayOfWeek.of(item),
                                    String.format("Invalid day of week: %d", item));
        return SpecItemParser.parseSpecItem(cronSpec.toLowerCase(), 7, dayMap)
            .flatMap(intSet -> Vavr.sequenceS(intSet.map(parseDow)))
            .map(Seq::toSet)
            .flatMap(DayOfWeekSpec::build);
    }

    /*package*/ Set<String> daysOfWeekFormattedSet() {
        return days
            .map(day -> Array.of(DayOfWeek.values()).indexOf(day)+1)
            .map(Object::toString);
    }

    /*package*/ boolean isMatch(ZonedDateTime dateTime) {
        return days.isEmpty() || days.contains(dateTime.getDayOfWeek());
    }
}
