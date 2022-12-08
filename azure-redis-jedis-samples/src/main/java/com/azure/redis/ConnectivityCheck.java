package com.azure.redis;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

public class ConnectivityCheck
{
    public static void main( String[] args )
    {

        if(args.length < 2) {
            System.out.println("Please pass the cache hostname and key");
        }

        String cacheHostname = args[0];
        String cachekey = args[1];

        boolean useSsl = true;

        // Connect to the Azure Cache for Redis over the TLS/SSL port using the key.
        Jedis jedis = new Jedis(cacheHostname, 6380, DefaultJedisClientConfig.builder()
                .password(cachekey)
                .ssl(useSsl)
                .build());

        // Perform cache operations using the cache connection object...

        // Simple PING command
        System.out.println( "\nCache Command  : Ping" );
        System.out.println( "Cache Response : " + jedis.ping());

        // Simple get and put of integral data types into the cache
        System.out.println( "\nCache Command  : GET Message" );
        System.out.println( "Cache Response : " + jedis.get("Message"));

        System.out.println( "\nCache Command  : SET Message" );
        System.out.println( "Cache Response : " + jedis.set("Message", "Hello! The cache is working from Java!"));

        // Demonstrate "SET Message" executed as expected...
        System.out.println( "\nCache Command  : GET Message" );
        System.out.println( "Cache Response : " + jedis.get("Message"));

        // Get the client list, useful to see if connection list is growing...
        System.out.println( "\nCache Command  : CLIENT LIST" );
        System.out.println( "Cache Response : " + jedis.clientList());

        jedis.close();
    }
}