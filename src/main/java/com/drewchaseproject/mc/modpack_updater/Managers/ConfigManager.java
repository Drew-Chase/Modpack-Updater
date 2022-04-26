package com.drewchaseproject.mc.modpack_updater.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConfigManager {

    private Date releaseDate = null;
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            StringBuilder builder = new StringBuilder();
            // @formatter:off
            builder.append("{")
            .append(String.format("\"ID\": \"%s\",", projectID))
            .append(String.format("\"releaseDate\": \"%s\"", CurseHandler.DateFormat.format(releaseDate)))
            .append("}");
            // @formatter:on
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Read() {
        if (file.toFile().exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file.toFile()));
                String line;
                line = reader.readLine();
                StringBuilder builder = new StringBuilder();
                while (line != null) {
                    builder.append(line).append("\n");
                    line = reader.readLine();
                }
                JsonObject obj = (JsonObject) JsonParser.parseString(builder.toString());
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            Write();
        }
    }

}
