package com.star.test;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;
import reactor.core.publisher.Mono;

public class TestConnection {

    private static final String ADDRESS_109 = "redis://10.0.251.109:6380";

    private Config config = new Config();

    {
        config.useSingleServer().setAddress(ADDRESS_109).setDatabase(6);
    }

    private RedissonClient client = Redisson.create(config);

    @Test
    public void init() {
        RAtomicLong ackNum = client.getAtomicLong("ack_num");
        long currentTimeMillis = System.currentTimeMillis();
        long get = ackNum.addAndGet(1);
        System.out.println(System.currentTimeMillis() - currentTimeMillis);
    }

    @Test
    public void async() {
        RAtomicLong ackNum = client.getAtomicLong("ack_num");
        long currentTimeMillis = System.currentTimeMillis();
        RFuture<Long> longRFuture = ackNum.addAndGetAsync(1);
        longRFuture.whenComplete((res, ex) -> System.out.println(System.currentTimeMillis() - currentTimeMillis));
    }

    @Test
    public void reactor() {
        RedissonReactiveClient reactive = Redisson.createReactive(config);
        RAtomicLongReactive ackNum = reactive.getAtomicLong("ack_num");
        Mono<Long> longMono = ackNum.addAndGet(1);
        Long block = longMono.block();
        System.out.println(block);
    }

    @Test
    public void testTopic() {
        RTopic clientTopic = client.getTopic("star");
        clientTopic.addListener(String.class, new MessageListener<String>() {
            @Override
            public void onMessage(CharSequence channel, String msg) {
                System.out.printf("receive message : %s", msg);
                System.out.println();
            }
        });
        for (int i = 0; i < 1000; i++) {
            String s = "hello redisson " + i;
            long publish = clientTopic.publish(s);
        }
    }

    @Test
    public void lock() {
        RLock lock = client.getLock("star:lock");
        lock.lock();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }
}
