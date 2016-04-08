package net.crony;

import javaslang.Function1;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Traversable;
import javaslang.control.Try;
import javaslang.control.Validation;

public class Javaslang {

    private static <T, U> Function1<Try<U>, Validation<T, U>> tryToValidation(T left) {
        return tryValue -> {
            if (tryValue.isSuccess()) {
                return Validation.valid(tryValue.get());
            } else {
                return Validation.invalid(left);
            }
        };
    }

    public static <E, T> Validation<E,T> tryValidation(Try.CheckedSupplier<? extends T> supplier, E left) {
        return Try.of(supplier).transform(Javaslang.tryToValidation(left));
    }

    public static <T> Validation<String, Seq<T>> sequenceS(Traversable<Validation<String, T>> items) {
        Traversable<Validation<List<String>, T>> items2 = items.map(v -> v.leftMap(List::of));
        return Validation.sequence(items2).leftMap(l -> l.mkString(", "));
    }
}
