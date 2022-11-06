package booking;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingSimulation extends Simulation {

    AtomicInteger counter = new AtomicInteger(0);

    ChainBuilder bookTravel =
        exec(session -> session.set("bookingId", counter.getAndIncrement()))
            .exec(
                http("Post booking form")
                    .post("/booking")
                    .formParam("destination", "Katowice-#{bookingId}")
                    .check(status().is(200))
            );

    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://localhost:8080");

    ScenarioBuilder travelBookingScenario = scenario("Book").exec(bookTravel);

    {
        setUp(
            travelBookingScenario.injectOpen(rampUsers(100).during(10))
        ).protocols(httpProtocol);
    }
}
