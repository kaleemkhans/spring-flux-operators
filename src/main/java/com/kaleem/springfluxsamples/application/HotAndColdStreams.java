package com.kaleem.springfluxsamples.application;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotAndColdStreams {

    public void coldStreams() {
        // Each subscriber gets its own independent copy of the data starting from the beginning
        Flux<String> flux = Flux.just("London", "New York");

        // Starts a new "execution" of the stream
        flux.subscribe(System.out::println);
        // Starts another separate execution; both print everything
        flux.subscribe(System.out::println);
    }

    public void hotStreams() throws InterruptedException {
        // publish() converts a Cold Flux into a ConnectableFlux
        // It waits for a manual trigger before emitting anything to any subscriber
        var flux = Flux.just("London", "New York").publish();

        // These subscribers are "hooked up" but waiting
        flux.subscribe(val -> System.out.println("Pub 1: " + val));
        flux.subscribe(val -> System.out.println("Pub 2: " + val));

        // Data starts flowing to ALL current subscribers simultaneously
        flux.connect();

        //Second example
        // share() is an alias for .publish().refCount()
        // It turns hot as soon as the FIRST subscriber joins
        var flux2 = Flux.just("London", "New York").share();

        // First subscriber triggers the data flow immediately
        flux2.subscribe(val -> System.out.println("Share 1: " + val));

        // Since Flux.just is synchronous and fast, Share 2 likely joins
        // AFTER the data has already passed and will receive nothing
        flux2.subscribe(val -> System.out.println("Share 2: " + val));

        //Third example
        // 1. Create a cold interval (emits 0, 1, 2... every 500ms)
        // 2. share() makes it hot: it starts when the first subscriber joins
        Flux<Long> hotInterval = Flux.interval(Duration.ofMillis(500))
                .share();

        // T=0ms: Subscriber 1 joins and triggers emission
        hotInterval.subscribe(val -> System.out.println("Sub 1: " + val));

        // Wait 1.2 seconds (Stream has emitted 0 and 1)
        Thread.sleep(1200);

        // T=1200ms: Subscriber 2 joins.
        // Because it's HOT, Sub 2 misses 0 and 1 and starts from 2.
        hotInterval.subscribe(val -> System.out.println("Sub 2: " + val));

        // Keep alive to see both receiving 2, 3, 4...
        Thread.sleep(2000);

    }
}
