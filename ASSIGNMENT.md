# Lab Assignment

## Task #1: Publish Trades via the Event Bus

As we know loose coupling and message driven communication are the core principles of a reactive landscape. Now that we have our **amazing** `StockService` we can make good use of if and publish all legal (and illegal) trades via the event bus!

In the project you'll find, that there are two verticles: the `WebVerticle`, which provides an HTTP Server (you see where this is going) and a `StockVerticle` which is somewhat empty, but already has our `StockService` wired. Both verticles run on a single thread, so we make sure that we'll notice quite quickly if we messed up.

1. Add two subscriptions to the `StockVerticle.asyncStart()` method - one for the legal trades and one for the illegal. Make sure that you receive the events when the application start (either by using log() or logging the items explicitly).

2. Every Verticle has access to the vertx object as a member variable. Use the vertx object to access the event bus and see which method fits best if we want to broadcast our (il)legal trades to other verticles. The target addresses should be `trade.legal` and `trade.illegal`. 

You can't produce complex object types because of the required interop between clustered nodes and different programming languages. So just serialize your objects to a JSON String via `Json.encode(object)`.

3. Take a look at what you've developed. Are we missing something? Are we prepared for the full lifecycle of a verticle? What could be improved?


## Task 2. Consume the Events in the WebVerticle

Now that we published our trades via the Event Bus we're ready to consume these events wherever we like. This could be a place like a persistence or analytics layer, or an API. We chose the latter and prepared a `WebVerticle` which starts a HTTP Server on port 8080.

We also already provided the necessary wiring for two endpoints: `/trades/legal` and `/trades/illegal`. Take a look at the WebVerticle `routes()` method to see where this is going: we're going to consume the messages and - in the end - publish them as hand rolled server-sent-events to all active clients.

After you've made yourself familiar with how things are set up take a look at the `TradeHandler` - this class is all you'll need to work on. To be precise all you'll need to do is to implement the two methods. The method will be called once for every client that calls the appropriate endpoint, so you can think of the contents of these methods as "session scoped".

1. Now it's time to consume some messages. We have the vertx object present, so again: get the eventBus and take a look at the API of the eventBus. Choose the appropriate method to subscribe to the channel from before and just log the Message to the console with the logger. You can trigger the consumption if you call the URL in your browser. Your browser most likely will show an error or just do nothing, but you should be able to see in the log that messages are being consumed.

2. Now it's time to publish something on the `RoutingContext` so our client will be able to see some events. The correct way for us (as we're hand-rolling our SSEs) is to do it like this: `context.response().write("data: " + msg + "\n")` - if you do this for every event in the right way you're almost there. There are obviously still a few caveats: you need to do this in a reactive way!

The write method on the response returns a `Uni<Void>` that needs to be consumed in order for the data to be sent. So in your message handler make sure to wrap everything in the correct Mutiny data structures and subscribe to the Uni(s) to begin the data stream. If you've done this correctly you should already be able to see the events in your browser (it might take a few seconds, but at least Firefox is able to show SSE content).

## Task 3: Clean Up, Validation and Optimization

If you've done everything perfectly -> great job and there will be nothing to do on Task 3 for you. But most likely you forgot a thing or two, es it's not really that intuitive.

1. Start everything up and open several tabs in your browser with the same URL, e.g. http://localhost:8080/trades/legal - are you seeing the same (and all) events in every tab? If not: what could be the reason? Try to fix it if necessary

2. Close one of the tabs and take a look at your log. Are you seeing errors? What could they mean and where do they come from? Do you have an idea how to mitigate this issue?


## That's it! You've done great!

You have completed all assignments. If you have any further questions or need clarification, please don't hesitate to reach out to us. We're here to help.