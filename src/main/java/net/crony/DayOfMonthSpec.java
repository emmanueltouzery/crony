package net.crony;

import java.time.Year;
import java.time.ZonedDateTime;

import javaslang.collection.HashMap;
import javaslang.collection.Set;
import javaslang.control.Validation;

public class DayOfMonthSpec {

    public static final int LAST_DAY_OF_MONTH = -1;

    public final Set<Integer> monthDays;

    private DayOfMonthSpec(Set<Integer> monthDays) {
        this.monthDays = monthDays;
    }

    public static Validation<String, DayOfMonthSpec> build(Set<Integer> monthDays) {
        if (monthDays.exists(m -> m < LAST_DAY_OF_MONTH || m > 31)) {
            return Validation.invalid("Invalid day of the month");
        }
        return Validation.valid(new DayOfMonthSpec(monthDays));
    }

    public static Validation<String, DayOfMonthSpec> parse(String cronSpec) {
        return SpecItemParser
            .parseSpecItem(cronSpec, 31, HashMap.of("L", LAST_DAY_OF_MONTH))
            .flatMap(DayOfMonthSpec::build);
    }

    public Set<String> daysOfMonthFormattedSet() {
        return monthDays.map(d -> d == LAST_DAY_OF_MONTH ? "L" : Integer.toString(d));
    }

    public boolean isMatch(ZonedDateTime dateTime) {
        boolean isLeap = Year.isLeap(dateTime.getYear());
        if (monthDays.contains(LAST_DAY_OF_MONTH)
            && dateTime.getDayOfMonth() == dateTime.getMonth().length(isLeap)) {
            return true;
        }
        return monthDays.isEmpty() || monthDays.contains(dateTime.getDayOfMonth());
    }
}
