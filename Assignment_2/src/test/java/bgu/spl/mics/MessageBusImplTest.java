package bgu.spl.mics;

import bgu.spl.mics.application.messages.DemoBroadcast;
import bgu.spl.mics.application.messages.DemoEvent;
import bgu.spl.mics.application.services.TestMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    private MessageBus messageBus;
    private MicroService microService1;
    private MicroService microService2;
    private Broadcast broadcast;
    private Event<Boolean> event1;
    private Event<Boolean> event2;



    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new TestMicroservice();
        microService2 = new TestMicroservice();
        broadcast = new DemoBroadcast();
        event1 = new DemoEvent();
        event2 = new DemoEvent();
    }

    @AfterEach
    void tearDown() {
        messageBus.unregister(microService1);
        messageBus.unregister(microService2);

    }

    @Test
    void subscribeEvent() {
    }

    @Test
    void subscribeBroadcast() {
    }

    /**
     * checks whether future object which was created by some event was resolved correctly during complete().
     */
    @Test
    void complete() {
        regAndInit();
        Future<Boolean> future = messageBus.sendEvent(event1);
        messageBus.complete(event1, true);
        assertTrue(future.get());
    }

    /**
     * this test checks the correctness of the following methods:
     * register: will be called on regAndInit(). If register() fails, subscribeBroadcast() will throw an exception,and the test will fail.
     * subscribeBroadcast: will be called on regAndInit() in initialize(). if it fails,
     *                     the desired broadcast will not reach any Microservice, and the test will fail.
     * sendBroadcast: we test if the desired broadcast is fetched by each subscribing Microservice. if not, the test fails.
     */
    @Test
    void sendBroadcast() {
        try {
            regAndInit();
        } catch (Exception e){
            fail();
        }
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(broadcast, messageBus.awaitMessage(microService1));
            assertEquals(broadcast, messageBus.awaitMessage(microService2));
        } catch (Exception e) {
            fail();
        }
    }
    /**
     * this test checks the correctness of the following methods:
     * register: will be called on regAndInit(). If register() fails, subscribeEvent() will throw an exception,and the test will fail.
     * subscribeEvent: will be called on regAndInit() in initialize(). if it fails,
     *                     the desired Event will not reach any Microservice, and the test will fail.
     * sendEvent: we test if the desired event is fetched by one Microservice who subscribes to this event type. if not, the test fails.
     */
    @Test
    void sendEvent() {
        try {
            regAndInit();
        } catch (Exception e){
            fail();
        }
        Future<Boolean> future1 = messageBus.sendEvent(event1);
        Future<Boolean> future2 = messageBus.sendEvent(event2);
        try {
            // Assuming microService1 will get event1 & Assuming microService2 will get event2
            assertEquals(event1, messageBus.awaitMessage(microService1));
            assertEquals(event2, messageBus.awaitMessage(microService2));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
    }

    /**
     * this test checks that a Microservice is fetching the message from its queue,
     * (assuming the queue is not empty).
     */
    @Test
    void awaitMessage() {
        regAndInit();
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(broadcast, messageBus.awaitMessage(microService1));
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * private method used to test the functionality of register() and subscribeEvent/Broadcast().
     * @throws IllegalStateException
     */
    private void regAndInit() throws IllegalStateException{
        messageBus.register(microService1);
        messageBus.register(microService2);
        microService1.initialize();
        microService2.initialize();
    }
}