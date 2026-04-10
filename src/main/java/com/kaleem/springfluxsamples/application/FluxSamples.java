package com.kaleem.springfluxsamples.application;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class FluxSamples {

    public Flux<String> justOperator() {
        return Flux.
                just("London", "New York").log();
    }

    public Flux<String> arrayOperator() {
        return Flux.
                fromArray(new String[]{"London", "New York"}).log();
    }

    public Flux<String> iterableOperator() {
        return Flux.
                fromIterable(List.of("London", "New York")).log();
    }

    public Flux<String> streamOperator() {
        return Flux.
                fromStream(Stream.of("London", "New York")).log();
    }

    public Flux<String> mapOperator() {
        return iterableOperator().
                map(String::toUpperCase).log();
    }

    public Flux<String> filterOperator() {
        return iterableOperator().
                filter(city -> city.equals("London")).log();
    }

    //async
    public Flux<String> flatMapOperator() {
        return Flux.just("Lon","don").
                flatMap(city -> Flux.fromArray(city.split(""))).
                delayElements(Duration.ofSeconds(1));
    }

    //sync
    public Flux<String> concatMapOperator() {
        return Flux.just("London").
                concatMap(city -> Flux.fromArray(city.split("")));
    }

    public Flux<String> transformOperator() {
        Function<Flux<String>, Flux<String>> transformData =
                (data) -> data.map(String::toUpperCase);

        return Flux.just("London").transform(transformData);
    }

    public Flux<String> defaultIfEmptyOperator() {
        return Flux.just("London").
                filter(city -> city.equals("New York")).
                defaultIfEmpty("Empty");
    }

    public Flux<String> switchIfEmptyOperator() {
        return Flux.just("London").
                filter(city -> city.equals("New York")).
                switchIfEmpty(Flux.just("Washington"));
    }

    //static
    public Flux<String> concatOperator() {
        return Flux.concat(Flux.just("London"), Mono.just("New York"));
    }

    //instance
    public Flux<String> concatWithOperator() {
        return Flux.just("London").
                concatWith(Flux.just("New York"));
    }



}
