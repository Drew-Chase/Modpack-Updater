package com.drewchaseproject.mc.modpack_updater.Managers;

import java.net.URL;
import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.GitHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.NetworkHandler;
import com.google.gson.JsonObject;

public class EnvironmentManager {

    public enum Environment {
        CLIENT, SIDE
    }

    public static boolean TryUpdate(Environment side) {
        return TryUpdate(side, false);
    }

    public static boolean TryUpdate(Environment side, boolean force) {
        App.log.debug("Attempting to Update!");
        if (force || GitHandler.CheckForUpdate()) {
            App.log.info("Update found!");
            JsonObject json = GitHandler.GetConnectionAsJson();
            if (json != null) {
                URL url = side == Environment.CLIENT ? GitHandler.GetClientArchiveURL(json) : GitHandler.GetServerArchiveURL(json);
                Path file = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp", side.toString() + ".zip");
                App.GetInstance().config.SetVersion(json.get("tag_name").getAsString());
                App.log.debug(String.format("Version %s", App.GetInstance().config.GetVersion()));
                return NetworkHandler.DownloadFile(url, file);
            }
        }
        App.log.debug("No update found!");
        return false;
    }
}
