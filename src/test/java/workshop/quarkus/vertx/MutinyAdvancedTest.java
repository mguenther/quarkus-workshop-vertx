package workshop.quarkus.vertx;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MutinyAdvancedTest {


    @Test
    void task_2_1_fetchDataEverySecond() throws InterruptedException {
        Uni<String> dataUni = Uni.createFrom().item("Fetch Data");

        Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToUniAndMerge(tick -> dataUni.onItem().transform(data -> data + " " + tick))
                .subscribe().with(
                        item -> System.out.println("Received: " + item),
                        failure -> System.err.println("Failed with: " + failure)
                );

        TimeUnit.SECONDS.sleep(10);
    }


    @Test
    void task_2_2_retries() throws InterruptedException {
        Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToUniAndConcatenate(tick -> unreliableUpstreamRequest())
                .onFailure().retry().withBackOff(Duration.ofMillis(100)).atMost(5)
                .subscribe().with(
                        item -> System.out.println("Received item: " + item),
                        failure -> System.err.println("Failed with: " + failure)
                );


        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    void task_2_3_failureIsolation() throws InterruptedException {
        Multi<Integer> numbers = Multi.createFrom().range(1, 1000)
                .onItem().transformToUniAndConcatenate(n -> Uni.createFrom().item(n)
                        .onItem().invoke(v -> {
                            if (v == 7) {
                                throw new IllegalArgumentException("We don't like seven!");
                            }
                        })
                        .onFailure().recoverWithItem(4));

        numbers
                .subscribe().with(
                        item -> System.out.println("Received: " + item),
                        failure -> System.err.println("Failed with: " + failure)
                );

        TimeUnit.SECONDS.sleep(10);
    }

    private Uni<String> unreliableUpstreamRequest() {
        return Uni.createFrom().item(() -> {
            if (new Random().nextBoolean()) {
                return "Successful Data";
            } else {
                throw new RuntimeException("Temporary Failure");
            }
        });
    }
}
