package com.drewchaseproject.mc.modpack_updater;

import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.Managers.ConfigManager;
import com.drewchaseproject.mc.modpack_updater.Managers.EnvironmentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static App _instance;

    public ConfigManager config;
    public Path WorkingDirectory;
    public static final String MOD_ID = "modpack_updater";

    public static final Logger log = LoggerFactory.getLogger(MOD_ID);

    private App() {
        _instance = this;
        WorkingDirectory = Path.of("config/modpack_updater");
        WorkingDirectory.toFile().mkdirs();
        config = new ConfigManager();
        EnvironmentManager.CleanUp();
    }

    public synchronized void AttemptUpdate() {
        EnvironmentManager.CleanUp();
        if (config.GetProjectID() != -1) {
            EnvironmentManager.TryUpdate(EnvironmentManager.Environment.CLIENT);
        } else
            log.error("Project ID cannot be blank!");
    }

    // private Thread AttemptUpdateAsync() {
    //     Runnable runnable = () -> {
    //         if (config.GetProjectID() != -1) {
    //             EnvironmentManager.TryUpdate(EnvironmentManager.Environment.CLIENT);
    //         } else
    //             log.error("Project ID cannot be blank!");
    //     };
    //     return new Thread(runnable);
    // }

    public static void main(String[] args) {
        GetInstance();
    }

    public static App GetInstance() {
        if (_instance == null)
            return new App();
        return _instance;
    }

}
