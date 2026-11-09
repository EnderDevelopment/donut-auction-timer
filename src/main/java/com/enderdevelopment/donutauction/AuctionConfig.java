package com.enderdevelopment.donutauction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AuctionConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("donutauction.json");

    public int timerSeconds = 60;
    public int minimumPrice = 1;
    public int maximumPrice = 1000000;
    public String itemName = "Any item";
    public boolean showHud = true;
    public boolean showUnderpaid = true;
    public int underpaidThreshold = 10;
    public int colorPreset = 0;

    public static AuctionConfig load() {
        if (!Files.exists(FILE)) {
            return new AuctionConfig();
        }
        try {
            AuctionConfig config = GSON.fromJson(Files.readString(FILE), AuctionConfig.class);
            return config == null ? new AuctionConfig() : config;
        } catch (IOException | RuntimeException ignored) {
            return new AuctionConfig();
        }
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }
}
