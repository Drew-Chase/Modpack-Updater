package com.drewchaseproject.mc.modpack_updater.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.ArchiveHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.IOHandler;
import com.drewchaseproject.mc.modpack_updater.Handlers.MinecraftHandler;
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
                    List<Mod> mods = CurseHandler.GetModListFromManifest(manifest);
                    List<String> installedMods = MinecraftHandler.InstalledMods();
                    for (Mod mod : mods) {
                        if (!installedMods.contains(mod.GetFileName())) {
                            mod.Download();
                            for (Mod m : ModManager.GetInstance().GetMods()) {
                                if (mod.GetProjectID() == m.GetProjectID() && mod.GetFileID() != m.GetFileID()) {
                                    ModManager.GetInstance().AddModToBeRemoved(m);
                                    App.log.info("Adding Updated version of " + m.GetFileName());
                                }
                            }
                        }
                    }
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
                    Files.move(file, Path.of(".", file.getName()).toFile());
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

            CleanUp();
        }
    }

    public static void CleanUp() {
        IOHandler.DeleteDirectory(Path.of(App.GetInstance().WorkingDirectory.toAbsolutePath().toString(), "temp").toFile());
    }
}
