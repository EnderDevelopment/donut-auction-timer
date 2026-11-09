package com.enderdevelopment.donutauction;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public final class AuctionScreen extends Screen {
    private final AuctionConfig config;
    private TextFieldWidget timer;
    private TextFieldWidget minimum;
    private TextFieldWidget maximum;
    private TextFieldWidget item;
    private TextFieldWidget underpaid;

    public AuctionScreen(AuctionConfig config) {
        super(Text.literal("Donut Auction Settings"));
        this.config = config;
    }

    @Override
    protected void init() {
        int left = width / 2 - 100;
        timer = field(left, 45, "Timer seconds", String.valueOf(config.timerSeconds));
        minimum = field(left, 75, "Minimum price", String.valueOf(config.minimumPrice));
        maximum = field(left, 105, "Maximum price", String.valueOf(config.maximumPrice));
        item = field(left, 135, "Item name (or Any item)", config.itemName);
        underpaid = field(left, 165, "Underpaid threshold", String.valueOf(config.underpaidThreshold));
        addDrawableChild(ButtonWidget.builder(Text.literal("HUD: " + (config.showHud ? "ON" : "OFF")), button -> {
            config.showHud = !config.showHud;
            button.setMessage(Text.literal("HUD: " + (config.showHud ? "ON" : "OFF")));
        }).dimensions(left, 195, 97, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Alerts: " + (config.showUnderpaid ? "ON" : "OFF")), button -> {
            config.showUnderpaid = !config.showUnderpaid;
            button.setMessage(Text.literal("Alerts: " + (config.showUnderpaid ? "ON" : "OFF")));
        }).dimensions(left + 103, 195, 97, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Color: " + colorName()), button -> {
            config.colorPreset = (config.colorPreset + 1) % 5;
            button.setMessage(Text.literal("Color: " + colorName()));
        }).dimensions(left, 220, 97, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Save and close"), button -> {
            readFields();
            config.save();
            close();
        }).dimensions(left + 103, 220, 97, 20).build());
    }

    private TextFieldWidget field(int x, int y, String message, String value) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, 200, 20, Text.literal(message));
        field.setText(value);
        field.setMaxLength(32);
        addDrawableChild(field);
        return field;
    }

    private void readFields() {
        config.timerSeconds = positive(timer.getText(), 60);
        config.minimumPrice = positive(minimum.getText(), 1);
        config.maximumPrice = positive(maximum.getText(), 1000000);
        config.itemName = item.getText().isBlank() ? "Any item" : item.getText();
        config.underpaidThreshold = positive(underpaid.getText(), 10);
    }

    private static int positive(String value, int fallback) {
        try { return Math.max(0, Integer.parseInt(value)); } catch (NumberFormatException ignored) { return fallback; }
    }

    private String colorName() {
        return new String[]{"Green", "Cyan", "Magenta", "Yellow", "Red"}[config.colorPreset];
    }

    @Override
    public void close() {
        readFields();
        config.save();
        if (client != null) client.setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, AuctionHud.color(config.colorPreset));
        context.drawTextWithShadow(textRenderer, Text.literal("Timer, prices, item filter, alert threshold"), width / 2 - 100, 32, 0xFFAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
}
