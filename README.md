# Crony

Crony is a library to deal with cron scheduler specifictions in java. It can parse cron specifications, calculate dates of execution, and also programmatically create cron specifications and save them back to cron format strings.

[Online Javadoc](http://emmanueltouzery.github.io/crony/apidocs/)

Crony is available on maven-central:

```xml
<dependency>
    <groupId>com.github.emmanueltouzery</groupId>
    <artifactId>crony</artifactId>
    <version>1.0.6</version>
</dependency>
```

Crony is licensed under the `MIT` license, and requires java8. It uses the java8 date time classes.

Crony relies a lot on [vavr](http://vavr.io/), and results are most of the time returned using vavr lists, sets and the vavr `Validation` construct. Please refer to the Vavr documentation. Crony also tries to avoid exceptions and instead use optionals and Validation constructs.
If you prefer to use a more classical java exception-based control flow, you can simply call `get()` on the `Validation` objects you get back, and it will throw a runtime exception if something went wrong (that way though you won't get the error message, use `getError()` for that).

Besides the standard `cron` format, crony supports also the nonstandard mechanism of putting `L` for the day of the month to specify the last day of the month.

## Code snippets

```java
Cron.parseCronString("0 8 * * 1");
```

```java
CronExecution.getNextExecutionDate(cron, date)
```

```java
Cron.build(
        HashSet.empty(),
        HashSet.of(1,2),
        HashSet.of(1, DayOfMonthSpec.LAST_DAY_OF_MONTH),
        HashSet.of(Month.JANUARY,Month.MARCH),
        HashSet.of(DayOfWeek.MONDAY, DayOfWeek.SUNDAY))
```

```java
cron.toCronString()
```

```java
cron.isMatch(datetime)
```
