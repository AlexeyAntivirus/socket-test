package com.rx.filestore.handlers;

import com.rx.filestore.core.FileExchanger;
import com.rx.filestore.core.PageLoader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by jrunix on 1/6/17.
 */
public class DownloadFileRequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        PageLoader loader = PageLoader.getInstance();

        String downloadFailPage = loader.getPageCode("download-fail.html");

        String filePath = null;

        try {
            String queryString = httpExchange.getRequestURI().getQuery();

            filePath = FileExchanger.getInstance().getFilePath(
                    UUID.fromString(queryString.substring(queryString.indexOf("f=") + "f=".length())));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (filePath != null) {
            File requestedFile = new File(filePath);
            try(InputStream stream = Files.newInputStream(requestedFile.toPath())) {
                int streamByte;
                httpExchange.getResponseHeaders().put("Content-type",
                        Collections.singletonList("application/octet-stream"));
                httpExchange.getResponseHeaders().put("Content-Disposition",
                        Collections.singletonList("filename=" + requestedFile.getName()));
                httpExchange.sendResponseHeaders(200, stream.available());
                while ((streamByte = stream.read()) != -1) {
                    httpExchange.getResponseBody().write(streamByte);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            downloadFailPage = String.format(downloadFailPage, "File is not exists");
            httpExchange.sendResponseHeaders(404, downloadFailPage.length());
            httpExchange.getResponseBody().write(downloadFailPage.getBytes());
        }
        httpExchange.close();
    }
}
