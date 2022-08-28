package me.jacktym.aiomacro.macros.coins;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.jacktym.aiomacro.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class APIUtils {
    public static Map<String, String> updateAuctionsAPI() {

        Map<String, String> auctionItems = new HashMap<>();

        try {
            URL auctionUrl = new URL("https://api.hypixel.net/skyblock/auctions");
            HttpURLConnection connection = (HttpURLConnection) auctionUrl.openConnection();
            InputStream response = connection.getInputStream();
            if (response != null) {
                try (Scanner scanner = new Scanner(response)) {
                    String responseBody = scanner.useDelimiter("\\A").next();

                    JsonElement jsonElement = new JsonParser().parse(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    int numPages = jsonObject.get("totalPages").getAsInt();
                    new Thread(() -> {
                        for (int page = 0; page < numPages; page++) {
                            try {
                                URL pageUrl = new URL("https://api.hypixel.net/skyblock/auctions?page=" + page);
                                HttpURLConnection connection1 = (HttpURLConnection) pageUrl.openConnection();
                                InputStream response1 = connection1.getInputStream();
                                if (response1 != null) {
                                    try (Scanner scanner1 = new Scanner(response1)) {
                                        String responseBody1 = scanner1.useDelimiter("\\A").next();

                                        JsonElement jsonElement1 = new JsonParser().parse(responseBody1);
                                        JsonObject jsonObject1 = jsonElement1.getAsJsonObject();
                                        JsonArray auctions = jsonObject1.getAsJsonArray("auctions");

                                        for (JsonElement item : auctions) {
                                            boolean isBin = item.getAsJsonObject().get("bin").getAsBoolean();
                                            if (isBin) {
                                                JsonElement itemLore = item.getAsJsonObject().get("item_lore");
                                                JsonElement itemName = item.getAsJsonObject().get("item_name");
                                                JsonElement itemPrice = item.getAsJsonObject().get("starting_bid");
                                                JsonElement itemSeller = item.getAsJsonObject().get("auctioneer");

                                                String lore = Utils.stripColor(itemLore.getAsString());

                                                String itemNameString = itemName.getAsString();

                                                //black/whitelist code here


                                                //add item to list of auctions

                                                auctionItems.put(itemNameString, lore + " ;" + "Price: " + itemPrice + " , Seller: " + itemSeller);
                                            } else {
                                                System.out.println("FAIL 2");
                                            }
                                        }
                                        page++;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        } catch (IOException ignored) {}
        return auctionItems;
    }
}

