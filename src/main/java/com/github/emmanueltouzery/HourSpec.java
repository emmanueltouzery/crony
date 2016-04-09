package com.github.emmanueltouzery;

import java.time.ZonedDateTime;

import javaslang.collection.Set;
import javaslang.control.Validation;

/**
 * Part of the cron specification describing the hour of the day.
 */
public class HourSpec {

    /**
     * The hours of the day at which the spec triggers.
     * Contains 0-23.
     * Empty means any hour is accepted.
     */
    public final Set<Integer> hours;

    private HourSpec(Set<Integer> hours) {
        this.hours = hours;
    }

    /**
     * Build a {@link HourSpec} from the list of hours of the day
     * that it matches.
     * @param hours Accepted hours of the day (0-23), or empty set for 'accept all'
     * @return a new {@link HourSpec} or an error message.
     */
    public static Validation<String, HourSpec> build(Set<Integer> hours) {
        if (hours.exists(m -> m < 0 || m > 23)) {
            return Validation.invalid("Invalid hour");
        }
        return Validation.valid(new HourSpec(hours));
    }

    /*package*/ static Validation<String, HourSpec> parse(String cronSpec) {
        return SpecItemParser.parseSpecItem(cronSpec, 23).flatMap(HourSpec::build);
    }

    /*package*/ boolean isMatch(ZonedDateTime dateTime) {
        return hours.isEmpty() || hours.contains(dateTime.getHour());
    }
}
