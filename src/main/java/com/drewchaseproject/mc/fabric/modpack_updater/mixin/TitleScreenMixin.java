package com.drewchaseproject.mc.fabric.modpack_updater.mixin;

import com.drewchaseproject.mc.fabric.modpack_updater.UpdateScreenOverlay;
import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    ButtonWidget UpdateButton;
    PressableTextWidget UpdateText;

    @Inject(at = @At("HEAD"), method = "initWidgetsNormal")
    private void addCustomButton(int y, int spacingY, CallbackInfo cb) {
        Identifier icon = new Identifier(String.format("%s:textures/gui/update_button.png", App.MOD_ID));
        UpdateText = new PressableTextWidget(5, 5, this.width / 2, 10, Text.of("Update Found!"), button -> ClickUpdate(), this.textRenderer);
        UpdateButton = new TexturedButtonWidget(this.width / 2 + 105, y, 20, 20, 0, 0, 20, icon, 20, 40, button -> ClickUpdate(), Text.of("Check For Update"));
        if (App.GetInstance().config.GetProjectID() == -1) {
            UpdateText = new PressableTextWidget(5, 5, this.width / 2, 10, Text.of("Project ID is not set!"), button -> ClickUpdate(), this.textRenderer);
            this.addDrawableChild(UpdateText);
        } else if (CurseHandler.CheckForUpdate()) {
            this.addDrawableChild(UpdateText);
            this.addDrawableChild(UpdateButton);
        }
    }

    void ClickUpdate() {
        this.remove(UpdateButton);
        this.remove(UpdateText);
        UpdateText = new PressableTextWidget(5, 5, this.width / 2, 10, Text.of("Downloading and Installing Update!"), button -> ClickUpdate(), this.textRenderer);
        this.addDrawableChild(UpdateText);
        App.GetInstance().AttemptUpdate();
        this.remove(UpdateText);
        UpdateText = new PressableTextWidget(5, 5, this.width / 2, 10, Text.of("Restart Minecraft!"), button -> ClickUpdate(), this.textRenderer);
        this.addDrawableChild(UpdateText);
        this.client.setOverlay(null);
    }

}
