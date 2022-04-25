package com.drewchaseproject.mc.modpack_updater.Managers;

import java.net.URL;
import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Objects.GitRequestObject;
import com.drewchaseproject.mc.modpack_updater.Utils.GitHandler;
import com.drewchaseproject.mc.modpack_updater.Utils.NetworkUtil;

public class EnvironmentManager {

    public enum Environment {
        CLIENT, SIDE
    }

    public static boolean TryUpdate(Environment side) {
        return TryUpdate(side, false);
    }

    public static boolean TryUpdate(Environment side, boolean force) {
        if (force || GitHandler.CheckForUpdate()) {
            GitRequestObject request = GitRequestObject.Make(App.GetInstance().config.Username, App.GetInstance().config.Repository, App.GetInstance().config.Token);
            URL url = GitHandler.GetClientArchiveURL(request.Content);
            Path file = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp", "client.zip");
            return NetworkUtil.DownloadFile(url, file);
        }
        return false;
    }
}
