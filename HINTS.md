# Hints

**Spoiler Alert**

We encourage you to work on the assignment yourself or together with your peers. However, situations may present themselves to you where you're stuck on a specific assignment. Thus, this document contains a couple of hints/solutions that ought to guide you through a specific task of the lab assignment.

In any case, don't hesitate to talk to us if you're stuck on a given problem!

## Task 1.1

We've done this plenty times before, we can subscribe to the Multis of the StockService like shown below. As soon as the verticle starts we'll be able to see the events in the log.

```java
service.validatedTrades()
        .subscribe()
        .with(
            item -> LOG.info("Valid trade: " + item)
        );

service.illegalTrades()
        .subscribe()
        .with(
            item -> LOG.info("Illegal trade: " + item)
            );
```

## Task 1.2 

We add a side-effect to the subscription, which is publishing a (serialized) event. We can do this via the `onItem().invoke()` method, which is good enough for this solution. The whole code now should something like below. Take note that we must use `publish()` instead of `send()` - as we have potentially more than one subscriber (one subscriber per user session). We don't necessarily need any special delivery options and set the address as described in the exercise.

```java
service.validatedTrades()
        .onItem().invoke(item -> vertx.eventBus().publish("trade.valid", Json.encode(item)))
        .subscribe()
        .with(
                item -> LOG.info("Valid trade: " + item)
        );

service.illegalTrades()
        .onItem().invoke(item -> vertx.eventBus().publish("trade.illegal", Json.encode(item)))
        .subscribe()
        .with(
                item -> LOG.info("Illegal trade: " + item)
        );
```

## Task 1.3 

The issue is, that the verticle can be un-deployed, but that won't stop the subscriptions. The subscribe returns an object of the type cancellable - we should save these objects and make sure to close them in the `asyncStop()` method.