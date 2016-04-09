package com.github.emmanueltouzery;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javaslang.collection.HashSet;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class MatchTest {

    private DayOfMonthSpec getDayOfMonthSpec() {
        return DayOfMonthSpec.build(
            HashSet.of(5,DayOfMonthSpec.LAST_DAY_OF_MONTH)).get();
    }

    @Test
    public void wrongDayOfWeekShouldNotMatch() {
        assertFalse(getDayOfMonthSpec().isMatch(ZonedDateTime.of(2016,1,4,0,0,0,0,ZoneId.of("UTC"))));
    }

    @Test
    public void simpleDayOfWeek() {
        assertTrue(getDayOfMonthSpec().isMatch(ZonedDateTime.of(2016,1,5,0,0,0,0,ZoneId.of("UTC"))));
    }

    @Test
    public void lastDayOfWeek() {
        assertTrue(getDayOfMonthSpec().isMatch(ZonedDateTime.of(2016,1,31,0,0,0,0,ZoneId.of("UTC"))));
    }
}
