package net.crony;

import java.time.ZonedDateTime;

import javaslang.collection.Set;
import javaslang.control.Validation;

public class DayOfMonthSpec {

    public final Set<Integer> monthDays;

    private DayOfMonthSpec(Set<Integer> monthDays) {
        this.monthDays = monthDays;
    }

    public static Validation<String, DayOfMonthSpec> build(Set<Integer> monthDays) {
        if (monthDays.exists(m -> m < 0 || m > 31)) {
            return Validation.invalid("A month has an out of range value");
        }
        return Validation.valid(new DayOfMonthSpec(monthDays));
    }

    public static Validation<String, DayOfMonthSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 31).flatMap(DayOfMonthSpec::build);
    }

    public boolean isMatch(ZonedDateTime dateTime) {
        return monthDays.isEmpty() || monthDays.contains(dateTime.getDayOfMonth());
    }
}
