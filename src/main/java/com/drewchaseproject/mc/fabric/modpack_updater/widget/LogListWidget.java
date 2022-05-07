package com.drewchaseproject.mc.fabric.modpack_updater.widget;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.drewchaseproject.mc.fabric.modpack_updater.widget.LogListWidget.Entry;
import com.drewchaseproject.mc.modpack_updater.App;
import com.google.common.collect.ImmutableList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class LogListWidget extends ElementListWidget<Entry> {

	private final Screen parent;

	Map<String, Integer> color_code = new HashMap<>();

	public LogListWidget(MinecraftClient client, Screen parent) {
		super(client, parent.width + 45, parent.height, 30, parent.height - 32, 15);
		this.parent = parent;
		color_code.put("trace", 0xffffff);
		color_code.put("debug", 0xaaaaaa);
		color_code.put("info", 0x00ff44);
		color_code.put("warn", 0xaaff00);
		color_code.put("error", 0xff4444);
		Update();
	}

	public void Update() {
		this.clearEntries();
		for (String line : App.GetInstance().GetLogMessages()) {
			String type = line.split("-")[0].replace("(", "").trim();
			try {
				int color = color_code.get(type);
				this.addEntry(new LogEntry(line, this, color));
			} catch (NullPointerException e) {
				System.out.println(type);
			}
		}
		this.setScrollAmount(this.getMaxScroll());
	}

	@Override
	protected int getScrollbarPositionX() {
		return this.parent.width - 15;
	}

	@Override
	public int getRowWidth() {
		return this.parent.width - 32;
	}

	@Environment(value = EnvType.CLIENT)
	public class LogEntry extends Entry {
		final Text text;
		private final int textWidth;
		final int textColor;

		public LogEntry(String text, LogListWidget parent, int textColor) {
			this.text = Text.of(text);
			this.textWidth = parent.width - 50;
			this.textColor = textColor;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			client.textRenderer.draw(matrices, this.text, (float) (client.currentScreen.width / 2 - this.textWidth / 2), (float) (y + entryHeight - client.textRenderer.fontHeight - 1), textColor);
		}

		@Override
		public boolean changeFocus(boolean lookForwards) {
			return false;
		}

		@Override
		public List<? extends Element> children() {
			return Collections.emptyList();
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(new Selectable() {

				@Override
				public Selectable.SelectionType getType() {
					return Selectable.SelectionType.HOVERED;
				}

				@Override
				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, LogEntry.this.text);
				}
			});
		}
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends ElementListWidget.Entry<Entry> {
	}

}
