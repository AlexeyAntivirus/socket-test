package com.rx.filestore.core;

import com.sun.net.httpserver.Headers;

import java.util.List;
import java.util.UUID;

/**
 * Created by jrunix on 1/6/17.
 */
public class RequestHandlingManager {
    private static final RequestHandlingManager INSTANCE = new RequestHandlingManager();

    private Headers headers;

    private int[] body;

    private RequestHandlingManager() {}

    public static RequestHandlingManager getInstance(Headers headers, int[] body) {
        INSTANCE.body = body;
        INSTANCE.headers = headers;

        return INSTANCE;
    }



    public String extractUserName() {
        String requestBody = new String(this.body, 0, this.body.length);
        String boundary = this.extractBoundaryValue();

        int indexOfContentDispositionOfUserNameStart =
                requestBody.indexOf("Content-Disposition");
        int indexOfContentDispositionOfUserNameEnd =
                requestBody.indexOf("\r\n", indexOfContentDispositionOfUserNameStart);
        int indexOfBoundaryOfUserName = requestBody.indexOf(
                boundary, indexOfContentDispositionOfUserNameEnd) - 2;

        String rawUserName = requestBody.substring(
                indexOfContentDispositionOfUserNameEnd, indexOfBoundaryOfUserName);

        return rawUserName.replace("\r\n", "");
    }

    public String extractFileName() {
        String requestBody = new String(this.body, 0, this.body.length);

        int indexOfContentDispositionOfFileStart =
                requestBody.lastIndexOf("Content-Disposition");
        int indexOfContentDispositionOfFileEnd =
                requestBody.indexOf("\r\n", indexOfContentDispositionOfFileStart);

        String contentDispositionLineOfFile = requestBody.substring(
                indexOfContentDispositionOfFileStart,
                indexOfContentDispositionOfFileEnd);
        String fileNameKey = "filename=\"";
        String fileName = contentDispositionLineOfFile.substring(
                contentDispositionLineOfFile.indexOf(fileNameKey) + fileNameKey.length());

        return fileName.substring(0, fileName.indexOf("\""));
    }

    public int[] extractFileContent() {
        String requestBody = new String(this.body, 0, this.body.length);
        String boundary = this.extractBoundaryValue();

        int indexOfFileContentTypeStart = requestBody.indexOf("Content-Type",
                requestBody.indexOf(this.extractFileName()));
        int indexOfFileContentTypeEnd = requestBody.indexOf("\r\n", indexOfFileContentTypeStart);

        String rawContent = requestBody.substring(indexOfFileContentTypeEnd);
        int end;
        for (end = 0; rawContent.substring(end, end + 2).equals("\r\n"); ) {
            end += 2;
        }
        rawContent = rawContent.substring(end);
        for (end = rawContent.length(); rawContent.substring(end - 2, end).equals("\r\n"); ) {
            end -= 2;
        }
        rawContent = rawContent.substring(0, end);

        int indexOfFileContentStart = requestBody.indexOf(rawContent);
        int[] fileContent = new int[indexOfFileContentStart + rawContent.length()];

        System.arraycopy(this.body, requestBody.indexOf(rawContent),
                fileContent, requestBody.indexOf(rawContent) - indexOfFileContentStart,
                requestBody.indexOf(rawContent) + rawContent.length() - requestBody.indexOf(rawContent));

        return fileContent;
    }

    public UUID extractFileUUID() {
        String requestBody = new String(this.body, 0, this.body.length);
        String boundary = this.extractBoundaryValue();

        int indexOfContentDispositionOfUserNameStart =
                requestBody.indexOf("Content-Disposition");
        int indexOfContentDispositionOfUserNameEnd =
                requestBody.indexOf("\r\n", indexOfContentDispositionOfUserNameStart);
        int indexOfBoundaryOfUserName = requestBody.indexOf(
                boundary, indexOfContentDispositionOfUserNameEnd) - 2;

        String raw = requestBody.substring(
                indexOfContentDispositionOfUserNameEnd, indexOfBoundaryOfUserName);

        return UUID.fromString(raw.replace("\r\n", ""));
    }

    private String extractBoundaryValue() {
        if (this.headers.containsKey("Content-type")) {
            String boundaryKey = "boundary=";
            List<String> contentTypeValues = this.headers.get("Content-type");
            String contentTypeValue = contentTypeValues.get(0);

            return contentTypeValue.substring(
                    contentTypeValue.indexOf(boundaryKey) + boundaryKey.length());
        } else {
            return "";
        }
    }
}
