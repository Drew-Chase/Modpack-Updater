package com.drewchaseproject.mc.fabric.modpack_updater;

import com.drewchaseproject.mc.modpack_updater.App;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ClothScreenManager {

    public static Screen CreateConfigScreen(Screen parent) {

        // Create Screen
        ConfigBuilder builder = ConfigBuilder.create().setTitle(new TranslatableText(String.format("title.%s.config", App.MOD_ID)));
        builder.setParentScreen(parent);
        // Create Category
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText(String.format("category.%s.general", App.MOD_ID)));

        // Add Project ID Option
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startIntField(new TranslatableText(String.format("option.%s.projectID", App.MOD_ID)), App.GetInstance().config.GetProjectID()).setDefaultValue(0).setTooltip(new TranslatableText(String.format("option.%s.projectID.tooltip", App.MOD_ID))).setSaveConsumer(newValue -> App.GetInstance().config.SetProjectID(newValue)).build());
        

        // Saving
        builder.setDoesConfirmSave(true);

        return builder.build();
    }

}
