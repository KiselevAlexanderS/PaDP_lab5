import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AkkaApp {
    public static void main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("AkkaApp");
        ActorRef storeRef = system.actorOf(Props.create(Actor.class));

        final Http http = Http.get(system);
        final ActorMaterializer materia = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = Flow.of(HttpRequest.class)
                .map(request -> {
                    String url = request.getUri().query().get("testUrl").orElse("http://bmstu.ru");
                    Integer count = Integer.parseInt(request.getUri().query().get("count").orElse("1"));
                    return new GetResult(url,count);
                }).mapAsync(4, param ->
                    Patterns.ask(storeRef, param, Duration.ofMillis(4000))
                            .thenCompose(msg -> {
                                ResponseResult responseResult = (ResponseResult) msg;
                                if (responseResult.isEmpty())
                                    return CompletableFuture.completedFuture(responseResult);
    }
}
