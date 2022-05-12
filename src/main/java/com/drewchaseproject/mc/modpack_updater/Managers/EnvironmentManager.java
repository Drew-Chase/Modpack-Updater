package com.drewchaseproject.mc.modpack_updater.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.App.LogType;
import com.drewchaseproject.mc.modpack_updater.Handlers.ArchiveHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.IOHandler;
import com.drewchaseproject.mc.modpack_updater.Objects.Mod;
import com.google.common.io.Files;

public class EnvironmentManager {

    public enum Environment {
        CLIENT, SIDE
    }

    public static boolean TryUpdate(Environment side) {
        return TryUpdate(side, false);
    }

    public static boolean TryUpdate(Environment side, boolean force) {
        App.GetInstance().Log("Attempting to Update!", LogType.info);
        if (force || CurseHandler.CheckForUpdate()) {
            try {
                App.GetInstance().Log("Downloading Update!", LogType.info);
                Path archive = CurseHandler.DownloadUpdateArchive();
                Path content = Path.of(archive.getParent().toString(), "output");
                ArchiveHandler.UnzipArchive(archive, content);
                Path manifest = Path.of(content.toString(), "manifest.json");
                if (manifest.toFile().exists()) {
                    List<Mod> newMods = CurseHandler.GetModListFromManifest(manifest);
                    for (Mod newMod : newMods) {
                        if (!ModManager.GetInstance().Contains(newMod)) {
                            App.GetInstance().Log(newMod.GetFileName());
                            newMod.Download();
                        }
                        for (Mod oldMods : ModManager.GetInstance().GetMods()) {
                            if ((newMod.GetProjectID() == oldMods.GetProjectID() && newMod.GetFileID() != oldMods.GetFileID())) {
                                newMod.Download();
                                ModManager.GetInstance().AddModToBeRemoved(oldMods);
                                App.GetInstance().Log("Adding Updated version of " + oldMods.GetFileName());
                            }
                        }
                    }
                    for (Mod removed : CurseHandler.CheckForRemovedMods(newMods, ModManager.GetInstance().GetMods())) {
                        ModManager.GetInstance().AddModToBeRemoved(removed);
                    }
                    InstallUpdate();
                    return true;
                } else {
                    App.GetInstance().Log("Manifest NOT Found!", LogType.error);
                }
            } catch (Exception e) {
                App.GetInstance().Log(e.getMessage(), LogType.error);
            }
        } else
            App.GetInstance().Log("No update found!", LogType.warn);
        return false;

    }

    public static void InstallUpdate() {
        App.GetInstance().Log("Installing Update...", LogType.info);
        Path toInstall = Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp", "toIntall");
        if (toInstall.toFile().isDirectory()) {
            for (File file : toInstall.toFile().listFiles()) {
                try {
                    Files.move(file, Path.of("./mods", file.getName()).toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Mod> mods = ModManager.GetInstance().GetModsToBeRemoved();
            for (Mod mod : mods) {
                File file = Path.of(mod.GetFileName()).toFile();
                file.delete();
            }
            List<Mod> newMods = CurseHandler.GetModListFromManifest(Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp", "output", "manifest.json"));
            App.GetInstance().config.setReleaseDate(CurseHandler.ParseFileDate(CurseHandler.GetLatestPackVersionAsJson().get("fileDate").getAsString()));
            ModManager.GetInstance().Clear();
            for (Mod mod : newMods) {
                ModManager.GetInstance().Add(mod);
            }

        }
        CleanUp();
        App.GetInstance().Log("Restart Minecraft!", LogType.info);
    }

    public static void CleanUp() {
        App.GetInstance().Log("Cleaning Temp Files...", LogType.warn);
        IOHandler.DeleteDirectory(Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp").toFile());
    }
}
