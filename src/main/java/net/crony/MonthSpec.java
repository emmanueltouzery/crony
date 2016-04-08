package net.crony;

import java.time.LocalDateTime;
import java.time.Month;

import javaslang.Function1;
import javaslang.collection.Array;
import javaslang.collection.Seq;
import javaslang.collection.Set;
import javaslang.control.Validation;

public class MonthSpec {

    public final Set<Month> months;

    private MonthSpec(Set<Month> months) {
        this.months = months;
    }

    public static Validation<String, MonthSpec> build(Set<Month> months) {
        return Validation.valid(new MonthSpec(months));
    }

    public static Validation<String, MonthSpec> parse(String cronSpec) {
        Function1<Integer, Validation<String, Month>> parseMonth = item ->
            Javaslang.tryValidation(() -> Month.of(item), "Invalid month");
        return SpecItemParser.parseSpecItem(cronSpec, 12)
            .flatMap(intSet -> Javaslang.sequenceS(intSet.map(parseMonth)))
            .map(Seq::toSet)
            .flatMap(MonthSpec::build);
    }

    public Set<Integer> monthsIntSet() {
        return months.map(month -> Array.of(Month.values()).indexOf(month)+1);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return months.isEmpty() || months.contains(dateTime.getMonth());
    }
}
