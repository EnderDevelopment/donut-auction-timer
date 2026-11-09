package com.enderdevelopment.donutauction;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class AuctionHud {
    private AuctionHud() {
    }

    public static void register(AuctionConfig config) {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (config.showHud && client.player != null && client.currentScreen == null) render(drawContext, config);
        });
    }

    private static void render(DrawContext context, AuctionConfig config) {
        int color = color(config.colorPreset);
        int x = 8;
        int y = 8;
        context.fill(x - 4, y - 4, x + 220, y + 62, 0xAA101018);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("Donut Auction"), x, y, color);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("Timer: " + DonutAuctionClient.remainingSeconds() + "s"), x, y + 12, 0xFFFFFFFF);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal(DonutAuctionClient.paymentLine()), x, y + 24, 0xFFFFFFFF);
        if (config.showUnderpaid && DonutAuctionClient.lastPayment() > 0 && DonutAuctionClient.lastPayment() < config.underpaidThreshold) {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("UNDERPAID: below $" + config.underpaidThreshold), x, y + 36, 0xFFFF5555);
        } else {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("Price: $" + config.minimumPrice + " - $" + config.maximumPrice), x, y + 36, 0xFFAAAAAA);
        }
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.literal("Press O for settings"), x, y + 48, 0xFFAAAAAA);
    }

    public static int color(int preset) {
        return switch (preset) {
            case 1 -> 0xFF55FFFF;
            case 2 -> 0xFFFF55FF;
            case 3 -> 0xFFFFFF55;
            case 4 -> 0xFFFF5555;
            default -> 0xFF55FF55;
        };
    }
}
