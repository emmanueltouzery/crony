package com.github.emmanueltouzery.crony;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.Month;

import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.control.Validation;

/**
 * The cron class represents a cron specification.
 */
public class Cron
{
    /**
     * the month part of the cron specification
     */
    public final MonthSpec monthSpec;

    /**
     * the day of month part of the cron specification
     */
    public final DayOfMonthSpec dayOfMonthSpec;

    /**
     * the day of week part of the cron specification
     */
    public final DayOfWeekSpec dayOfWeekSpec;

    /**
     * the hour of day part of the cron specification
     */
    public final HourSpec hourSpec;

    /**
     * the minute of day part of the cron specification
     */
    public final MinSpec minSpec;

    private Cron(MinSpec minSpec,
                HourSpec hourSpec,
                DayOfMonthSpec dayOfMonthSpec,
                MonthSpec monthSpec,
                DayOfWeekSpec dayOfWeekSpec) {
        this.monthSpec = monthSpec;
        this.dayOfMonthSpec = dayOfMonthSpec;
        this.dayOfWeekSpec = dayOfWeekSpec;
        this.hourSpec = hourSpec;
        this.minSpec = minSpec;
    }

    /**
     * Programmatically reate a Cron specification.
     * @param minutes minutes of the hour (0-59)
     * @param hours hours of the day (0-23)
     * @param daysOfMonth the days of the month (1-31). To specify the
     *        last day of the month, use {@link DayOfMonthSpec#LAST_DAY_OF_MONTH}
     * @param months months of the year
     * @param daysOfWeek the days of the week
     * @return a Cron object or an error message
     */
    public static Validation<String, Cron> build(
        Set<Integer> minutes, Set<Integer> hours,
        Set<Integer> daysOfMonth, Set<Month> months,
        Set<DayOfWeek> daysOfWeek) {
        return Validation.combine(
            MinSpec.build(minutes),
            HourSpec.build(hours),
            DayOfMonthSpec.build(daysOfMonth),
            MonthSpec.build(months),
            DayOfWeekSpec.build(daysOfWeek))
            .ap(Cron::new).leftMap(l -> l.mkString(", "));
    }

    /**
     * Build a Cron specification from a cron string specification
     * @param cronString a cron string specification to parse
     * @return a Cron object or an error message
     */
    public static Validation<String, Cron> parseCronString(String cronString) {
        return Javaslang.splitValidate(cronString, " ", 5)
            .flatMap(pieces ->
                     Validation.combine(
                         MinSpec.parse(pieces[0]),
                         HourSpec.parse(pieces[1]),
                         DayOfMonthSpec.parse(pieces[2]),
                         MonthSpec.parse(pieces[3]),
                         DayOfWeekSpec.parse(pieces[4]))
                     .ap(Cron::new).leftMap(l -> l.mkString(", ")));
    }

    /**
     * Generate a cron string specification from the cron specification.
     * @return a cron string specification as used in crontab
     */
    public String toCronString() {
        return List.of(minSpec.minutes,
                       hourSpec.hours,
                       dayOfMonthSpec.daysOfMonthFormattedSet(),
                       monthSpec.monthsFormattedSet(),
                       dayOfWeekSpec.daysOfWeekFormattedSet())
            .map(set -> set.isEmpty() ? "*" : set.mkString(","))
            .mkString(" ");
    }

    /**
     * Will return true if the date time given is a match
     * for this cron specification (in other words if
     * execution would trigger at that exact date and time)
     * @param dateTime the date time to test
     * @return true if the cron would execute at that dateTime
     */
    public boolean isMatch(ZonedDateTime dateTime) {
        return dateTime.getSecond() == 0 &&
            dateTime.getNano() == 0 &&
            minSpec.isMatch(dateTime) &&
            hourSpec.isMatch(dateTime) &&
            isDayMatch(dateTime);
    }

    /*package*/ boolean isDayMatch(ZonedDateTime datetime) {
        // The day of a command's execution can be specified in the following two fields
        // — 'day of month', and 'day of week'.  If both fields are restricted
        // (i.e., do not contain the "*" character),  the
        // command will be run when either field matches the current time.
        boolean bothDayMonthWeekSpecified =
            !dayOfMonthSpec.monthDays.isEmpty() && !dayOfWeekSpec.days.isEmpty();
        boolean isDayMatch = bothDayMonthWeekSpecified
            ? dayOfMonthSpec.isMatch(datetime) || dayOfWeekSpec.isMatch(datetime)
            : dayOfMonthSpec.isMatch(datetime) && dayOfWeekSpec.isMatch(datetime);
        return monthSpec.isMatch(datetime) && isDayMatch;
    }
}
