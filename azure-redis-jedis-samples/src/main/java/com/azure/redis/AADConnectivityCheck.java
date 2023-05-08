package com.azure.redis;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

/*
Pre-requisites
    1. Register an application on the AD and extract the client-id, secret and tenant-id
    2. Click the "Data Access Configuration" blade on redis resource, register the user with service principal
*/
public class AADConnectivityCheck
{
    public static void main( String[] args )
    {
        //
        if(args.length < 5) {
            System.out.println("Please pass the cache hostname, userName, client-id, client-secret and tenant-id");
        }
        String cacheHostname = args[0];
        String userName = args[1];
        String clientId = args[2];
        String clientSecret = args[3];
        String tenantId = args[4];
        //
        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
        //
        // String oldScope = ""https://*.cacheinfra.windows.net:10225/appid/.default";
        String accessToken = clientSecretCredential
                .getToken(new TokenRequestContext().addScopes("acca5fbb-b7e4-4009-81f1-37e38fd66d78/.default")).block()
                .getToken();
        //
        boolean useSsl = true;
        // Connect to the Azure Cache for Redis over the TLS/SSL port using the key.
        Jedis jedis = new Jedis(cacheHostname, 6380, DefaultJedisClientConfig.builder()
                .password(accessToken)
                .user(userName)
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