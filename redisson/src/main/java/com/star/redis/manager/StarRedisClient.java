package com.star.redis.manager;

import java.io.IOException;
import java.net.Socket;

public class StarRedisClient {

    private Socket socket;

    public StarRedisClient(String host, int port) {
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String set(final String key, final String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("*3").append("\r\n");
        sb.append("$3").append("\r\n");
        sb.append("set").append("\r\n");
        sb.append("$").append(key.getBytes().length).append("\r\n");
        sb.append(key).append("\r\n");
        sb.append("$").append(value.getBytes().length).append("\r\n");
        sb.append(value).append("\r\n");
        return io(sb);
    }

    private String io(StringBuilder sb) {
        byte[] b;
        try {
            socket.getOutputStream().write(sb.toString().getBytes());
            b = new byte[2048];
            socket.getInputStream().read(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(b);
    }

    public String get(final String key){

        StringBuilder sb = new StringBuilder();
        sb.append("*2").append("\r\n"); // *表示数组 后面数字表示数组长度
        sb.append("$3").append("\r\n");
        sb.append("get").append("\r\n");
        sb.append("$").append(key.getBytes().length).append("\r\n"); // 美元符号表示字符串，后面的数字表示长度
        sb.append(key).append("\r\n");

        return io(sb);
    }
}
