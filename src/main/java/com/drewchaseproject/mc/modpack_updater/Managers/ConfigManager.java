package com.drewchaseproject.mc.modpack_updater.Managers;

import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.JsonHandler;
import com.google.gson.JsonObject;

public class ConfigManager {

    private Date releaseDate = new Date(0);
    private String projectID = "";
    private Path file;

    public ConfigManager() {
        file = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "config.json");
        Read();
    }

    public void SetReleaseDate(Date release) {
        this.releaseDate = release;
        Write();
    }

    public Date GetReleaseDate() {
        return releaseDate;
    }

    public void SetProjectID(String id) {
        this.projectID = id;
        Write();
    }

    public String GetProjectID() {
        return projectID;
    }

    public void Write() {
        JsonObject json = new JsonObject();
        json.addProperty("ID", projectID);
        json.addProperty("releaseDate", CurseHandler.DateFormat.format(releaseDate));
        JsonHandler.WriteJsonToFile(json, file.toFile());
    }

    public void Read() {
        if (file.toFile().exists()) {
            JsonObject obj = (JsonObject) JsonHandler.ParseJsonFromFile(file.toFile());
            if (obj.get("releaseDate") != null) {
                try {
                    releaseDate = CurseHandler.DateFormat.parse(obj.get("releaseDate").getAsString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (obj.get("ID") != null) {
                projectID = obj.get("ID").getAsString();
            }

        } else {
            Write();
        }
    }

}
