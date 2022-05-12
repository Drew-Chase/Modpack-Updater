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
    private int projectID = -1;
    private Path file;
    private boolean needsUpdate = false;

    public ConfigManager() {
        file = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "config.json");
        Read();
    }

    // Getters
    /**
     * Gets the Release Date of the installed Mod Pack
     * 
     * @return release date
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Gets the Project ID of the Mod Pack
     * 
     * @return project id
     */
    public int getProjectID() {
        return projectID;
    }

    /**
     * Returns if the pack currently requires an update
     * 
     * @return if an update is available
     */
    public boolean getNeedsUpdate() {
        return needsUpdate;
    }

    // Setters
    /**
     * Sets the release date of the mod pack and writes to file. <br>
     * SEE: {@link ConfigManager#Write()}
     * 
     * @param release
     */
    public void setReleaseDate(Date release) {
        this.releaseDate = release;
        Write();
    }

    /**
     * Sets if the mod pack needs an update and writes to file. <br>
     * SEE: {@link ConfigManager#Write()}
     * 
     * @param value
     */
    public void setNeedsUpdate(boolean value) {
        needsUpdate = value;
        Write();
    }

    /**
     * Sets the project id of the mod pack and writes to file. <br>
     * SEE: {@link ConfigManager#Write()}
     * 
     * @param release
     */
    public void setProjectID(int id) {
        this.projectID = id;
        Write();
    }

    /**
     * Writes all values to json file
     */
    public void Write() {
        JsonObject json = new JsonObject();
        json.addProperty("ID", projectID);
        json.addProperty("releaseDate", CurseHandler.DateFormat.format(releaseDate));
        json.addProperty("needsUpdate", needsUpdate);
        JsonHandler.WriteJsonToFile(json, file.toFile());
    }

    /**
     * Reads values from json file.
     */
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
                projectID = obj.get("ID").getAsInt();
            } else {
                setProjectID(projectID);
            }
            if (obj.get("needsUpdate") != null) {
                needsUpdate = obj.get("needsUpdate").getAsBoolean();
            } else {
                setNeedsUpdate(needsUpdate);
            }

        } else {
            Write();
        }
    }

}
