package pl.luwi.akka.cancellable;

import java.util.concurrent.TimeUnit;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class WorkerActor extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    
    private static Timeout cancelTimeout = new Timeout(Duration.create(1, TimeUnit.DAYS));
    
    @Override
    public void onReceive(Object msg) throws Exception {
        Future<?> shouldCancel = Patterns.ask(context().parent(), Message.shouldCancel, cancelTimeout);
        int i = 0;
        while (!shouldCancel.isCompleted()) {
            log.debug("produced {}", i++);
            Thread.sleep(1000);
        }
        log.debug("finished");
    }

}
