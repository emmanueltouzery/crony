package net.crony;

import java.time.LocalDateTime;

import javaslang.collection.Set;
import javaslang.control.Option;

public class MinSpec {

    private Set<Integer> minutes;

    private MinSpec(Set<Integer> minutes) {
        this.minutes = minutes;
    }

    public static Option<MinSpec> build(Set<Integer> minutes) {
        if (minutes.exists(m -> m < 0 || m > 59)) {
            return Option.none();
        }
        return Option.of(new MinSpec(minutes));
    }

    public static Option<MinSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 59).flatMap(MinSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return minutes.isEmpty() || minutes.contains(dateTime.getMinute());
    }
}
