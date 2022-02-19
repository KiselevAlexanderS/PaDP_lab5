import akka.actor.AbstractActor;

import java.util.Map;
import java.util.HashMap;

public class Actor extends AbstractActor {
    private Map<String, Float> storage = new HashMap<>();
}
