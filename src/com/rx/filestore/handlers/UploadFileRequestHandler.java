package com.rx.filestore.handlers;

import com.rx.filestore.core.FileExchanger;
import com.rx.filestore.core.PageLoader;
import com.rx.filestore.core.RequestHandlingManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.nio.IntBuffer;
import java.util.*;

/**
 * Created by jrunix on 1/5/17.
 */
public class UploadFileRequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        PageLoader loader = PageLoader.getInstance();
        String uploadFailPageCode = loader.getPageCode("upload-fail.html");
        String uploadResultPageCode = loader.getPageCode("upload-result.html");

        try (InputStream requestBodyInputStream = httpExchange.getRequestBody()) {

            IntBuffer requestBodyBinary = IntBuffer.allocate(Integer.parseInt(
                    httpExchange.getRequestHeaders().get("Content-Length").get(0)));

            int bin;
            while ((bin = requestBodyInputStream.read()) != -1) {
                requestBodyBinary.put(bin);
            }

            RequestHandlingManager manager = RequestHandlingManager.getInstance(
                    httpExchange.getRequestHeaders(), requestBodyBinary.array());

            String userName = manager.extractUserName();
            String fileName = manager.extractFileName();
            int[] fileContent = manager.extractFileContent();

            UUID fileUUID = FileExchanger.getInstance()
                    .upload(fileName, fileContent);

            URI uri = URI.create("http://localhost:8080/download?f=" + fileUUID.toString());
            if (fileUUID == null) {
                uploadFailPageCode = String.format(uploadFailPageCode, "Problems with storing your file!");
                httpExchange.sendResponseHeaders(500, uploadFailPageCode.length());
                httpExchange.getResponseBody().write(uploadFailPageCode.getBytes());
                httpExchange.close();
            } else {
                uploadResultPageCode = String.format(uploadResultPageCode, userName, uri.toURL().toString());
                httpExchange.sendResponseHeaders(200, uploadResultPageCode.length());
                httpExchange.getResponseBody().write(uploadResultPageCode.getBytes());
                httpExchange.close();
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}
