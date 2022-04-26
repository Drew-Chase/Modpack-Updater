package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Objects.HttpConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.text.SimpleDateFormat;

public class CurseHandler {

    public static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static boolean CheckForUpdate() {
        App.log.info("Checking for Updates!");
        JsonObject json = CurseHandler.GetLatestPackVersionAsJson();
        return json == null || App.GetInstance().config.GetReleaseDate() == null || ParseFileDate(json.get("fileDate").getAsString()).after(App.GetInstance().config.GetReleaseDate());
    }

    public static URL GetClientArchiveURL() {
        try {
            return new URL(GetLatestPackVersionAsJson().get("downloadUrl").getAsString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
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

    public static JsonObject GetLatestPackVersionAsJson() {
        Date latestDate = null;
        JsonObject latestVersion = null;
        for (JsonElement element : GetPackAsJson()) {
            Date date = ParseFileDate(element.getAsJsonObject().get("fileDate").getAsString());
            if (latestDate == null || date.after(latestDate)) {
                latestDate = date;
                latestVersion = element.getAsJsonObject();
            }
        }
        return latestVersion;
    }

    static Date ParseFileDate(String fileDate) {
        try {
            return DateFormat.parse(fileDate.split("\\.")[0].replace("T", "-"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonArray GetPackAsJson() {
        try {
            URL url = new URL(String.format("https://addons-ecs.forgesvc.net/api/v2/addon/%s/files", App.GetInstance().config.GetProjectID()));
            HttpConnection connection = new HttpConnection((HttpURLConnection) url.openConnection());
            JsonArray obj = (JsonArray) JsonParser.parseString(connection.GetContent());
            connection.Close();
            return obj;
        } catch (IOException e) {
        }

        return null;
    }

}
