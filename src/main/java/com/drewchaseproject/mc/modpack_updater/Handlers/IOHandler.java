package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.File;

public class IOHandler {

    public static boolean DeleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                DeleteDirectory(file);
            }
        }
        return directory.delete();
    }

}
