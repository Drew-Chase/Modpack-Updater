package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Objects.HttpConnection;

public class NetworkHandler {
    public static boolean DownloadFile(URL url, Path file) {
        App.log.info("Downloading Update...");
        File f = file.toFile();
        f.getParentFile().mkdirs();
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            FileOutputStream output = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.close();
            in.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HttpConnection MakeConnection(URL url, Map<String, String> headers) {
        App.log.debug(String.format("Attempting Connection to \"%s\"", url.toString()));
        try {
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("User-Agent", "Minecraft Modpack Updater");
            for (String key : headers.keySet()) {
                http.setRequestProperty(key, headers.get(key));
            }
            return new HttpConnection(http);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}