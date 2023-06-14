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

## Task 2.1 

We have the vertx-object, so we just have to find the consume method and define a message handler (the code for the illegal trade is 1:1 the same, just a different consumer address and maybe log string)

```java 
vertx.eventBus().consumer("trade.valid", message -> {
    LOG.info("Received trade message: " + message.body());
});
```


## Task 2.2

We have to send the received items reactively on the RoutingContext. The code looks like this (again, the code for the illegal trades is pretty much c&p)

```java 
vertx.eventBus().consumer("trade.valid", message -> {
    LOG.info("Received trade message: " + message.body());
    Uni.createFrom().item(message.body())
        .onItem().transformToUni(msg -> context.response().write("data: " + msg + "\n").replaceWith(msg))
        .subscribe()
        .with(
            msg -> LOG.info("Sent message to client: " + msg)
        );
});
```

## Task 3.1

If you see the trades distributed on all tabs (e.g. Trade 1 on Tab 2, Trade 2 on Tab 2, Trade 3 on Tab 3 etc) this means, that you didn't use the correct method to send your event via the event bus. We have to use publish to send the events to **all** subscribers.


## Task 3.2

If we close a tab the connection will be killed, but there is still the message consumer happily getting and trying to send the messages to the client(s).

In order to mitigate this we have to catch the failure-condition and close the consumer for good.

```java 
final MessageConsumer<String> consumer = vertx.eventBus().consumer("trade.valid");

consumer.handler(message -> {
    LOG.info("Received trade message: " + message.body());
    Uni.createFrom().item(message.body())
            .onItem().transformToUni(msg -> context.response().write("data: " + msg + "\n").replaceWith(msg))
            .subscribe()
            .with(
                    msg -> LOG.info("Sent message to client: " + msg),
                    failure -> consumer.unregister().subscribe().with(
                            result -> LOG.info("Successfully unsubscribed after error.")
                    )
            );
});
```