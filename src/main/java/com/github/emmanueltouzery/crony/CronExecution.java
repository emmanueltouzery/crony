package com.github.emmanueltouzery.crony;

import java.time.Duration;
import java.time.ZonedDateTime;

import io.vavr.collection.Stream;

/**
 * Retrieve information regarding the execution dates of
 * a cron specification.
 */
public class CronExecution {

    /**
     * Get an infinite stream of the next execution dates after a date you give
     * for a cron specification.
     * You can use {@link vavr.collection.Stream#take} to retrieve a limited
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
     * You can use {@link vavr.collection.Stream#take} to retrieve a limited
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
        if (cron.isMatch(base)) {
            return Duration.ZERO;
        }
        Duration duration1 = Duration.between(getPreviousExecutionDate(cron, base), base);
        Duration duration2 = Duration.between(base, getNextExecutionDate(cron, base));
        return (duration1.getSeconds() < duration2.getSeconds()) ? duration1 : duration2;
    }

    private static ZonedDateTime getNextMatchingDay(
        Cron cron, ZonedDateTime date, boolean forward) {
        ZonedDateTime curDate = date;
        while (!cron.isDayMatch(curDate)) {
            curDate = forward
                ? curDate.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
                : curDate.plusDays(-1).withHour(23).withMinute(59).withSecond(0).withNano(0);
        }
        return curDate;
    }

    private static ZonedDateTime testNextHours(
        Cron cron, ZonedDateTime date, boolean forward) {
        ZonedDateTime curDate = date;
        // make sure to stay within the same day
        while (!cron.hourSpec.isMatch(curDate)
               && curDate.toLocalDate().equals(date.toLocalDate())) {
            curDate = forward
                ? curDate.plusHours(1).withMinute(0).withSecond(0).withNano(0)
                : curDate.plusHours(-1).withMinute(59).withSecond(0).withNano(0);
        }
        return curDate;
    }

    private static ZonedDateTime testNextMinutes(
        Cron cron, ZonedDateTime date, boolean forward) {
        ZonedDateTime curDate = date;
        // make sure to stay within the same hour
        while (!(cron.minSpec.isMatch(curDate)
                 && curDate.getSecond() == 0
                 && curDate.getNano() == 0)
               && curDate.getHour() == date.getHour()) {
            if (!forward && curDate.getSecond() + curDate.getNano() > 0) {
                curDate = curDate.withSecond(0).withNano(0);
            } else {
                curDate = curDate.plusMinutes(forward ? 1 : -1).withSecond(0).withNano(0);
            }
        }
        return curDate;
    }

    private static ZonedDateTime getExecutionDateDirection(
        Cron cron, ZonedDateTime date, boolean forward) {

        // if we are right on an execution date right now, we'll return the next one.
        if (cron.isMatch(date)) {
            date = date.plusMinutes(forward ? 1 : -1).withSecond(0).withNano(0);
        }

        while (!cron.isMatch(date)) {
            // we make an effort not to just brute force add minutes one by one.
            // Notice the special case, when we make a stream for the next X ones,
            // we start from a minute previous/after the previous one.
            // Which means 99% of the time, the day will match,
            // the hour will match, just the minute will fail,
            // we add minutes at first, but then actually reset
            // back to adding days if needed.
            date = getNextMatchingDay(cron, date, forward);
            date = testNextHours(cron, date, forward);
            date = testNextMinutes(cron, date, forward);
        }
        return date;
    }
}
