package net.crony;

import java.time.LocalDateTime;

import javaslang.collection.Set;
import javaslang.control.Validation;

public class MinSpec {

    public final Set<Integer> minutes;

    private MinSpec(Set<Integer> minutes) {
        this.minutes = minutes;
    }

    public static Validation<String, MinSpec> build(Set<Integer> minutes) {
        if (minutes.exists(m -> m < 0 || m > 59)) {
            return Validation.invalid("Some minutes are out of range");
        }
        return Validation.valid(new MinSpec(minutes));
    }

    public static Validation<String, MinSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 59).flatMap(MinSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return minutes.isEmpty() || minutes.contains(dateTime.getMinute());
    }
}
