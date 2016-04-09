package net.crony;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.Month;

import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.control.Validation;

public class Cron
{
    public final MonthSpec monthSpec;
    public final DayOfMonthSpec dayOfMonthSpec;
    public final DayOfWeekSpec dayOfWeekSpec;
    public final HourSpec hourSpec;
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

    public String toCronString() {
        return List.of(minSpec.minutes,
                       hourSpec.hours,
                       dayOfMonthSpec.monthDays,
                       monthSpec.monthsIntSet(),
                       dayOfWeekSpec.daysOfWeekIntSet())
            .map(set -> {
                    if (set.isEmpty()) { return "*"; }
                    else { return set.mkString(","); } })
            .mkString(" ");
    }

    public boolean isMatch(ZonedDateTime dateTime) {
        return minSpec.isMatch(dateTime) &&
            hourSpec.isMatch(dateTime) &&
            dayOfMonthSpec.isMatch(dateTime) &&
            monthSpec.isMatch(dateTime) &&
            dayOfWeekSpec.isMatch(dateTime);
    }
}
