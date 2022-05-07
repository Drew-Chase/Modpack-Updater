package com.drewchaseproject.mc.fabric.modpack_updater.mixin;

import com.drewchaseproject.mc.fabric.modpack_updater.UpdateScreen;
import com.drewchaseproject.mc.modpack_updater.App;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    private TitleScreen self;

    protected TitleScreenMixin(Text title) {
        super(title);
        self = (TitleScreen) ((Object) this);
    }

    @Inject(at = @At("HEAD"), method = "initWidgetsNormal")
    private void addCustomButton(int y, int spacingY, CallbackInfo cb) {
        Identifier icon = new Identifier(String.format("%s:textures/gui/update_button.png", App.MOD_ID));
        ButtonWidget UpdateButton = new TexturedButtonWidget(this.width / 2 + 105, y, 20, 20, 0, 0, 20, icon, 20, 40, button -> client.setScreen(new UpdateScreen(self)), Text.of("Check For Update"));
        this.addDrawableChild(UpdateButton);
    }

}
