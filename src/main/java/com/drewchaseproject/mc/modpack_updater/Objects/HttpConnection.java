package com.drewchaseproject.mc.modpack_updater.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpConnection {
    private String Content = "";
    private Map<String, List<String>> Headers = new HashMap<>();
    private HttpURLConnection connection;

    public HttpConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public Map<String, List<String>> GetHeaders() {
        if (Headers.isEmpty()) {
            Headers = connection.getHeaderFields();
        }

        return Headers;
    }

    public String[] GetHeader(String key) {
        return (String[]) Headers.get(key).toArray();
    }

    public boolean IsSuccess() {
        try {
            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String GetContent() {
        if (Content.isEmpty()) {
            InputStreamReader reader;
            try {
                reader = new InputStreamReader((InputStream) connection.getContent());
                BufferedReader buffer = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String line = buffer.readLine();
                while (line != null) {
                    builder.append(line).append("\n");
                    line = buffer.readLine();
                }
                buffer.close();
                Content = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Content;
    }

    public JsonObject GetContentAsJson() {
        return JsonParser.parseString(GetContent()).getAsJsonObject();
    }

    public void Close() {
        connection.disconnect();
    }

}
