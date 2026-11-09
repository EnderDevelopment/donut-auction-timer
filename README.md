# Minecraft Client-Side Auction Timer Mod

Donut Auction Client is a client-side Fabric Minecraft mod for Donut SMP that reads auction and payment messages from chat, tracks detected payments, starts configurable auction timers, filters auctions by price and item name, and displays the results in an in-game HUD.

## Install

1. Install Minecraft Java Edition 1.21.1.
2. Install Fabric Loader 0.16.5 or newer for Minecraft 1.21.1.
3. Install Fabric API for Minecraft 1.21.1.
4. Install Java 21 and Gradle.
5. From the project root, build the mod:

```sh
gradle build
```

6. Copy the compiled `donut-auction-client` jar from `build/libs` into the `mods` folder for your Minecraft 1.21.1 Fabric profile.
7. Launch Minecraft with the matching Fabric profile. Lunar Client can be used when its profile supports Fabric mods for Minecraft 1.21.1.

## Use

1. Join Donut SMP and allow the mod to receive auction and payment chat messages.
2. Press `O` to open or close the Donut Auction settings screen.
3. Configure the auction timer in seconds.
4. Set the minimum and maximum auction price filters.
5. Enter an item name, or leave the item filter as `Any item`.
6. Toggle the auction HUD and underpayment alerts.
7. Set the underpayment threshold and select a HUD color preset.
8. Select `Save and close` to store the settings.
9. When a recognizable auction message appears in chat, the mod starts the configured timer.
10. When a recognizable payment message appears, the HUD displays the payment amount and sender when available.
11. The HUD shows the current timer, latest payment, configured price range, and underpayment warning when applicable.

Settings are stored in `config/donutauction.json`.

The parser recognizes common messages containing terms such as `auction`, `listed`, `starting bid`, `paid`, `payment`, or `received`. Donut SMP message wording may vary, so the mod only reacts when the message contains recognizable terms and price or payment information.

---

More Minecraft mod projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/3457142f-1adf-47d2-956a-d5734843b31f?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=3457142f-1adf-47d2-956a-d5734843b31f
