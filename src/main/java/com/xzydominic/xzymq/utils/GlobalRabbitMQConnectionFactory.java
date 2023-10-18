package com.xzydominic.xzymq.utils;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import java.util.Arrays;


public class GlobalRabbitMQConnectionFactory {

    private static ConnectionFactory connectionFactory;

    private static CachingConnectionFactory cachingConnectionFactory;

    private static int efficientURI = 0;

    private static final String[] remoteServiceURI = new String[100];

    public static void setConnectionFactory(ConnectionFactory factory) {
        connectionFactory = factory;
    }

    public static void setCachingConnectionFactory(CachingConnectionFactory cachingFactory) {
        cachingConnectionFactory = cachingFactory;
    }

    public static void setRemoteServiceURI(String[] URI) {
        System.arraycopy(URI, 0, remoteServiceURI, 0, URI.length);
        setEfficientURI(URI.length);
    }

    public static void setEfficientURI(int value) {
        efficientURI = value;
    }

    public static int getEfficientURI() {
        return efficientURI;
    }

    public static String[] getRemoteServiceURI() {
        return remoteServiceURI;
    }

    @SuppressWarnings("all")
    public static void clearRemoteServiceURI() {
        Arrays.fill(remoteServiceURI, null);
    }

    public static ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            // 在这里创建 ConnectionFactory 并设置连接参数
            connectionFactory = new ConnectionFactory();
            System.out.println("Connect error: ConnectionFactory is null");
            // 其他连接属性设置
        }
        return connectionFactory;
    }

    public static CachingConnectionFactory getCachingConnectionFactory() {
        if (cachingConnectionFactory == null) {
            cachingConnectionFactory = new CachingConnectionFactory();
            System.out.println("Connect error: CachingConnectionFactory is null");
        }
        return cachingConnectionFactory;
    }

}
