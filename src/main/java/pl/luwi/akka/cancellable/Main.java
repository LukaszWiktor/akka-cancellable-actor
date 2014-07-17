package pl.luwi.akka.cancellable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

    private static final ActorSystem system = ActorSystem.create("System");
    
    private static Map<String, Runnable> actions = new HashMap<String, Runnable>();
    
    static {
        actions.put("start", new Runnable() {
            public void run() {
                ActorRef managerRef = system.actorOf(Props.create(ManagerActor.class), "Manager");
                managerRef.tell(Message.start, null);
            }
        });
        actions.put("cancel", new Runnable() {
            public void run() {
                system.actorSelection("/user/Manager").tell(Message.cancel, null);
            }
        });
        actions.put("quit", new Runnable() {
            public void run() {
                actions.get("cancel").run();
                system.shutdown();
                system.awaitTermination(Duration.create(15, TimeUnit.SECONDS));
                System.exit(0);
            }
        });
    }
    
    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("available commands: " + actions.keySet());
        while (true) {
            System.out.println("type your command:");
            String command = stdin.readLine();
            if (actions.containsKey(command)) {
                actions.get(command).run();
            } else {
                System.out.println("unknown command: " + command);
            }
        }
    }
}
