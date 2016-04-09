package com.github.emmanueltouzery;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.function.Supplier;

import javaslang.collection.HashSet;
import javaslang.control.Validation;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParseTest
{
    private void assertTrueS(Supplier<String> msgSupplier, boolean test) {
        if (!test) {
            fail(msgSupplier.get());
        } else {
            assertTrue(true);
        }
    }

    @Test
    public void parseSimplePattern() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 * * 1");
        assertTrueS(() -> parsed.getError(), parsed.isValid());
        assertEquals(HashSet.of(0), parsed.get().minSpec.minutes);
        assertEquals(HashSet.of(8), parsed.get().hourSpec.hours);
        assertEquals(HashSet.empty(), parsed.get().dayOfMonthSpec.monthDays);
        assertEquals(HashSet.empty(), parsed.get().monthSpec.months);
        assertEquals(HashSet.of(DayOfWeek.MONDAY), parsed.get().dayOfWeekSpec.days);
    }

    @Test
    public void parseMixedPattern() {
        Validation<String, Cron> parsed = Cron.parseCronString("*/15 3,5,8 1,12 6 3-5");
        assertTrueS(() -> parsed.getError(), parsed.isValid());
        assertEquals(HashSet.of(0, 15, 30, 45), parsed.get().minSpec.minutes);
        assertEquals(HashSet.of(3, 5, 8), parsed.get().hourSpec.hours);
        assertEquals(HashSet.of(1, 12), parsed.get().dayOfMonthSpec.monthDays);
        assertEquals(HashSet.of(Month.JUNE), parsed.get().monthSpec.months);
        assertEquals(HashSet.of(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
                     parsed.get().dayOfWeekSpec.days);
    }

    @Test
    public void parseComplicated() {
        Validation<String, Cron> parsed = Cron.parseCronString("1,2,3,5,20-25,30-35,59 0-23/2 31,L 12 *");
        assertTrueS(() -> parsed.getError(), parsed.isValid());
        assertEquals(HashSet.of(1,2,3,5,20,21,22,23,24,25,30,31,32,33,34,35,59), parsed.get().minSpec.minutes);
        assertEquals(HashSet.of(0,2,4,6,8,10,12,14,16,18,20,22), parsed.get().hourSpec.hours);
        assertEquals(HashSet.of(31, DayOfMonthSpec.LAST_DAY_OF_MONTH), parsed.get().dayOfMonthSpec.monthDays);
        assertEquals(HashSet.of(Month.DECEMBER), parsed.get().monthSpec.months);
        assertEquals(HashSet.empty(), parsed.get().dayOfWeekSpec.days);
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
        assertEquals("Invalid hour", parsed.getError());
    }

    @Test
    public void dayMonthOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 40 * 1");
        assertTrue(parsed.isInvalid());
        assertEquals("Invalid day of the month", parsed.getError());
    }

    @Test
    public void monthOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 * 18 1");
        assertTrue(parsed.isInvalid());
        assertEquals("Invalid month: 18", parsed.getError());
    }

    @Test
        public void dayOfWeekOutOfRangeParseShouldFail() {
        Validation<String, Cron> parsed = Cron.parseCronString("0 8 * * 8");
        assertTrue(parsed.isInvalid());
        assertEquals("Invalid day of week: 8", parsed.getError());
    }
}
