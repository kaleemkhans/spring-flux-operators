package com.kaleem.springfluxsamples.application;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MonoSamples {

    public Mono<String[]> flatMapOperator() {
        return Mono.just("London").
                flatMap(city -> Mono.just(city.split("")));
    }

    //mono to flux
    public Flux<String> flatMapManyOperator() {
        return Mono.just("London").
                flatMapMany(city -> Flux.just(city.split("")));
    }

}
