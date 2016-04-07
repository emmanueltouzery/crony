package net.crony;

import java.time.LocalDateTime;

import javaslang.collection.Set;
import javaslang.control.Option;

import jdk.nashorn.internal.objects.annotations.SpecializedConstructor;

public class HourSpec {

    private Set<Integer> hours;

    private HourSpec(Set<Integer> hours) {
        this.hours = hours;
    }

    public static Option<HourSpec> build(Set<Integer> hours) {
        if (hours.exists(m -> m < 0 || m > 23)) {
            return Option.none();
        }
        return Option.of(new HourSpec(hours));
    }

    public static Option<HourSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 23).flatMap(HourSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return hours.isEmpty() || hours.contains(dateTime.getHour());
    }
}
