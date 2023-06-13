package workshop.quarkus.vertx.verticles.web;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.RoutingContext;

public class TradeHandler {

    private final Logger LOG = LoggerFactory.getLogger(TradeHandler.class);

    private final Vertx vertx;

    public TradeHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void validTrades(RoutingContext context) {
        context
                .response()
                .setChunked(true);
    }

    public void invalidTrades(RoutingContext context) {
        context
                .response()
                .setChunked(true);
    }
}
