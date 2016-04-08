package net.crony;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import javaslang.Function1;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.collection.Seq;
import javaslang.control.Option;
import javaslang.control.Try;
import javaslang.control.Validation;

public class DayOfWeekSpec {

    /**
     * empty means 'any'
     */
    public final Set<DayOfWeek> days;

    private DayOfWeekSpec(Set<DayOfWeek> days) {
        this.days = days;
    }

    public static Validation<String, DayOfWeekSpec> build(Set<DayOfWeek> days) {
        return Validation.valid(new DayOfWeekSpec(days));
    }

    public static Validation<String, DayOfWeekSpec> parse(String cronSpec) {
        Function1<Integer, Validation<String, DayOfWeek>> parseDow = item ->
            Javaslang.tryValidation(() -> DayOfWeek.of(item), "Invalid day of week");
        return SpecItemParser.parseSpecItem(cronSpec, 7)
            .flatMap(intSet -> Javaslang.sequenceS(intSet.map(parseDow)))
            .map(Seq::toSet)
            .flatMap(DayOfWeekSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return days.isEmpty() || days.contains(dateTime.getDayOfWeek());
    }
}
