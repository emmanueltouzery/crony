package com.github.emmanueltouzery.crony;

import java.time.Year;
import java.time.ZonedDateTime;

import io.vavr.collection.HashMap;
import io.vavr.collection.Set;
import io.vavr.control.Validation;

/**
 * Part of the cron specification describing the day of the month.
 */
public class DayOfMonthSpec {

    /**
     * Value for the special cron constant "last day of month" (encoded in the
     * cron format with a 'L').
     * When you build a {@link DayOfMonthSpec} using {@link DayOfMonthSpec#build},
     * you must give a {@link io.vavr.collection.Set} of integers.
     * You can use this constant to specify the last day of month (note: most
     * cron implementations don't support this feature)
     */
    public static final int LAST_DAY_OF_MONTH = -1;

    /**
     * The days of the month at which the spec triggers.
     * Contains 1-31 and also {@link LAST_DAY_OF_MONTH}.
     * Empty means any day is accepted.
     */
    public final Set<Integer> monthDays;

    private DayOfMonthSpec(Set<Integer> monthDays) {
        this.monthDays = monthDays;
    }

    /**
     * Build a {@link DayOfMonthSpec} from the list of days of the month
     * that it matches.
     * @param monthDays Days of the months, (1-31), plus the special
     * value {@link LAST_DAY_OF_MONTH}, or empty set for 'accept all'.
     * @return a new {@link DayOfMonthSpec} or an error message.
     */
    public static Validation<String, DayOfMonthSpec> build(Set<Integer> monthDays) {
        if (monthDays.exists(m -> m < LAST_DAY_OF_MONTH || m > 31)) {
            return Validation.invalid("Invalid day of the month");
        }
        return Validation.valid(new DayOfMonthSpec(monthDays));
    }

    /*package*/ static Validation<String, DayOfMonthSpec> parse(String cronSpec) {
        return SpecItemParser
            .parseSpecItem(cronSpec, 31, HashMap.of("L", LAST_DAY_OF_MONTH))
            .flatMap(DayOfMonthSpec::build);
    }

    /*package*/ Set<String> daysOfMonthFormattedSet() {
        return monthDays.map(d -> d == LAST_DAY_OF_MONTH ? "L" : Integer.toString(d));
    }

    /*package*/ boolean isMatch(ZonedDateTime dateTime) {
        boolean isLeap = Year.isLeap(dateTime.getYear());
        if (monthDays.contains(LAST_DAY_OF_MONTH)
            && dateTime.getDayOfMonth() == dateTime.getMonth().length(isLeap)) {
            return true;
        }
        return monthDays.isEmpty() || monthDays.contains(dateTime.getDayOfMonth());
    }
}
