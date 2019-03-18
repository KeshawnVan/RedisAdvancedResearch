package com.star.test;

import com.star.redis.manager.StarRedisClient;
import org.junit.jupiter.api.Test;

public class TestStar {

    @Test
    public void conn() {
        StarRedisClient client = new StarRedisClient("10.0.251.109", 6380);
        String set = client.set("hello", "redis");
        System.out.println(set);
        String hello = client.get("hello");
        System.out.println(hello);
    }
}
