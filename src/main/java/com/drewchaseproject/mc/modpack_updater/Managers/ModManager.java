package com.drewchaseproject.mc.modpack_updater.Managers;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.JsonHandler;
import com.drewchaseproject.mc.modpack_updater.Objects.Mod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ModManager {
    private static ModManager _instance;
    private List<Mod> _mods;
    private List<Mod> _modsToBeRemoved;
    private File manifest;

    public static ModManager GetInstance() {
        if (_instance == null)
            return new ModManager();
        return _instance;
    }

    private ModManager() {
        _instance = this;
        manifest = Path.of(App.GetInstance().WorkingDirectory.toString(), "manifest.json").toFile();
        _mods = new ArrayList<>();
        _modsToBeRemoved = new ArrayList<>();
        Read();
    }

    public void Clear() {
        _mods.clear();
    }

    public List<Mod> GetMods() {
        return _mods;
    }

    public boolean Contains(Mod mod) {
        for (Mod m : _mods) {
            if (m.GetProjectID() == mod.GetProjectID() || Path.of("mods/" + m.GetFileName()).toFile().exists())
                return true;
        }
        return false;
    }

    public List<Mod> GetModsToBeRemoved() {
        return _modsToBeRemoved;
    }

    public boolean IsEmpty() {
        Read();
        return _mods.size() == 0;
    }

    public Mod Add(Mod mod) {
        _mods.add(mod);
        Write();
        return mod;
    }

    public void AddModToBeRemoved(Mod mod) {
        _modsToBeRemoved.add(mod);
    }

    public void Remove(Mod mod) {
        Write();
        _mods.remove(mod);
    }

    private void Write() {
        JsonObject json = new JsonObject();
        JsonArray modsJson = new JsonArray();
        for (Mod mod : _mods) {
            JsonObject modJson = new JsonObject();
            modJson.addProperty("projectID", mod.GetProjectID());
            modJson.addProperty("fileID", mod.GetFileID());
            modJson.addProperty("fileName", mod.GetFileName());

            modsJson.add(modJson);
        }
        json.add("mods", modsJson);
        JsonHandler.WriteJsonToFile(json, manifest);
    }

    private void Read() {
        if (manifest.exists()) {
            _mods.clear();
            JsonObject json = (JsonObject) JsonHandler.ParseJsonFromFile(manifest);
            JsonArray modsJson = json.get("mods").getAsJsonArray();
            for (JsonElement element : modsJson) {
                int projectID = element.getAsJsonObject().get("projectID").getAsInt();
                int fileID = element.getAsJsonObject().get("fileID").getAsInt();
                Add(new Mod(projectID, fileID));
            }
        } else {
            Write();
        }
    }
}