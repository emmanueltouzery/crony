# Crony

Crony is a library to deal with cron scheduler specifictions in java. It can parse cron specifications, calculate dates of execution, and also create specifictions and save them back to cron format strings.

[Online Javadoc](http://emmanueltouzery.github.io/crony/apidocs/)

Crony relies a lot on [javaslang](http://javaslang.io/), and results are most of the time returned using javaslang lists, sets and the javaslang `Validation` construct. Please refer to the Javaslang documentation. Crony also tries to avoid exceptions and instead use optionals and `Validation` constructs.
If you prefer to use a more classical java exception-based control flow, you can simply call `get()` on the `Validation` objects you get back, and it will throw a runtime exception if something went wrong (that way though you won't get the error message, use `getError()` for that).

Besides the standard `cron` format, crony supports also the nonstandard mechanism of putting `L` for the day of the month to specify the last day of the month.

## Code snippets

    Cron.parseCronString("0 8 * * 1");
    CronExecution.getNextExecutionDate(cron, date)
    Cron.build(
            HashSet.empty(),
            HashSet.of(1,2),
            HashSet.of(1, DayOfMonthSpec.LAST_DAY_OF_MONTH),
            HashSet.of(Month.JANUARY,Month.MARCH),
            HashSet.of(DayOfWeek.MONDAY, DayOfWeek.SUNDAY))
    cron.toCronString()
    cron.isMatch(datetime)
