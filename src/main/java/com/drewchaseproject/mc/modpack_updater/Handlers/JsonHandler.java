package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonHandler {

    public static JsonElement ParseJsonFromFile(Path file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {

            String line = reader.readLine();
            StringBuilder builder = new StringBuilder();
            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }

            reader.close();
            return JsonParser.parseString(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
