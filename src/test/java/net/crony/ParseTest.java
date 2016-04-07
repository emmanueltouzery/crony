package net.crony;

import java.time.DayOfWeek;
import java.time.Month;

import javaslang.collection.HashSet;
import javaslang.control.Option;
import javaslang.control.Validation;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class ParseTest
{
    @Test
    public void parseSimplePattern() {
        Option<Cron> parsed = Cron.parseCronString("0 8 * * 1").toOption();
        assertTrue(parsed.isDefined());
        assertEquals(HashSet.of(0), parsed.get().minSpec.minutes);
        assertEquals(HashSet.of(8), parsed.get().hourSpec.hours);
        assertEquals(HashSet.empty(), parsed.get().dayOfMonthSpec.monthDays);
        assertEquals(HashSet.empty(), parsed.get().monthSpec.months);
        assertEquals(HashSet.of(DayOfWeek.MONDAY), parsed.get().dayOfWeekSpec.days);
    }

    @Test
    public void parseMixedPattern() {
        Option<Cron> parsed = Cron.parseCronString("*/15 3,5,8 1,12 6 3-5").toOption();
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
        Validation<String, Cron> parsed = Cron.parseCronString("80 8 * * 1");
        assertTrue(parsed.isInvalid());
        assertEquals("Some minutes are out of range", parsed.getError());
    }

    @Test
    public void hourOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 28 * * 1");
        assertTrue(parsed.isInvalid());
        assertEquals("Some hours are out of range", parsed.getError());
    }

    @Test
    public void dayMonthOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 40 * 1");
        assertTrue(parsed.isInvalid());
        assertEquals("A month has an out of range value", parsed.getError());
    }

    @Test
    public void monthOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 * 18 1");
        assertTrue(parsed.isInvalid());
        assertEquals("Invalid month", parsed.getError());
    }

    @Test
        public void dayOfWeekOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 * * 8");
        assertTrue(parsed.isInvalid());
        assertEquals("Invalid day of week", parsed.getError());
    }
}
