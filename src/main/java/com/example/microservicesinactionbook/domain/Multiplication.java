package com.example.microservicesinactionbook.domain;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public final class Multiplication {
    private final int factorA;
    private final int factorB;

    public Multiplication() {
        this(0, 0);
    }
}
