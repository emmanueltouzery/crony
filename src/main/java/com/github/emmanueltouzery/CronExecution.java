package com.github.emmanueltouzery;

import java.time.Duration;
import java.time.ZonedDateTime;

import javaslang.Function1;
import javaslang.collection.Stream;

/**
 * Retrieve information regarding the execution dates of
 * a cron specification.
 */
public class CronExecution {

    /**
     * Get an infinite stream of the next execution dates after a date you give
     * for a cron specification.
     * You can use {@link javaslang.collection.Stream#take} to retrieve a limited
     * number of elements.
     * @param cron the cron specification
     * @param base the date to start from
     * @return an infinite stream of upcoming execution dates.
     */
    public static Stream<ZonedDateTime> getNextExecutionDates(Cron cron, ZonedDateTime base) {
        return Stream
            .iterate(base, d -> getNextExecutionDate(cron, d))
            .drop(1);
    }

    /**
     * Get the next execution date for a cron specification after the date you give
     * @param cron the cron specification
     * @param base the date to start from
     * @return the next execution date
     */
    public static ZonedDateTime getNextExecutionDate(Cron cron, ZonedDateTime base) {
        return getExecutionDateDirection(cron, base, true);
    }

    /**
     * Get an infinite stream of the previous execution dates before a date you give
     * for a cron specification.
     * You can use {@link javaslang.collection.Stream#take} to retrieve a limited
     * number of elements.
     * @param cron the cron specification
     * @param base the date to start from
     * @return an infinite stream of previous execution dates.
     */
    public static Stream<ZonedDateTime> getPreviousExecutionDates(Cron cron, ZonedDateTime base) {
        return Stream
            .iterate(base, d -> getPreviousExecutionDate(cron, d))
            .drop(1);
    }

    /**
     * Get the previous execution date for a cron specification before the date you give
     * @param cron the cron specification
     * @param base the date to start from
     * @return the previous execution date
     */
    public static ZonedDateTime getPreviousExecutionDate(Cron cron, ZonedDateTime base) {
        return getExecutionDateDirection(cron, base, false);
    }

    /**
     * Calculates the gap between the date you give and the closest execution date
     * for the cron specification you supply (either before or after the date you give).
     * The calculated gap will always be positive. So for instance if the previous
     * execution is 2 minutes before the date you give, and the next one is 2 hours
     * after, the duration returned will be two minutes (not minus two minutes).
     * @param cron the cron specification
     * @param base the date to consider
     * @return the gap between the date you give and the closest execution time
     */
    public static Duration gapToClosestExecution(Cron cron, ZonedDateTime base) {
        Duration duration1 = Duration.between(getPreviousExecutionDate(cron, base), base);
        Duration duration2 = Duration.between(base, getNextExecutionDate(cron, base));
        return (duration1.getSeconds() < duration2.getSeconds()) ? duration1 : duration2;
    }

    private static ZonedDateTime getExecutionDateDirection(
        Cron cron, ZonedDateTime date, boolean forward) {
        int increment = forward ? 1 : -1;
        Function1<ZonedDateTime, ZonedDateTime> newDayResetFwd =
            d -> d.withHour(0).withMinute(0);
        Function1<ZonedDateTime, ZonedDateTime> newDayResetBack =
            d -> d.withHour(23).withMinute(59);
        Function1<ZonedDateTime, ZonedDateTime> newDayReset =
            forward ? newDayResetFwd: newDayResetBack;

        // if we are right on an execution date right now, we'll return the next one.
        date = date.plusMinutes(increment);

        // first find the next matching day
        while (!isDayMatch(cron, date)) {
            date = newDayReset.apply(date.plusDays(increment));
        }
        // now find the next matching hour
        while (!cron.hourSpec.isMatch(date)) {
            date = date.plusHours(increment);
        }
        // and now the minute
        while (!cron.isMatch(date)) {
            date = date.plusMinutes(increment);
        }
        return date;
    }

    private static boolean isDayMatch(Cron cron, ZonedDateTime datetime) {
        return cron.dayOfMonthSpec.isMatch(datetime) &&
            cron.monthSpec.isMatch(datetime) &&
            cron.dayOfWeekSpec.isMatch(datetime);
    }
}
