package net.crony;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

import javaslang.Function1;
import javaslang.collection.Array;
import javaslang.collection.Seq;
import javaslang.collection.Set;
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
            Javaslang.tryValidation(() -> item == 0 ? DayOfWeek.SUNDAY : DayOfWeek.of(item),
                                    String.format("Invalid day of week: %d", item));
        return SpecItemParser.parseSpecItem(cronSpec, 7)
            .flatMap(intSet -> Javaslang.sequenceS(intSet.map(parseDow)))
            .map(Seq::toSet)
            .flatMap(DayOfWeekSpec::build);
    }

    public Set<Integer> daysOfWeekIntSet() {
        return days.map(day -> Array.of(DayOfWeek.values()).indexOf(day)+1);
    }

    public boolean isMatch(ZonedDateTime dateTime) {
        return days.isEmpty() || days.contains(dateTime.getDayOfWeek());
    }
}
