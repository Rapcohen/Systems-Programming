package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 *
 *
 * note: after clarifications, we were told not to adress the situation of excuting the method 'unregister' in
 * the same time as subscribe/send methods. in a general application, this would cause a
 * problem - since unregister removes elements from all of the messagebus data sructures, and send/subscribe
 * methods read from the same data structures simultanusly. this could cause varuis exceptions and may couse
 * the program to crash. if we were to tackle this conflict, our idea was to use a Semaphore,
 * with a number of permits that is equal to INT.MAX_VALUE - that way,
 * each microservice can call send/subscribe with a use of a single permit, and only unregister will acquire INT.MAX_VALUE permits.
 * that way, unregister will only be executed when no other send/subscribe method is running, while send/subscribe methods can be executed
 * concurrently by many threads.
 */
public class MessageBusImpl implements MessageBus {

	private final Map<MicroService, LinkedBlockingQueue<Message>> messageQueues;
	private final Map<Class<? extends Event<?>>, Queue<MicroService>> eventMap;
	private final Map<Class<? extends Broadcast>, Set<MicroService>> broadMap;
	private final Map<Event<?>, Future> futureMap;

	private final Object roundRobinLock = new Object();
	private static class SingletonHolder{
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBus getInstance(){
		return SingletonHolder.instance;
	}

	private MessageBusImpl(){ // private constructor, as the class is a singleton.
		this.messageQueues = new ConcurrentHashMap<>();
		this.eventMap = new ConcurrentHashMap<>();
		this.broadMap = new ConcurrentHashMap<>();
		this.futureMap = new ConcurrentHashMap<>();
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!messageQueues.containsKey(m)){
			throw new IllegalStateException("MicroService " + m.getName() + " is not registered!");
		}

		synchronized (eventMap) { // Avoids duplicate put() method call for the same "type"
			if (!this.eventMap.containsKey(type)) {
				this.eventMap.put(type, new LinkedBlockingQueue<MicroService>());
			}
		}
		Queue<MicroService> microServiceQueue = this.eventMap.get(type);
		microServiceQueue.add(m);


	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!messageQueues.containsKey(m)){
			throw new IllegalStateException("MicroService " + m.getName() + " is not registered!");
		}
		synchronized (broadMap) { // Avoids duplicate put() method call for the same "type"
			if (!this.broadMap.containsKey(type)) {
				this.broadMap.put(type, ConcurrentHashMap.newKeySet());
			}
		}
		Set<MicroService> microServiceSet = this.broadMap.get(type);
		microServiceSet.add(m);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		futureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadMap.get(b.getClass()) != null){
			// Adds broadcast to all the relevant MicroService message-queues
			for(MicroService microService : broadMap.get(b.getClass())){
				Queue<Message> messageQueue = this.messageQueues.get(microService);
				messageQueue.add(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (roundRobinLock){ // Ensures correct execution of RoundRobin
			Queue<MicroService> eventQueue = eventMap.get(e.getClass());
			if (eventQueue != null){
				MicroService receiver = eventQueue.poll();
				if(receiver != null){
					Future<T> future = new Future<>();
					futureMap.put(e,future);
					messageQueues.get(receiver).add(e);
					eventQueue.add(receiver);
					return future;
				}
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		this.messageQueues.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		this.messageQueues.remove(m);

		for (Class<? extends Event> event : eventMap.keySet()){
			Queue<MicroService> queue = eventMap.get(event);
			queue.remove(m);
		}

		for (Class<? extends Broadcast> broadcast : broadMap.keySet()){
			Set<MicroService> set = broadMap.get(broadcast);
			set.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!messageQueues.containsKey(m)){
			throw new IllegalStateException("MicroService " + m.getName() + " is not registered!");
		}
		try {
			return messageQueues.get(m).take();
		} catch(InterruptedException e) {
			System.out.println(m.getName() + " was interrupted while waiting for a message");
			throw e;
		}
	}
}
