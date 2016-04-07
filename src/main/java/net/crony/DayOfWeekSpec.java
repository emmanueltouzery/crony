package net.crony;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import javaslang.Function1;
import javaslang.collection.Set;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;

public class DayOfWeekSpec {

    /**
     * empty means 'any'
     */
    public final Set<DayOfWeek> days;

    private DayOfWeekSpec(Set<DayOfWeek> days) {
        this.days = days;
    }

    public static Option<DayOfWeekSpec> build(Set<DayOfWeek> days) {
        return Option.of(new DayOfWeekSpec(days));
    }

    public static Option<DayOfWeekSpec> parse(String cronSpec) {
        Function1<Integer, Option<DayOfWeek>> parseDow = item -> Try.of(() -> DayOfWeek.of(item)).getOption();
        return SpecItemParser.parseSpecItem(cronSpec, 7)
            .flatMap(intSet -> Option.sequence(intSet.map(parseDow)))
            .map(Seq::toSet)
            .flatMap(DayOfWeekSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return days.isEmpty() || days.contains(dateTime.getDayOfWeek());
    }
}
