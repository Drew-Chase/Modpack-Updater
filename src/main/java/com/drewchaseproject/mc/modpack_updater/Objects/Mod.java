package com.drewchaseproject.mc.modpack_updater.Objects;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.Handlers.NetworkHandler;
import com.google.gson.JsonObject;

public class Mod {
    private int ID;
    private String Name;
    private String FileName;
    private URL DownloadURL;

    public Mod(int projectID, int fileID) {

        try {
            HttpConnection connection = new HttpConnection((HttpURLConnection) new URL(String.format("https://cursemeta.dries007.net/%s/%s.json", projectID, fileID)).openConnection());
            JsonObject json = connection.GetContentAsJson();

            ID = json.get("Id").getAsInt();
            Name = json.get("DisplayName").getAsString();
            FileName = json.get("FileName").getAsString();
            DownloadURL = new URL(json.get("DownloadURL").getAsString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int GetID() {
        return ID;
    }

    public String GetName() {
        return Name;
    }

    public boolean Download(Path directory) {
        return NetworkHandler.DownloadFile(DownloadURL, Path.of(directory.toAbsolutePath().toString(), FileName));
    }
}
