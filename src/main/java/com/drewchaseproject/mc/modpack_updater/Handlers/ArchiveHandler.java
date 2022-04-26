package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.drewchaseproject.mc.modpack_updater.App;

public class ArchiveHandler {

    public static Path ZipArchive(Path archive) {

        try {
            File file = archive.toFile();
            FileOutputStream output = new FileOutputStream(file);
            ZipOutputStream zip = new ZipOutputStream(output);
            FileInputStream input = new FileInputStream(file);
            ZipEntry entry = new ZipEntry(file.getName());

            byte[] bytes = new byte[1024];
            int length;
            while((length=input.read(bytes))>=0){
                zip.write(bytes, 0, length);
            }

            zip.close();
            input.close();
            output.close();
        } catch (FileNotFoundException e) {
            App.log.error(String.format("Archive \"%s\" not found", archive.toAbsolutePath().toString()));
        }catch (IOException e){
            StringBuilder stackTrace = new StringBuilder();
            for(StackTraceElement s : e.getStackTrace()){
                stackTrace.append(s.toString());
            }
            App.log.error(String.format("%s\n%s", e.getMessage(), stackTrace.toString()));
            App.log.error(String.format("Unknown issue has occurred while attempting to Unzip \"%s\"... Aborting!", archive.toAbsolutePath().toString()));
        }

        return null;
    }

    public static Path UnzipArchive(Path archive){
        

        return null;
    }

}
