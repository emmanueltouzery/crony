package net.crony;

import java.time.DayOfWeek;
import java.time.Month;

import javaslang.collection.HashSet;
import javaslang.control.Validation;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class FormatTest {

    @Test
    public void simpleFormat() {
        Validation<String, Cron> cronV = Validation.combine(
            MinSpec.build(HashSet.empty()),
            HourSpec.build(HashSet.of(1,2)),
            DayOfMonthSpec.build(HashSet.empty()),
            MonthSpec.build(HashSet.of(Month.JANUARY,Month.MARCH)),
            DayOfWeekSpec.build(HashSet.of(DayOfWeek.MONDAY, DayOfWeek.SUNDAY)))
            .ap(Cron::new).leftMap(l->l.mkString(", "));
        assertTrue(cronV.isValid());
        assertEquals("* 1,2 * 1,3 1,7", cronV.get().toCronString());
    }

}
