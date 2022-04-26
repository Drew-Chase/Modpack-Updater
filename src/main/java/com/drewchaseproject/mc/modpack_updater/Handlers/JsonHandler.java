package com.drewchaseproject.mc.modpack_updater.Handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHandler {

    public static JsonElement ParseJsonFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

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

    public static void WriteJsonToFile(JsonObject json, File file) {
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(json.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
