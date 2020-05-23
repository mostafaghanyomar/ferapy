package com.ferapy.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Pair<F, S> {

    private final F first;
    private final S second;

    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }

}
