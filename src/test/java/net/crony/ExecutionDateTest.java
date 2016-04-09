package net.crony;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ExecutionDateTest {

    @Test
    public void nextExecutionDate() {
        assertEquals(
            ZonedDateTime.of(2014, 12, 1, 8, 0, 0, 0, ZoneId.of("UTC")),
            CronExecution.getNextExecutionDate(
                Cron.parseCronString("0 8 * * 1").get(),
                ZonedDateTime.of(2014, 11, 30, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void previousExecutionDate() {
        assertEquals(
            ZonedDateTime.of(2014, 11, 24, 8, 0, 0, 0, ZoneId.of("UTC")),
            CronExecution.getPreviousExecutionDate(
                Cron.parseCronString("0 8 * * 1").get(),
                ZonedDateTime.of(2014, 12, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }
}
