package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.App.LogType;
import com.drewchaseproject.mc.modpack_updater.Managers.ModManager;
import com.drewchaseproject.mc.modpack_updater.Objects.HttpConnection;
import com.drewchaseproject.mc.modpack_updater.Objects.Mod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.icu.text.SimpleDateFormat;

public class CurseHandler {

    public static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static boolean CheckForUpdate() {
        JsonObject json = CurseHandler.GetLatestPackVersionAsJson();
        if (json == null)
            return false;
        return ModManager.GetInstance().IsEmpty() || App.GetInstance().config.getReleaseDate() == null || ParseFileDate(json.get("fileDate").getAsString()).after(App.GetInstance().config.getReleaseDate());
    }

    public static Path DownloadUpdateArchive() {
        try {
            Path output = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp", GetLatestPackVersionAsJson().get("fileName").getAsString());
            NetworkHandler.DownloadFile(new URL(GetLatestPackVersionAsJson().get("downloadUrl").getAsString()), output);
            return output;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Mod> GetModListFromManifest(Path manifest) {
        List<Mod> mods = new ArrayList<>();
        JsonObject json = (JsonObject) JsonHandler.ParseJsonFromFile(manifest.toFile());
        if (json != null) {
            JsonArray files = json.get("files").getAsJsonArray();
            if (files != null) {
                for (JsonElement element : files) {
                    JsonObject obj = element.getAsJsonObject();
                    mods.add(new Mod(obj.get("projectID").getAsInt(), obj.get("fileID").getAsInt()));
                }
            }
        }
        return mods;
    }

    public static List<Mod> CheckForRemovedMods(List<Mod> NewMods, List<Mod> OldMods) {
        List<Mod> removed = new ArrayList<>();
        for (Mod oldMod : OldMods) {
            boolean found = false;
            for (Mod newMod : NewMods) {
                if (oldMod.GetProjectID() == newMod.GetProjectID())
                    found = true;
            }
            if (!found)
                removed.add(oldMod);
        }
        return removed;
    }

    public static JsonObject GetLatestPackVersionAsJson() {
        Date latestDate = null;
        JsonObject latestVersion = null;
        JsonArray array = GetPackAsJson();
        if (array == null)
            return null;
        for (JsonElement element : array) {
            Date date = ParseFileDate(element.getAsJsonObject().get("fileDate").getAsString());
            if (latestDate == null || date.after(latestDate)) {
                latestDate = date;
                latestVersion = element.getAsJsonObject();
            }
        }
        return latestVersion;
    }

    public static Date ParseFileDate(String fileDate) {
        try {
            return DateFormat.parse(fileDate.split("\\.")[0].replace("T", "-"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JsonArray GetPackAsJson() {
        try {
            URL url = new URL(String.format("https://addons-ecs.forgesvc.net/api/v2/addon/%s/files", App.GetInstance().config.getProjectID()));
            HttpConnection connection = new HttpConnection((HttpURLConnection) url.openConnection());
            if (connection.IsSuccess()) {

                JsonArray obj = (JsonArray) JsonParser.parseString(connection.GetContent());
                connection.Close();
                return obj;
            } else {
                App.GetInstance().Log("Unable to get pack information... Check Project ID...", LogType.error);
            }
        } catch (IOException e) {
            App.GetInstance().Log(e.getMessage(), LogType.error);
        }

        return null;
    }

}
