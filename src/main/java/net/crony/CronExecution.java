package net.crony;

import java.time.ZonedDateTime;

import javaslang.Function1;
import javaslang.collection.Stream;

public class CronExecution {

    public static Stream<ZonedDateTime> getNextExecutionDates(Cron cron, ZonedDateTime base) {
        return Stream
            .iterate(base, d -> getNextExecutionDate(cron, d))
            .drop(1);
    }

    public static ZonedDateTime getNextExecutionDate(Cron cron, ZonedDateTime base) {
        return getExecutionDateDirection(cron, base, true);
    }

    public static Stream<ZonedDateTime> getPreviousExecutionDates(Cron cron, ZonedDateTime base) {
        return Stream
            .iterate(base, d -> getPreviousExecutionDate(cron, d))
            .drop(1);
    }

    public static ZonedDateTime getPreviousExecutionDate(Cron cron, ZonedDateTime base) {
        return getExecutionDateDirection(cron, base, false);
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
