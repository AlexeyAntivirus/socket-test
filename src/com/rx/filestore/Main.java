package com.rx.filestore;

import com.rx.filestore.core.FileExchangerHttpServer;

/**
 * Created by jrunix on 1/8/17.
 */
public class Main {
    public static void main(String[] args) {
        FileExchangerHttpServer server = new FileExchangerHttpServer();
        server.start();
    }
}
