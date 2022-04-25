package com.drewchaseproject.mc.modpack_updater.Utils;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GitHandler {

    public static boolean CheckForUpdate() {
        return false;
    }

    public static URL GetClientArchiveURL(JsonObject obj) {
        URL url = null;
        for (JsonElement i : obj.get("assets").getAsJsonArray()) {
            if (i.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("client.zip")) {
                try {
                    url = new URL(i.getAsJsonObject().get("browser_download_url").getAsString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return url;
    }

    public static URL GetServerArchiveURL(JsonObject obj) {
        URL url = null;
        for (JsonElement i : obj.get("assets").getAsJsonArray()) {
            if (i.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("server.zip")) {
                try {
                    url = new URL(i.getAsJsonObject().get("browser_download_url").getAsString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return url;
    }

}
