package com.github.emmanueltouzery.crony;

import java.time.DayOfWeek;
import java.time.Month;

import io.vavr.collection.HashSet;
import io.vavr.control.Validation;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class FormatTest {

    @Test
    public void simpleFormat() {
        Validation<String, Cron> cronV = Cron.build(
            HashSet.empty(),
            HashSet.of(1,2),
            HashSet.of(1, DayOfMonthSpec.LAST_DAY_OF_MONTH),
            HashSet.of(Month.JANUARY,Month.MARCH),
            HashSet.of(DayOfWeek.MONDAY, DayOfWeek.SUNDAY));
        assertTrue(cronV.isValid());
        assertEquals("* 1,2 L,1 1,3 1,7", cronV.get().toCronString());
    }
}
