package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MinecraftHandler {

    public static List<String> InstalledMods() {
        List<String> mods = new ArrayList<>();
        Path modsDir = Path.of(".");

        FilenameFilter justJars = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return name.endsWith(".jar");
            }
        };
        for (String file : modsDir.toFile().list(justJars)) {
            mods.add(file);
        }

        return mods;
    }

}
