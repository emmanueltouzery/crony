package net.crony;

import javaslang.control.Option;

public class Cron
{
    public final MonthSpec monthSpec;
    public final DayOfMonthSpec dayOfMonthSpec;
    public final DayOfWeekSpec dayOfWeekSpec;
    public final HourSpec hourSpec;
    public final MinSpec minSpec;

    private Cron(MonthSpec monthSpec,
                 DayOfMonthSpec dayOfMonthSpec,
                 DayOfWeekSpec dayOfWeekSpec,
                 HourSpec hourSpec,
                 MinSpec minSpec) {
        this.monthSpec = monthSpec;
        this.dayOfMonthSpec = dayOfMonthSpec;
        this.dayOfWeekSpec = dayOfWeekSpec;
        this.hourSpec = hourSpec;
        this.minSpec = minSpec;
    }

    public static Option<Cron> parseCronString(String cronString) {
        String[] pieces = cronString.split(" ");
        if (pieces.length != 5) {
            return Option.none();
        }
        return MinSpec.parse(pieces[0])
            .flatMap(min -> HourSpec.parse(pieces[1])
                     .flatMap(hour -> DayOfMonthSpec.parse(pieces[2])
                              .flatMap(dayOfMonth -> MonthSpec.parse(pieces[3])
                                       .flatMap(month -> DayOfWeekSpec.parse(pieces[4])
                                                .map(dayOfWeek -> new Cron(month, dayOfMonth, dayOfWeek, hour, min))))));
    }
}
