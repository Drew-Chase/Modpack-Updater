package com.drewchaseproject.mc.modpack_updater;

import java.nio.file.Path;

import com.drewchaseproject.mc.modpack_updater.Managers.ConfigManager;

public class App {
    private static App _instance;
    public ConfigManager config;
    public Path WorkingDirectory;
    
    private App() {
        _instance = this;
        WorkingDirectory = Path.of("modpack_updater");
        config = new ConfigManager();
    }
    
    public static void main(String[] args) {
    }
    
    public static App GetInstance() {
        if (_instance == null)
            return new App();
        return _instance;
    }
}
