package de.jetwick.snacktory.utils;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Helper class for making HTTP API Calls
 *
 * @author Abhishek Mulay
 */
final public class HTTPUtils {

    static final private PoolingHttpClientConnectionManager connectionManager;

    static {
        // @Todo: Get it from config
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
    }

    private HTTPUtils() {
    }

    /**
     * Creates a HTTP Client based on simple username, password authentication
     *
     * @param userName {@link String}
     * @param password {@link String}
     * @return {@link HttpClient}
     */
    public static HttpClient getHttpClient(String userName, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}
