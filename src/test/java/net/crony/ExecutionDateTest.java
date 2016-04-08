package net.crony;

import java.time.LocalDateTime;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ExecutionDateTest {

    @Test
    public void nextExecutionDate() {
        assertEquals(
            LocalDateTime.of(2014, 12, 1, 8, 0),
            Cron.parseCronString("0 8 * * 1").get()
            .getNextExecutionDate(LocalDateTime.of(2014, 11, 30, 0, 0)));
    }
}
