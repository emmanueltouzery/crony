package com.github.emmanueltouzery.crony;

import io.vavr.Function1;
import io.vavr.CheckedFunction0;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;
import io.vavr.control.Validation;

/*package*/ class Vavr {

    private static <T, U> Function1<Try<U>, Validation<T, U>> tryToValidation(T left) {
        return tryValue ->
            Validation.fromEither(tryValue.toEither().mapLeft(x -> left));
    }

    public static <E, T> Validation<E,T> tryValidation(CheckedFunction0<? extends T> supplier, E left) {
        return Try.of(supplier).transform(Vavr.tryToValidation(left));
    }

    public static <T> Validation<String, Seq<T>> sequenceS(Traversable<Validation<String, T>> items) {
        Traversable<Validation<Seq<String>, T>> items2 = items.map(v -> v.mapError(List::of));
        return Validation.sequence(items2).mapError(l -> l.mkString(", "));
    }

    public static Validation<String,String[]> splitValidate(String str, String separator, int count) {
        String[] items = str.split(separator);
        if (items.length != count) {
            return Validation.invalid(String.format("Invalid range, expected %d items, got %d", count, items.length));
        }
        return Validation.valid(items);
    }

    public static Validation<String, Integer> validationParseInt(String value) {
        return tryValidation(() -> Integer.parseInt(value),
                             String.format("Error parsing %s as integer.", value));
    }
}
