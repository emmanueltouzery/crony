package net.crony;

import java.time.LocalDateTime;

import javaslang.collection.Set;
import javaslang.control.Option;

public class DayOfMonthSpec {

    private Set<Integer> monthDays;

    private DayOfMonthSpec(Set<Integer> monthDays) {
        this.monthDays = monthDays;
    }

    public static Option<DayOfMonthSpec> build(Set<Integer> monthDays) {
        if (monthDays.exists(m -> m < 0 || m > 31)) {
            return Option.none();
        }
        return Option.of(new DayOfMonthSpec(monthDays));
    }

    public static Option<DayOfMonthSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 31).flatMap(DayOfMonthSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return monthDays.isEmpty() || monthDays.contains(dateTime.getDayOfMonth());
    }
}
