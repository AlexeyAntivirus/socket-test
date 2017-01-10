package com.rx.filestore.core;

import com.rx.filestore.handlers.DownloadFileRequestHandler;
import com.rx.filestore.handlers.PageHandler;
import com.rx.filestore.handlers.UploadFileRequestHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by jrunix on 1/8/17.
 */
public class FileExchangerHttpServer {

    private HttpServer server;

    public FileExchangerHttpServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(8080), 50);
            this.server.createContext("/upload", new PageHandler());
            this.server.createContext("/upload-result", new UploadFileRequestHandler());
            this.server.createContext("/download", new DownloadFileRequestHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.server.start();
    }
}
