package com.drewchaseproject.mc.modpack_updater.Managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.App;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConfigManager {

    String version = "0.0.0";
    String Username;
    String Repository;
    String Token;
    Path file;

    public ConfigManager() {
        file = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "config.json");
    }

    public void Write() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile()))) {
            StringBuilder builder = new StringBuilder();
            // @formatter:off
            builder.append("{")
            .append(String.format("\"version\": \"%s\"", version))
            .append(String.format("\"username\": \"%s\"", Username))
            .append(String.format("\"repository\": \"%s\"", Repository))
            .append(String.format("\"token\": \"%s\"", Token))
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
                if (obj.get("version") != null) {
                    version = obj.get("version").getAsString();
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

        }
    }

}
