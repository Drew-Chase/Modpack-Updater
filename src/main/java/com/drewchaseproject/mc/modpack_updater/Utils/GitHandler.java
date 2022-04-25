package com.drewchaseproject.mc.modpack_updater.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Objects.HttpConnection;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitHandler {

    public static boolean CheckForUpdate() {
        App.log.info("Checking for Updates!");
        JsonObject json = GitHandler.GetConnectionAsJson();
        if (json != null) {
            return !json.get("tag_name").getAsString().equals(App.GetInstance().config.GetVersion());
        }
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

    public static JsonObject GetConnectionAsJson() {
        URL url = null;
        try {
            url = new URL(String.format("https://api.github.com/repos/%s/%s/releases/latest", App.GetInstance().config.GetUsername().replace(" ", "-"), App.GetInstance().config.GetRepository().replace(" ", "-")));

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Authorization", "token " + App.GetInstance().config.GetToken());
            http.setRequestProperty("User-Agent", "Minecraft Modpack Updater");

            HttpConnection connection = new HttpConnection(http);

            JsonObject obj = (JsonObject) JsonParser.parseString(connection.GetContent());
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
