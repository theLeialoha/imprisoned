package dev.leialoha.imprisoned.utils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BiComponent<F, S> {

    public final F first;
    public final S second;

    public BiComponent(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public BiComponent<S, F> swap() {
        return new BiComponent<>(second, first);
    }

    public <T> BiComponent<T, S> mapFirst(Function<F, T> f) {
        return new BiComponent<>(f.apply(first), second);
    }

    public <T> BiComponent<F, T> mapSecond(Function<S, T> f) {
        return new BiComponent<>(first, f.apply(second));
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public static <F, S> Collector<BiComponent<F, S>, ?, Map<F, S>> toMap() {
        return Collectors.toMap(BiComponent::getFirst, BiComponent::getSecond);
    }

}
