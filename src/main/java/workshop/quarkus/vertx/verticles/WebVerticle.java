package workshop.quarkus.vertx.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.mutiny.ext.web.Router;
import workshop.quarkus.vertx.verticles.web.TradeHandler;


public class WebVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger(WebVerticle.class);


    @Override
    public Uni<Void> asyncStart() {
        return vertx.createHttpServer()
                .requestHandler(routes(new TradeHandler(vertx)))
                .listen(8080)
                .onItem().invoke(() -> LOG.info("See http://localhost:8080"))
                .onFailure().invoke(Throwable::printStackTrace)
                .replaceWithVoid();
    }

    private Router routes(TradeHandler handlers) {        // Create a Router
        Router router = Router.router(vertx);
        router.get("/trades/valid").produces("text/event-stream")
                .handler(handlers::validTrades);

        router.get("/trades/valid").produces("text/event-stream")
                .handler(handlers::invalidTrades);

        return router;
    }
}
