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
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

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
                    return new GetResult(url, count);
                }).mapAsync(4, param ->
                        Patterns.ask(storeRef, param, Duration.ofMillis(4000))
                                .thenCompose(msg -> {
                                    ResponseResult responseResult = (ResponseResult) msg;
                                    if (responseResult.isEmpty())
                                        return CompletableFuture.completedFuture(responseResult);

                                    return Source.from(Collections.singletonList(param))
                                            .toMat(testSink(), Keep.right()).run(materia)
                                            .thenCompose(time -> CompletableFuture.completedFuture(
                                                    new ResponseResult(false, param.getUrl(), time/param.getCount())));
                                })).map(response -> {
                    if (!response.isEmpty()){
                        StoreResult storeResult = new StoreResult(response.getUrl(),response.getTime());
                        storeRef.tell(storeResult,ActorRef.noSender());
                    }
                    return HttpResponse.create().withStatus(200).withEntity(response.getTime() + "ms");
                });
        final comp
    }

    static final Sink<GetResult, CompletionStage<Long>> testSink() {
        return Flow.<GetResult>create()
                .mapConcat(msg -> Collections.nCopies(msg.getCount(),msg.getUrl()))
                .mapAsync(4, pair -> {
                    Long startTime = System.currentTimeMillis();
                    AsyncHttpClient asyncHttpClient = asyncHttpClient();
                    return asyncHttpClient
                            .prepareGet(pair)
                            .execute()
                            .toCompletableFuture()
                            .thenCompose(response ->
                                    CompletableFuture.completedFuture(System.currentTimeMillis() - startTime));
                }).toMat(Sink.fold(0L,Long::sum), Keep.right());
    }
}
