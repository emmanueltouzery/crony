package net.crony;

import javaslang.control.Option;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class CronTest
{
    @Test
    public void testApp()
    {
        Option<Cron> parsed = Cron.parseCronString("0 8 * * 1");
        assertTrue(parsed.isDefined());
    }
}
