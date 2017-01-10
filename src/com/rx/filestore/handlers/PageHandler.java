package com.rx.filestore.handlers;

import com.rx.filestore.core.PageLoader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by jrunix on 1/5/17.
 */
public class PageHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        byte[] pageCodeBytes = PageLoader.getInstance()
                .getPageCode(httpExchange.getRequestURI().getPath().substring(1) + ".html")
                .getBytes();
        OutputStream responseBodyOutputStream = httpExchange.getResponseBody();

        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, pageCodeBytes.length);
        responseBodyOutputStream.write(pageCodeBytes);
        responseBodyOutputStream.flush();
        responseBodyOutputStream.close();
        httpExchange.close();
    }
}
