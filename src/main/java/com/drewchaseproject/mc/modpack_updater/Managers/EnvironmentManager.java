package com.drewchaseproject.mc.modpack_updater.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.drewchaseproject.mc.modpack_updater.App;
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
        App.log.debug("Attempting to Update!");
        if (force || CurseHandler.CheckForUpdate()) {
            try {

                App.log.info("Update found!");
                Path archive = CurseHandler.DownloadUpdateArchive();
                Path content = Path.of(archive.getParent().toString(), "output");
                ArchiveHandler.UnzipArchive(archive, content);
                Path manifest = Path.of(content.toString(), "manifest.json");
                if (manifest.toFile().exists()) {
                    List<Mod> newMods = CurseHandler.GetModListFromManifest(manifest);
                    for (Mod newMod : newMods) {
                        if (!ModManager.GetInstance().GetMods().contains(newMod)) {
                            newMod.Download();
                        }
                        for (Mod oldMods : ModManager.GetInstance().GetMods()) {
                            if ((newMod.GetProjectID() == oldMods.GetProjectID() && newMod.GetFileID() != oldMods.GetFileID())) {
                                newMod.Download();
                                ModManager.GetInstance().AddModToBeRemoved(oldMods);
                                App.log.info("Adding Updated version of " + oldMods.GetFileName());
                            }
                        }
                    }
                    for (Mod removed : CurseHandler.CheckForRemovedMods(newMods, ModManager.GetInstance().GetMods())) {
                        ModManager.GetInstance().AddModToBeRemoved(removed);
                    }
                    InstallUpdate();
                    return true;
                } else {
                    App.log.info("Manifest NOT Found!");
                }
            } catch (Exception e) {
            }
        } else
            App.log.info("No update found!");
        return false;

    }

    public static void InstallUpdate() {
        App.log.info("Installing Update...");
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
            App.GetInstance().config.SetReleaseDate(CurseHandler.ParseFileDate(CurseHandler.GetLatestPackVersionAsJson().get("fileDate").getAsString()));
            ModManager.GetInstance().Clear();
            for (Mod mod : newMods) {
                ModManager.GetInstance().Add(mod);
            }

        }
        CleanUp();
    }

    public static void CleanUp() {
        IOHandler.DeleteDirectory(Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp").toFile());
    }
}
