package net.crony;

import java.time.LocalDateTime;

import javaslang.Function1;

public class CronExecution {

    public static LocalDateTime getNextExecutionDate(Cron cron, LocalDateTime base) {
        return getExecutionDateDirection(cron, base, true);
    }

    public static LocalDateTime getPreviousExecutionDate(Cron cron, LocalDateTime base) {
        return getExecutionDateDirection(cron, base, false);
    }

    private static LocalDateTime getExecutionDateDirection(
        Cron cron, LocalDateTime date, boolean forward) {
        int increment = forward ? 1 : -1;
        Function1<LocalDateTime, LocalDateTime> newDayResetFwd =
            d -> d.withHour(0).withMinute(0);
        Function1<LocalDateTime, LocalDateTime> newDayResetBack =
            d -> d.withHour(23).withMinute(59);
        Function1<LocalDateTime, LocalDateTime> newDayReset =
            forward ? newDayResetFwd: newDayResetBack;

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

    private static boolean isDayMatch(Cron cron, LocalDateTime datetime) {
        return cron.dayOfMonthSpec.isMatch(datetime) &&
            cron.monthSpec.isMatch(datetime) &&
            cron.dayOfWeekSpec.isMatch(datetime);
    }
}
