package net.crony;

import java.time.LocalDateTime;
import java.time.Month;

import javaslang.Function1;
import javaslang.collection.Seq;
import javaslang.collection.Set;
import javaslang.control.Option;
import javaslang.control.Try;

public class MonthSpec {

    public final Set<Month> months;

    private MonthSpec(Set<Month> months) {
        this.months = months;
    }

    public static Option<MonthSpec> build(Set<Month> months) {
        return Option.of(new MonthSpec(months));
    }

    public static Option<MonthSpec> parse(String cronSpec) {
        Function1<Integer, Option<Month>> parseMonth = item -> Try.of(() -> Month.of(item)).getOption();
        return SpecItemParser.parseSpecItem(cronSpec, 12)
            .flatMap(intSet -> Option.sequence(intSet.map(parseMonth)))
            .map(Seq::toSet)
            .flatMap(MonthSpec::build);
    }

    public boolean isMatch(LocalDateTime dateTime) {
        return months.isEmpty() || months.contains(dateTime.getMonth());
    }
}
