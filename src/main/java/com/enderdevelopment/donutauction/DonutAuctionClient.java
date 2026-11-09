package com.enderdevelopment.donutauction;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DonutAuctionClient implements ClientModInitializer {
    private static final Pattern PAYMENT = Pattern.compile("(?i)(?:paid|pay(?:ment)?(?: of)?|received)\\s*\\$?\\s*([\\d,]+)\\s*(?:from|by)?\\s*(.*)");
    private static final Pattern PRICE = Pattern.compile("(?i)\\$\\s*([\\d,]+)|([\\d,]+)\\s*(?:coins|dollars|\u24b8)");
    private static AuctionConfig config;
    private static KeyBinding openKey;
    private static long auctionEndsAt;
    private static long lastPayment;
    private static String lastPaymentText = "Waiting for payment messages";
    private static String lastAuction = "No auction detected";

    @Override
    public void onInitializeClient() {
        config = AuctionConfig.load();
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.donutauction.open_settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O,
                KeyBinding.Category.create("category.donutauction.controls")));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new AuctionScreen(config));
                else if (client.currentScreen instanceof AuctionScreen) client.setScreen(null);
            }
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> inspectMessage(message.getString()));
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> inspectMessage(message.getString()));
        AuctionHud.register(config);
    }

    private static void inspectMessage(String input) {
        String text = input.replaceAll("\\u00a7.", "");
        Matcher payment = PAYMENT.matcher(text);
        if (payment.find()) {
            long amount = parseNumber(payment.group(1));
            lastPayment = amount;
            lastPaymentText = "Payment: $" + String.format(Locale.US, "%,d", amount) + (payment.group(2).isBlank() ? "" : " from " + payment.group(2).trim());
            if (config.showUnderpaid && amount < config.underpaidThreshold && MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.literal("[Auction] Underpayment detected: $" + String.format(Locale.US, "%,d", amount)), false);
            }
        }
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.contains("auction") || lower.contains("listed") || lower.contains("starting bid")) {
            Matcher price = PRICE.matcher(text);
            long found = price.find() ? parseNumber(price.group(1) != null ? price.group(1) : price.group(2)) : -1;
            boolean inRange = found < 0 || (found >= config.minimumPrice && found <= config.maximumPrice);
            if (inRange && (config.itemName.equalsIgnoreCase("Any item") || lower.contains(config.itemName.toLowerCase(Locale.ROOT)))) {
                lastAuction = text;
                auctionEndsAt = System.currentTimeMillis() + config.timerSeconds * 1000L;
            }
        }
    }

    private static long parseNumber(String value) {
        try { return Long.parseLong(value.replace(",", "")); } catch (NumberFormatException ignored) { return 0; }
    }

    static String paymentLine() { return lastPaymentText; }
    static String auctionLine() { return lastAuction; }
    static long remainingSeconds() { return Math.max(0, (auctionEndsAt - System.currentTimeMillis() + 999) / 1000); }
    static long lastPayment() { return lastPayment; }
}
