package net.crony;

import java.time.DayOfWeek;
import java.time.Month;

import javaslang.collection.HashSet;
import javaslang.control.Option;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class CronTest
{
    @Test
    public void parseSimplePattern() {
        Option<Cron> parsed = Cron.parseCronString("0 8 * * 1");
        assertTrue(parsed.isDefined());
        assertEquals(HashSet.of(0), parsed.get().minSpec.minutes);
        assertEquals(HashSet.of(8), parsed.get().hourSpec.hours);
        assertEquals(HashSet.empty(), parsed.get().dayOfMonthSpec.monthDays);
        assertEquals(HashSet.empty(), parsed.get().monthSpec.months);
        assertEquals(HashSet.of(DayOfWeek.MONDAY), parsed.get().dayOfWeekSpec.days);
    }

    @Test
    public void parseMixedPattern() {
        Option<Cron> parsed = Cron.parseCronString("*/15 3,5,8 1,12 6 3-5");
        assertTrue(parsed.isDefined());
        assertEquals(HashSet.of(0, 15, 30, 45), parsed.get().minSpec.minutes);
        assertEquals(HashSet.of(3, 5, 8), parsed.get().hourSpec.hours);
        assertEquals(HashSet.of(1, 12), parsed.get().dayOfMonthSpec.monthDays);
        assertEquals(HashSet.of(Month.JUNE), parsed.get().monthSpec.months);
        assertEquals(HashSet.of(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                     parsed.get().dayOfWeekSpec.days);
    }

    @Test
    public void minOutOfRangeParseShouldFail() {
        Option<Cron> parsed = Cron.parseCronString("80 8 * * 1");
        assertFalse(parsed.isDefined());
    }

    @Test
    public void hourOutOfRangeParseShouldFail() {
        Option<Cron> parsed = Cron.parseCronString("0 28 * * 1");
        assertFalse(parsed.isDefined());
    }

    @Test
    public void dayMonthOutOfRangeParseShouldFail() {
        Option<Cron> parsed = Cron.parseCronString("0 8 40 * 1");
        assertFalse(parsed.isDefined());
    }

    @Test
    public void monthOutOfRangeParseShouldFail() {
        Option<Cron> parsed = Cron.parseCronString("0 8 * 18 1");
        assertFalse(parsed.isDefined());
    }

    @Test
        public void dayOfWeekOutOfRangeParseShouldFail() {
        Option<Cron> parsed = Cron.parseCronString("0 8 * * 8");
        assertFalse(parsed.isDefined());
    }
}
