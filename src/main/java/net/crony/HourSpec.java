package net.crony;

import java.time.LocalDateTime;

import javaslang.collection.Set;
import javaslang.control.Validation;

public class HourSpec {

    public final Set<Integer> hours;

    private HourSpec(Set<Integer> hours) {
        this.hours = hours;
    }

    public static Validation<String, HourSpec> build(Set<Integer> hours) {
        if (hours.exists(m -> m < 0 || m > 23)) {
            return Validation.invalid("Some hours are out of range");
        }
        return Validation.valid(new HourSpec(hours));
    }

    public static Validation<String, HourSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 23).flatMap(HourSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return hours.isEmpty() || hours.contains(dateTime.getHour());
    }
}
