package com.github.emmanueltouzery.crony;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import io.vavr.collection.List;
import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;
import io.vavr.test.Property;

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

    @Test
    public void nextExecutionDateSameAsFirstOfStream() {
        Cron cron = Cron.parseCronString("0 8 * * 1").get();
        ZonedDateTime startDate = ZonedDateTime.of(2014, 12, 1, 8, 0, 12, 0, ZoneId.of("UTC"));
        assertEquals(
            CronExecution.getNextExecutionDates(cron, startDate).head(),
            CronExecution.getNextExecutionDate(cron, startDate));
    }

    @Test
    public void previousNextIsSymetrical() {
        Arbitrary<ZonedDateTime> dates = CronyArbitrary.zonedDateTime(
            CronyArbitrary.RangeType.Reasonnable);
        Arbitrary<Cron> crons = CronyArbitrary.cron();
        Property.def("previousExec(m) plus previousExec(n-1) on top of nextExec(m+n) on an execution date is a NOP")
            .forAll(dates, crons, Gen.choose(0, 100).arbitrary(), Gen.choose(1, 100).arbitrary())
            .suchThat((date, cron, m, n) -> {
                    ZonedDateTime init = CronExecution.getNextExecutionDate(cron, date);
                    ZonedDateTime next = CronExecution.getNextExecutionDates(cron, init).get(m+n);
                    ZonedDateTime previous1 = CronExecution.getPreviousExecutionDates(cron, next).get(m);
                    ZonedDateTime previous2 = CronExecution.getPreviousExecutionDates(cron, previous1).get(n-1);
                    return previous2.equals(init);
                })
            .check()
            .assertIsSatisfied();
    }

    @Test
    public void nextExecutionDateIsOnExecDate() {
        Arbitrary<ZonedDateTime> dates = CronyArbitrary.zonedDateTime(
            CronyArbitrary.RangeType.Reasonnable);
        Arbitrary<Cron> crons = CronyArbitrary.cron();

        Property.def("nextExec() and previousExec() are on an execution date")
            .forAll(dates, crons)
            .suchThat((date, cron) -> {
                    ZonedDateTime next = CronExecution.getNextExecutionDate(cron, date);
                    ZonedDateTime previous = CronExecution.getPreviousExecutionDate(cron, date);
                    return CronExecution.gapToClosestExecution(cron, next).equals(Duration.ZERO) &&
                        CronExecution.gapToClosestExecution(cron, previous).equals(Duration.ZERO);
                })
            .check()
            .assertIsSatisfied();
    }
}
