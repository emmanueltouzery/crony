package com.github.emmanueltouzery.crony;

import java.time.Duration;
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
    public void nextExecutionDateOther() {
        assertEquals(
            ZonedDateTime.of(2014, 4, 22, 10, 0, 0, 0, ZoneId.of("UTC")),
            CronExecution.getNextExecutionDate(
                Cron.parseCronString("0 10 * * 1,2,3,4,5,6,7").get(),
                ZonedDateTime.of(2014, 4, 21, 11, 12, 0, 382, ZoneId.of("UTC"))));
    }

    @Test
    public void previousExecutionDatePastLess1h() {
        assertEquals(
            ZonedDateTime.of(2014, 4, 21, 10, 15, 0, 0, ZoneId.of("UTC")),
            CronExecution.getPreviousExecutionDate(
                Cron.parseCronString("15 10 * * 1,2,3,4,5,6,7").get(),
                ZonedDateTime.of(2014, 4, 21, 11, 12, 0, 382, ZoneId.of("UTC"))));
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
        assertEquals(Duration.ofHours(1), CronExecution.gapToClosestExecution(
                         Cron.parseCronString("0 6,8 * * 1").get(),
                         ZonedDateTime.of(2014, 12, 1, 5, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void gapToClosestBefore() {
        assertEquals(Duration.ofMinutes(30).plusSeconds(12).plusNanos(10), CronExecution.gapToClosestExecution(
                         Cron.parseCronString("0 6,8 * * 1").get(),
                         ZonedDateTime.of(2014, 12, 1, 6, 30, 12, 10, ZoneId.of("UTC"))));
    }

    @Test
    public void gapToClosestRightOnDate() {
        assertEquals(Duration.ZERO, CronExecution.gapToClosestExecution(
                         Cron.parseCronString("0 6,8 * * 1").get(),
                         ZonedDateTime.of(2014, 12, 1, 6, 0, 0, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void gapToClosestOnItExceptMillis() {
        assertEquals(Duration.ofMillis(355), CronExecution.gapToClosestExecution(
                         Cron.parseCronString("0 7 * * 1,2,3,4,5,6,7").get(),
                         ZonedDateTime.parse("2016-04-21T07:00:00.355-07:00[America/Los_Angeles]")));
    }

    @Test
    public void nextExecutionDateNonZeroSecond() {
        assertEquals(
            ZonedDateTime.of(2014, 12, 1, 8, 0, 0, 0, ZoneId.of("UTC")),
            CronExecution.getNextExecutionDate(
                Cron.parseCronString("0 8 * * 1").get(),
                ZonedDateTime.of(2014, 11, 30, 0, 0, 12, 0, ZoneId.of("UTC"))));
    }

    @Test
    public void nextExecutionDateThereExceptForSeconds() {
        assertEquals(
            ZonedDateTime.of(2014, 12, 8, 8, 0, 0, 0, ZoneId.of("UTC")),
            CronExecution.getNextExecutionDate(
                Cron.parseCronString("0 8 * * 1").get(),
                ZonedDateTime.of(2014, 12, 1, 8, 0, 12, 0, ZoneId.of("UTC"))));
    }
}
