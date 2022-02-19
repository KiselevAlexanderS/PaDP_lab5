import akka.actor.ActorSystem;

import java.io.IOException;

public class AkkaApp {
    public static main(String[] args) throws IOException {
        ActorSystem system = ActorSystem.create("routes");
    }
}
