package net.crony;

import javaslang.Function1;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Traversable;
import javaslang.control.Try;
import javaslang.control.Validation;

public class Javaslang {

    // TODO: another one that gets the message from the exception.
    public static <T, U> Function1<Try<U>, Validation<T, U>> tryToValidation(T left) {
        return tryValue -> {
            if (tryValue.isSuccess()) {
                return Validation.valid(tryValue.get());
            } else {
                return Validation.invalid(left);
            }
        };
    }

    public static <T> Validation<String, Seq<T>> sequenceS(Traversable<Validation<String, T>> items) {
        Traversable<Validation<List<String>, T>> items2 = items.map(v -> v.leftMap(List::of));
        return Validation.sequence(items2).leftMap(l -> l.mkString(", "));
    }
}
