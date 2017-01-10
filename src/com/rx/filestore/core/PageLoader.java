package com.rx.filestore.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by jrunix on 1/7/17.
 */
public class PageLoader {

    private static final PageLoader INSTANCE = new PageLoader();

    private Path pageDirectory;

    private PageLoader() {
        this.pageDirectory = Paths.get(
                "/home/jrunix/Projects/Java/socket-test/src/com/rx/filestore/templates");
    }

    public static PageLoader getInstance() {
        return INSTANCE;
    }

    private Scanner createPageCodeScanner(String pageName) {
        try {
            BufferedReader srcReader = Files.newBufferedReader(
                    this.pageDirectory
                            .resolve(pageName));
            return new Scanner(srcReader).useDelimiter("\\Z");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public String getPageCode(String pageName) {
        Scanner pageCodeScanner = this.createPageCodeScanner(pageName);
        return pageCodeScanner == null ? "" : pageCodeScanner.next();
    }
}
