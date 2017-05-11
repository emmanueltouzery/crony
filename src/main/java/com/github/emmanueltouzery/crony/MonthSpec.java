package com.github.emmanueltouzery.crony;

import java.time.Month;
import java.time.ZonedDateTime;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import io.vavr.control.Validation;
import io.vavr.control.Try;

/**
 * Part of the cron specification describing the month of the year.
 */
public class MonthSpec {

    /**
     * The months of the year at which the spec triggers.
     * Empty means any month is accepted.
     */
    public final Set<Month> months;

    private MonthSpec(Set<Month> months) {
        this.months = months;
    }

    /**
     * Build a {@link MonthSpec} from the list of months of the year
     * that it matches.
     * @param months Accepted months of the year, or empty set for 'accept all'
     * @return a new {@link MonthSpec} or an error message.
     */
    public static Validation<String, MonthSpec> build(Set<Month> months) {
        return Validation.valid(new MonthSpec(months));
    }

    private static final Map<String, Integer> monthMap = Array.of(
        "jan", "feb", "mar", "apr", "may", "jun",
        "jul", "aug", "sep", "oct", "nov", "dec")
        .zip(Stream.from(1)).toMap(Function1.identity());

    /*package*/ static Validation<String, MonthSpec> parse(String cronSpec) {
        Function1<Integer, Validation<String, Month>> parseMonth = item ->
            Try.of(() -> Month.of(item))
            .toValidation(String.format("Invalid month: %d", item));
        return SpecItemParser.parseSpecItem(cronSpec.toLowerCase(), 12, monthMap)
            .flatMap(intSet -> Vavr.sequenceS(intSet.map(parseMonth)))
            .map(Seq::toSet)
            .flatMap(MonthSpec::build);
    }

    /*package*/ Set<String> monthsFormattedSet() {
        return months
            .map(month -> Array.of(Month.values()).indexOf(month)+1)
            .map(Object::toString);
    }

    /*package*/ boolean isMatch(ZonedDateTime dateTime) {
        return months.isEmpty() || months.contains(dateTime.getMonth());
    }
}
