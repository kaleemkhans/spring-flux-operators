package com.kaleem.springfluxsamples.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class FluxSamplesTest {

    @Autowired
    private FluxSamples fluxSamples;

    @Test
    void justOperatorTest() {
        StepVerifier.
                create(fluxSamples.justOperator()).
                expectNext("London", "New York").
                verifyComplete();
    }

    @Test
    void arrayOperatorTest() {
        StepVerifier.
                create(fluxSamples.arrayOperator()).
                expectNext("London", "New York").
                verifyComplete();
    }

    @Test
    void iterableOperatorTest() {
        StepVerifier.
                create(fluxSamples.iterableOperator()).
                expectNext("London", "New York").
                verifyComplete();
    }

    @Test
    void mapOperatorTest() {
        StepVerifier.
                create(fluxSamples.mapOperator()).
                expectNext("LONDON", "NEW YORK").
                verifyComplete();
    }

    @Test
    void filterOperatorTest() {
        StepVerifier.
                create(fluxSamples.filterOperator()).
                expectNext("London").
                verifyComplete();
    }

    @Test
    void flatMapOperatorTest() {
        StepVerifier.
                create(fluxSamples.flatMapOperator()).
                expectNext("L", "o", "n", "d", "o", "n").
                verifyComplete();

    }

}
