package pl.luwi.akka.cancellable;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ManagerActor extends UntypedActor {
   
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    
    private List<ActorRef> _cancelListeners = new ArrayList<ActorRef>();
    
    public ManagerActor() {
        for (int i = 0; i < 3; i++) {
            ActorRef workerRef = context().actorOf(Props.create(WorkerActor.class), "Worker-" + i);
            workerRef.tell(Message.start, getSelf());    
        }
        
    }
    
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg.equals(Message.shouldCancel)) {
            _cancelListeners.add(getSender());
            log.debug("registered cancel listener {}", getSender());
        } else if (msg.equals(Message.cancel)) {
            for (ActorRef cancelListener: _cancelListeners) {
                cancelListener.tell(Message.cancel, cancelListener);
                log.debug("cancel passed to {}", cancelListener);
            }
            _cancelListeners = new ArrayList<ActorRef>();
        }
    }

}
