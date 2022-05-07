package com.drewchaseproject.mc.fabric.modpack_updater;

import java.util.ArrayList;
import java.util.List;

import com.drewchaseproject.mc.fabric.modpack_updater.widget.LogListWidget;
import com.drewchaseproject.mc.modpack_updater.App;
import com.drewchaseproject.mc.modpack_updater.App.LogType;
import com.drewchaseproject.mc.modpack_updater.Handlers.CurseHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class UpdateScreen extends Screen {
	private final MinecraftClient client = MinecraftClient.getInstance();

	Screen _parent;

	LogListWidget logListWidget;

	public UpdateScreen(Screen parent) {
		super(new TranslatableText("updater.title.updater"));
		this._parent = parent;

	}

	@Override
	protected void init() {
		super.init();

		int buttonWidth = 120;
		int buttonHeight = 20;
		int padding = 20;
		logListWidget = new LogListWidget(client, this);
		this.addDrawableChild(logListWidget);
		TextFieldWidget projectIDField = new TextFieldWidget(this.textRenderer, this.width - buttonWidth - 5, 5, buttonWidth, buttonHeight, new TranslatableText("option.modpack_updater.projectID"));
		projectIDField.setText(App.GetInstance().config.GetProjectID() + "");
		projectIDField.setChangedListener(value -> {
			if (!value.isBlank()) {
				try {
					int projectID = Integer.parseInt(value);
					App.GetInstance().config.SetProjectID(projectID);
				} catch (NumberFormatException e) {
					for (char c : value.toCharArray()) {
						if (!Character.isDigit(c)) {
							value = value.replace(c + "", "");
						}
					}
					projectIDField.setText(value);
				}
			}
		});
		this.addDrawableChild(projectIDField);
		this.addDrawableChild(new ButtonWidget((this.width / 2) - (buttonWidth / 2) - ((buttonWidth / 2) + padding), this.height - buttonHeight - 5, buttonWidth, buttonHeight, new TranslatableText("updater.option.checkforupdate"), button -> {
			button.active = false;
			if (CurseHandler.CheckForUpdate()) {
				App.GetInstance().Log("Checking for Updates!", LogType.info);
				App.GetInstance().Log(String.format("New Version: %s", CurseHandler.GetLatestPackVersionAsJson().get("displayName")));
				App.GetInstance().Log("Update Found!", LogType.info);
				this.addDrawableChild(new ButtonWidget(button.x, button.y, buttonWidth, buttonHeight, new TranslatableText("updater.option.updatenow"), b2 -> {
					b2.active = false;
					App.GetInstance().AttemptUpdate();
					this.remove(b2);
				}));
				this.remove(button);

			} else {
				App.GetInstance().Log("No Update Found!");
				button.active = true;
			}
		}));

		this.addDrawableChild(new ButtonWidget((this.width / 2) - (buttonWidth / 2) + ((buttonWidth / 2) + padding), this.height - buttonHeight - 5, buttonWidth, buttonHeight, ScreenTexts.BACK, button -> this.client.setScreen(_parent)));

	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		UpdateScreen.drawTextWithShadow(matrices, textRenderer, title, 12, 12, 0xffffff);
		UpdateScreen.drawTextWithShadow(matrices, textRenderer, new TranslatableText("option.modpack_updater.projectID"), this.width - 130 - textRenderer.getWidth(new TranslatableText("option.modpack_updater.projectID")), 10, 0xffffff);
	}

	@Override
	public void tick() {
		logListWidget.Update();
	}
}
