package com.drewchaseproject.mc.modpack_updater.Objects;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.NetworkHandler;
import com.google.gson.JsonObject;

public class Mod {
    private String Name;
    private String FileName;
    private int ProjectID;
    private int FileID;
    private URL DownloadURL;

    public Mod(int projectID, int fileID) {

        try {
            HttpConnection connection = new HttpConnection((HttpURLConnection) new URL(String.format("https://cursemeta.dries007.net/%s/%s.json", projectID, fileID)).openConnection());
            JsonObject json = connection.GetContentAsJson();

            ProjectID = projectID;
            FileID = fileID;

            Name = json.get("DisplayName").getAsString();
            FileName = json.get("FileName").getAsString();
            DownloadURL = new URL(json.get("DownloadURL").getAsString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int GetProjectID() {
        return ProjectID;
    }

    public int GetFileID() {
        return FileID;
    }

    public String GetName() {
        return Name;
    }

    public String GetFileName() {
        return FileName;
    }

    public boolean Download() {
        App.log.info(String.format("Downloading \"%s\"", FileName));
        Path outputDirectory = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp", "toIntall");
        outputDirectory.toFile().mkdirs();
        return NetworkHandler.DownloadFile(DownloadURL, Path.of(outputDirectory.toAbsolutePath().toString(), FileName));
    }
}
