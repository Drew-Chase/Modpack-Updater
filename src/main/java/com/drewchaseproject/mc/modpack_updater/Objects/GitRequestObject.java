package com.drewchaseproject.mc.modpack_updater.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GitRequestObject {
    public final String Username;
    public final String Token;
    public final String Repository;
    public final JsonObject Content;

    private GitRequestObject(String gitUser, String gitRepoName, String gitToken, JsonObject obj) {
        this.Username = gitUser;
        this.Token = gitToken;
        this.Repository = gitRepoName;
        Content = obj;

    }

    public static GitRequestObject Make(String gitUser, String gitRepoName, String gitToken) {
        URL url = null;
        try {
            url = new URL(String.format("https://api.github.com/repos/%s/%s/releases/latest", gitUser.replace(" ", "-"), gitRepoName.replace(" ", "-")));

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Authorization", "token " + gitToken);
            http.setRequestProperty("User-Agent", "Minecraft Modpack Updater");

            InputStreamReader reader = new InputStreamReader((InputStream) http.getContent());
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line = buffer.readLine();
            while (line != null) {
                builder.append(line).append("\n");
                line = buffer.readLine();
            }

            buffer.close();
            http.disconnect();
            JsonObject obj = (JsonObject) JsonParser.parseString(builder.toString());
            return new GitRequestObject(gitUser, gitRepoName, gitToken, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
