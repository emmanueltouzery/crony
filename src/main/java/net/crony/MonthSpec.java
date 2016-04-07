package net.crony;

import java.time.LocalDateTime;
import java.time.Month;

import javaslang.collection.Set;
import javaslang.control.Option;

public class MonthSpec {

    private Set<Month> months;

    private MonthSpec(Set<Month> months) {
        this.months = months;
    }

    public static Option<MonthSpec> build(Set<Month> months) {
        return Option.of(new MonthSpec(months));
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return months.isEmpty() || months.contains(dateTime.getMonth());
    }
}
