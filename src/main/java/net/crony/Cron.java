package net.crony;

import javaslang.control.Option;
import javaslang.control.Validation;

public class Cron
{
    public final MonthSpec monthSpec;
    public final DayOfMonthSpec dayOfMonthSpec;
    public final DayOfWeekSpec dayOfWeekSpec;
    public final HourSpec hourSpec;
    public final MinSpec minSpec;

    public Cron(MinSpec minSpec,
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
}
