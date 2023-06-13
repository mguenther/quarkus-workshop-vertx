package workshop.quarkus.vertx.verticles;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import workshop.quarkus.vertx.stock.StockExchange;
import workshop.quarkus.vertx.stock.StockService;

public class StockVerticle extends AbstractVerticle {

    private final StockService service = new StockService(new StockExchange(500));

    private final Logger LOG = LoggerFactory.getLogger(StockVerticle.class);

    @Override
    public Uni<Void> asyncStart() {
        return Uni.createFrom().voidItem();
    }
}
