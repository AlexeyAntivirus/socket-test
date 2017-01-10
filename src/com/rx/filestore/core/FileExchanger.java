package com.rx.filestore.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by jrunix on 1/3/17.
 */

public class FileExchanger {

    private static final FileExchanger INSTANCE = new FileExchanger();


    private final Path storeFilePath = Paths.get("/", "home", "jrunix", ".rxservice");

    private final Path fileMapFilePath = storeFilePath.resolve("file-map.properties");

    private Properties fileMap;


    private FileExchanger() {
        try {
            this.fileMap = new Properties();
            if (Files.notExists(this.fileMapFilePath)) {
                Files.createFile(this.fileMapFilePath);
            } else {
                this.fileMap.load(Files.newBufferedReader(this.fileMapFilePath));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileExchanger getInstance() {
        return INSTANCE;
    }


    public UUID upload(String fileName, int[] fileContent) {
        Path loadedFilePath = this.storeFilePath.resolve(fileName);

        try (FileOutputStream stream = new FileOutputStream(
                this.storeFilePath.resolve(fileName).toFile())) {

            for (int fileContentByte : fileContent) {
                stream.write(fileContentByte);
            }
            stream.flush();

            UUID fileUUID = UUID.randomUUID();

            this.fileMap.put(fileUUID.toString(), loadedFilePath.toString());
            this.fileMap.store(Files.newBufferedWriter(this.fileMapFilePath), "");

            return fileUUID;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFilePath(UUID userUUID) {
        return (String) this.fileMap.get(userUUID.toString());
    }
}
