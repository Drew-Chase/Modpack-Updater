package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveHandler {

    public static void UnzipArchive(Path archive, Path outputDirectory) {
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(archive.toFile()));
            ZipEntry entry = zipInput.getNextEntry();
            while (entry != null) {
                File file = newFile(outputDirectory.toFile(), entry);
                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdirs()) {
                        throw new IOException(String.format("Failed to create directory: \"%s\"", file));
                    }
                } else {
                    File parent = file.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException(String.format("Failed to create directory: \"%s\"", parent));
                    }

                    FileOutputStream outputStream = new FileOutputStream(file);
                    int length;
                    while ((length = zipInput.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                }
                entry = zipInput.getNextEntry();
            }

            zipInput.closeEntry();
            zipInput.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static File newFile(File destDir, ZipEntry entry) throws IOException {
        File destFile = new File(destDir, entry.getName());

        String destDirPath = destDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator))
            throw new IOException("Entry is outside of the target dir: " + entry.getName());
        return destFile;
    }

}
