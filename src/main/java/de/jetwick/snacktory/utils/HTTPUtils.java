package de.jetwick.snacktory.utils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Created by admin- on 19/7/17.
 */
final public class HTTPUtils {

    static final private PoolingHttpClientConnectionManager connectionManager;

    private HTTPUtils() {
    }

    static {
        // @Todo: Get it from config
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
    }

    public static PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }
}
