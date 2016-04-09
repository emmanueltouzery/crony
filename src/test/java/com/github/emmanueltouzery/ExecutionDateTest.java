package com.github.emmanueltouzery;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import javaslang.collection.List;

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
    public void nextExecutionDates() {
        List<ZonedDateTime> expected = List.of(
            ZonedDateTime.of(2014, 12, 1, 6, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.of(2014, 12, 1, 8, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.of(2014, 12, 8, 6, 0, 0, 0, ZoneId.of("UTC")));
        assertEquals(
            expected,
            CronExecution.getNextExecutionDates(
                Cron.parseCronString("0 6,8 * * 1").get(),
                ZonedDateTime.of(2014, 11, 30, 0, 0, 0, 0, ZoneId.of("UTC"))).take(3).toList());
    }

    @Test
    public void previousExecutionDate() {
        assertEquals(
            ZonedDateTime.of(2014, 11, 24, 8, 0, 0, 0, ZoneId.of("UTC")),
            CronExecution.getPreviousExecutionDate(
                Cron.parseCronString("0 8 * * 1").get(),
                ZonedDateTime.of(2014, 12, 1, 0, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void previousExecutionDates() {
        List<ZonedDateTime> expected = List.of(
            ZonedDateTime.of(2014, 11, 24, 8, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.of(2014, 11, 24, 6, 0, 0, 0, ZoneId.of("UTC")),
            ZonedDateTime.of(2014, 11, 17, 8, 0, 0, 0, ZoneId.of("UTC")));
        assertEquals(
            expected,
            CronExecution.getPreviousExecutionDates(
                Cron.parseCronString("0 6,8 * * 1").get(),
                ZonedDateTime.of(2014, 12, 1, 6, 0, 0, 0, ZoneId.of("UTC"))).take(3).toList());
    }

    @Test
    public void gapToClosest() {
        assertEquals(7200, CronExecution.gapToClosestExecution(
            Cron.parseCronString("0 6,8 * * 1").get(),
            ZonedDateTime.of(2014, 12, 1, 6, 0, 0, 0, ZoneId.of("UTC"))).getSeconds());
    }
}
