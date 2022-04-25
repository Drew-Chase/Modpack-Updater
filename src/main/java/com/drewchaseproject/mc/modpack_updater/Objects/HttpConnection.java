package com.drewchaseproject.mc.modpack_updater.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class HttpConnection {
    String Content;
    Map<String, List<String>> Headers;
    HttpURLConnection connection;

    public HttpConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public Map<String, List<String>> GetHeaders() {
        if (Headers.isEmpty()) {
            Headers = connection.getHeaderFields();
        }

        return Headers;
    }

    public String[] GetHeader(String key){
        return (String[])Headers.get(key).toArray();
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

    public void Close(){
        connection.disconnect();
    }

}
