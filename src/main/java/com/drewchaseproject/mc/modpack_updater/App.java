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

    public static final Logger log = LoggerFactory.getLogger("modpack_updater");

    private App() {
        _instance = this;
        WorkingDirectory = Path.of("modpack_updater");
        WorkingDirectory.toFile().mkdirs();
        config = new ConfigManager();
        if (!(config.GetUsername().isBlank() || config.GetRepository().isBlank() || config.GetToken().isBlank()))
            EnvironmentManager.TryUpdate(EnvironmentManager.Environment.CLIENT);
        else if (config.GetUsername().isBlank())
            log.error("Username cannot be blank!");
        else if (config.GetRepository().isBlank())
            log.error("Repository cannot be blank!");
        else if (config.GetToken().isBlank())
            log.error("Token cannot be blank!");
    }

    public static void main(String[] args) {
        GetInstance();
    }

    public static App GetInstance() {
        if (_instance == null)
            return new App();
        return _instance;
    }
}
