package com.github.emmanueltouzery.crony;

import java.time.ZonedDateTime;

import javaslang.collection.Set;
import javaslang.control.Validation;

/**
 * Part of the cron specification describing the minute of the hour.
 */
public class MinSpec {

    /**
     * The minutes of the hour at which the spec triggers.
     * Contains 0-59.
     * Empty means any minute is accepted.
     */
    public final Set<Integer> minutes;

    private MinSpec(Set<Integer> minutes) {
        this.minutes = minutes;
    }

    /**
     * Build a {@link MinSpec} from the list of minutes of the hour
     * that it matches.
     * @param minutes Accepted minutes of the hour (0-59), or empty set for 'accept all'
     * @return a new {@link MinSpec} or an error message.
     */
    public static Validation<String, MinSpec> build(Set<Integer> minutes) {
        if (minutes.exists(m -> m < 0 || m > 59)) {
            return Validation.invalid("Some minutes are out of range");
        }
        return Validation.valid(new MinSpec(minutes));
    }

    /*package*/ static Validation<String, MinSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 59).flatMap(MinSpec::build);
    }

    /*package*/ boolean isMatch(ZonedDateTime dateTime) {
        return minutes.isEmpty() || minutes.contains(dateTime.getMinute());
    }
}
