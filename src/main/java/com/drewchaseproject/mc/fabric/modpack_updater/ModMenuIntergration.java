package com.drewchaseproject.mc.fabric.modpack_updater;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntergration  implements ModMenuApi{

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent->ClothScreenManager.CreateConfigScreen(parent);
    }
    
}
