package com.kaleem.springfluxsamples.application;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class FluxSamples {

    // --- CREATION OPERATORS ---

    /** Emits provided elements directly; .log() prints signal events (onNext, onComplete, etc.) */
    public Flux<String> justOperator() {
        return Flux.just("London", "New York").log();
    }

    /** Creates a Flux from an existing Array */
    public Flux<String> arrayOperator() {
        return Flux.fromArray(new String[]{"London", "New York"}).log();
    }

    /** Creates a Flux from any Iterable source like a List or Set */
    public Flux<String> iterableOperator() {
        return Flux.fromIterable(List.of("London", "New York")).log();
    }

    /** Creates a Flux from a Java 8 Stream */
    public Flux<String> streamOperator() {
        return Flux.fromStream(Stream.of("London", "New York")).log();
    }

    // --- TRANSFORMATION & FILTERING ---

    /** 1-to-1 transformation: converts each emitted string to uppercase */
    public Flux<String> mapOperator() {
        return iterableOperator().map(String::toUpperCase).log();
    }

    /** Evaluates a boolean condition; only "London" passes through */
    public Flux<String> filterOperator() {
        return iterableOperator().filter(city -> city.equals("London")).log();
    }

    /**
     * Asynchronous 1-to-N: Flattens inner publishers.
     * Elements may interleave (overlap) due to the 1-second delay.
     */
    public Flux<String> flatMapOperator() {
        return Flux.just("Lon","don").
                flatMap(city -> Flux.fromArray(city.split(""))).
                delayElements(Duration.ofSeconds(1));
    }

    /**
     * Synchronous/Sequential 1-to-N: Maintains the order of the source
     * by waiting for one inner publisher to complete before starting the next.
     */
    public Flux<String> concatMapOperator() {
        return Flux.just("London").
                concatMap(city -> Flux.fromArray(city.split("")));
    }

    /** Externalizes logic: useful for reusing a chain of operators across different streams */
    public Flux<String> transformOperator() {
        Function<Flux<String>, Flux<String>> transformData =
                (data) -> data.map(String::toUpperCase);

        return Flux.just("London").transform(transformData);
    }

    // --- EMPTY HANDLING ---

    /** If the filter results in an empty stream, emit the literal value "Empty" */
    public Flux<String> defaultIfEmptyOperator() {
        return Flux.just("London").
                filter(city -> city.equals("New York")).
                defaultIfEmpty("Empty");
    }

    /** If the stream is empty, switch to an entirely different Publisher (Flux/Mono) */
    public Flux<String> switchIfEmptyOperator() {
        return Flux.just("London").
                filter(city -> city.equals("New York")).
                switchIfEmpty(Flux.just("Washington"));
    }

    // --- COMBINATION OPERATORS ---

    /** Static method to join two publishers; finishes first one before starting the second */
    public Flux<String> concatOperator() {
        return Flux.concat(Flux.just("London"), Mono.just("New York"));
    }

    /** Instance method equivalent to concat; appends "New York" after "London" completes */
    public Flux<String> concatWithOperator() {
        return Flux.just("London").
                concatWith(Flux.just("New York"));
    }

    /** Eagerly subscribes to all sources; elements are emitted as they arrive (interleaved) */
    public Flux<String> mergeOperator() {
        var flux1 = Flux.just("London","New York").delayElements(Duration.ofMillis(1));
        var flux2 = Flux.just("Washington","Delhi").delayElements(Duration.ofMillis(1));
        return Flux.merge(flux1, flux2);
    }

    /** Instance method equivalent to merge; combines both streams eagerly */
    public Flux<String> mergeWithOperator() {
        var flux1 = Flux.just("London","New York").delayElements(Duration.ofMillis(1));
        var flux2 = Flux.just("Washington","Delhi").delayElements(Duration.ofMillis(1));
        return flux1.mergeWith(flux2);
    }

    /** Eagerly subscribes but buffers results to ensure output matches the order of sources */
    public Flux<String> mergeSequentialOperator() {
        var flux1 = Flux.just("London","New York").delayElements(Duration.ofMillis(1));
        var flux2 = Flux.just("Washington","Delhi").delayElements(Duration.ofMillis(1));
        return Flux.mergeSequential(flux1, flux2);
    }

    /** Pairs items from two streams together based on index (e.g., 1st with 1st) */
    public Flux<String> zipOperator() {
        var flux1 = Flux.just("London","New York");
        var flux2 = Flux.just("Washington","Delhi");
        return Flux.zip(flux1, flux2, (first, second) -> first + "-" + second);
    }

    /** Instance method equivalent to zip; pairs "London" with "Washington" */
    public Flux<String> zipWithOperator() {
        var flux1 = Flux.just("London","New York");
        var flux2 = Flux.just("Washington","Delhi");
        return flux1.zipWith(flux2, (first, second) -> first + "-" + second);
    }

    // --- SIDE EFFECTS & ERROR HANDLING ---

    /** Peak into the stream lifecycle without modifying the data */
    public Flux<String> doOnOperator(){
        return Flux.just("London","New York").
                doOnNext(System.out::println). // Triggers for every item
                        doOnEach(signal -> {           // Access the Signal object (Next, Error, Complete)
                    if (signal.isOnNext()) {
                        System.out.println("Item Emitted: " + signal.get());
                    } else if (signal.isOnComplete()) {
                        System.out.println("Stream Finished!");
                    } else if (signal.isOnError()) {
                        System.err.println("Error: " + Objects.requireNonNull(signal.getThrowable()).getMessage());
                    }
                }).
                doOnComplete(() -> System.out.println("Done"));
    }

    /** Swallows the error and emits a fallback value ("New York") before terminating normally */
    public Flux<String> onErrorReturnOperator(){
        return Flux.just("London").
                concatWith(Flux.error(new RuntimeException())).
                onErrorReturn("New York");
    }

    /** Drops the failing element/signal and continues with the rest of the stream */
    public Flux<String> onErrorContinueOperator(){
        return Flux.just("London").
                concatWith(Flux.error(new RuntimeException())).
                concatWith(Flux.just("New York")).
                onErrorContinue((exp, value) -> {
                    System.out.println("Error Continue: " + exp);
                    System.out.println("Error Continue: " + value);
                });
    }

}
