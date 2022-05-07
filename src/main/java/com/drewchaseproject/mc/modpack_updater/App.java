package com.drewchaseproject.mc.modpack_updater;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;
import com.drewchaseproject.mc.modpack_updater.Managers.ConfigManager;
import com.drewchaseproject.mc.modpack_updater.Managers.EnvironmentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    public enum LogType {
        debug, info, warn, error, trace
    }

    private static App _instance;

    public ConfigManager config;
    public Path WorkingDirectory;
    public static final String MOD_ID = "modpack_updater";

    private static JTextArea logArea = new JTextArea();
    private final File logFile;
    private static final Logger _log = LoggerFactory.getLogger(MOD_ID);

    private App() {
        _instance = this;
        WorkingDirectory = Path.of("config/modpack_updater");
        logFile = Path.of(WorkingDirectory.toAbsolutePath().toString(), "log.txt").toFile();
        if (logFile.exists())
            logFile.delete();

        WorkingDirectory.toFile().mkdirs();
        config = new ConfigManager();
        EnvironmentManager.CleanUp();
    }

    public void AttemptUpdate() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                EnvironmentManager.CleanUp();
                if (config.GetProjectID() != -1) {
                    EnvironmentManager.TryUpdate(EnvironmentManager.Environment.CLIENT);
                } else
                    Log("Project ID cannot be blank!", LogType.error);
            }
        };
        Thread thread = new Thread(run);
        thread.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Modpack Updater");

        // Set Log Text Area
        logArea.setVisible(true);
        logArea.setEditable(false);
        logArea.setEnabled(false);
        logArea.setSize(frame.getSize());
        logArea.setBounds(frame.getBounds());
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(Color.white);
        logArea.setLineWrap(true);

        // Add Items To Frame
        frame.add(logArea);

        // Set Frame Options
        frame.setAlwaysOnTop(true);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GetInstance().AttemptUpdate();

    }

    public static App GetInstance() {
        if (_instance == null)
            return new App();
        return _instance;
    }

    public void Log(String message) {
        Log(message, LogType.debug);
    }

    public void Log(String message, LogType type) {
        switch (type) {
        case debug:
            _log.debug(message);
            break;
        case info:
            _log.info(message);
            break;
        case warn:
            _log.warn(message);
            break;
        case error:
            _log.error(message);
            break;
        case trace:
            _log.trace(message);
            break;
        default:
            _log.debug(message);
            break;
        }
        message = String.format("(%s - %s): %s\n", type, CurseHandler.DateFormat.format(new Date()), message);
        logArea.append(message);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log(e.getMessage(), LogType.error);
        }
    }

    public String[] GetLogMessages() {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            return reader.lines().toList().toArray(new String[0]);
        } catch (IOException e) {

        }
        return null;
    }

}
