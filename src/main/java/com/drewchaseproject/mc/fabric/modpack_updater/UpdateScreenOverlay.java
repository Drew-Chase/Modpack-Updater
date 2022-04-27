package com.drewchaseproject.mc.fabric.modpack_updater;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.IntSupplier;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class UpdateScreenOverlay extends Overlay {
    static final Identifier LOGO = new Identifier("textures/gui/title/mojangstudios.png");
    private static final int MOJANG_RED = ColorHelper.Argb.getArgb(255, 239, 50, 61);
    private static final int MONOCHROME_BLACK = ColorHelper.Argb.getArgb(255, 0, 0, 0);
    private static final IntSupplier BRAND_ARGB = () -> MinecraftClient.getInstance().options.monochromeLogo ? MONOCHROME_BLACK : MOJANG_RED;
    public static final long RELOAD_COMPLETE_FADE_DURATION = 1000L;
    public static final long RELOAD_START_FADE_DURATION = 500L;
    private final MinecraftClient client;
    private float progress;

    public UpdateScreenOverlay(MinecraftClient client) {
        this.client = client;
    }

    public static void init(MinecraftClient client) {
        client.getTextureManager().registerTexture(LOGO, new LogoTexture());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float h;
        int k;
        int i = this.client.getWindow().getScaledWidth();
        int j = this.client.getWindow().getScaledHeight();
        float f = -1.0f;
        if (f >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(matrices, 0, 0, delta);
            }
            k = MathHelper.ceil((1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f)) * 255.0f);
            UpdateScreenOverlay.fill(matrices, 0, 0, i, j, UpdateScreenOverlay.withAlpha(BRAND_ARGB.getAsInt(), k));
            h = 1.0f - MathHelper.clamp(f - 1.0f, 0.0f, 1.0f);
        } else {
            k = BRAND_ARGB.getAsInt();
            float m = (float) (k >> 16 & 0xFF) / 255.0f;
            float n = (float) (k >> 8 & 0xFF) / 255.0f;
            float o = (float) (k & 0xFF) / 255.0f;
            GlStateManager._clearColor(m, n, o, 1.0f);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            h = 1.0f;
        }
        k = (int) ((double) this.client.getWindow().getScaledWidth() * 0.5);
        int p = (int) ((double) this.client.getWindow().getScaledHeight() * 0.5);
        double d = Math.min((double) this.client.getWindow().getScaledWidth() * 0.75, (double) this.client.getWindow().getScaledHeight()) * 0.25;
        int q = (int) (d * 0.5);
        double e = d * 4.0;
        int r = (int) (e * 0.5);
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, h);
        UpdateScreenOverlay.drawTexture(matrices, k - r, p - q, r, (int) d, -0.0625f, 0.0f, 120, 60, 120, 120);
        UpdateScreenOverlay.drawTexture(matrices, k, p - q, r, (int) d, 0.0625f, 60.0f, 120, 60, 120, 120);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        this.progress = MathHelper.clamp(this.progress * 0.95f + 0 * 0.050000012f, 0.0f, 1.0f);
        if (f < 1.0f) {
        }
        if (f >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.client.currentScreen != null) {
            this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        }
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | alpha << 24;
    }

    @Environment(value = EnvType.CLIENT)
    static class LogoTexture extends ResourceTexture {
        public LogoTexture() {
            super(LOGO);
        }

        @Override
        protected ResourceTexture.TextureData loadTextureData(ResourceManager resourceManager) {
            ResourceTexture.TextureData textureData;
            block8: {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackProvider().getPack();
                try (InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, LOGO)) {
                    try {
                        textureData = new ResourceTexture.TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
                        if (inputStream == null)
                            break block8;
                    } catch (Throwable throwable) {
                        try {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        } catch (IOException iOException) {
                            return new ResourceTexture.TextureData(iOException);
                        }
                    }
                    inputStream.close();
                    return textureData;
                } catch (Throwable e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}