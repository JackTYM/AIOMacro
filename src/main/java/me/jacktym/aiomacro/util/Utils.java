package me.jacktym.aiomacro.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import gg.essential.universal.ChatColor;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static JsonObject bazaarApi;

    //ScreenshotUtils
    public static String takeScreenshot() {
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capture = new Robot().createScreenCapture(screenRect);

            String filename = String.valueOf(System.currentTimeMillis()).substring(String.valueOf(System.currentTimeMillis()).length() - 3);

            File temp = File.createTempFile(filename, ".png");

            ImageIO.write(capture, "png", temp);

            temp.deleteOnExit();

            return uploadScreenshot(temp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String uploadScreenshot(File file) {
        String link = "";

        try {
            URL url;
            url = new URL("https://api.imgur.com/3/image");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedImage image;
            image = ImageIO.read(file);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArray);
            byte[] byteImage = byteArray.toByteArray();
            String dataImage = Base64.encode(byteImage);
            String data = URLEncoder.encode("image", "UTF-8") + "="
                    + URLEncoder.encode(dataImage, "UTF-8");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Client-ID 9a47f5fc66bc069");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            conn.connect();
            StringBuilder stb = new StringBuilder();
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                stb.append(line).append("\n");
            }
            wr.close();
            rd.close();

            JsonElement jsonElement = new JsonParser().parse(stb.toString());

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            link = jsonObject.get("data").getAsJsonObject().get("link").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }

    //Time Utils
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long timeSinceMillis(long oldMS) {
        return System.currentTimeMillis() - oldMS;
    }

    public static boolean millisPassed(long waitTime) {
        return currentTimeMillis() >= waitTime;
    }


    //Webhook Utils
    public static void sendWebhook(JsonElement jsonElement) {

        JsonObject json = new JsonObject();
        json.addProperty("content", "");
        json = jsonElement.getAsJsonObject();

        String urlstr = AIOMVigilanceConfig.webhookLink;

        try {
            URL url = new URL(urlstr);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "User-Agent");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream stream = connection.getOutputStream();
            stream.write(json.toString().getBytes());
            stream.flush();
            stream.close();

            connection.getInputStream().close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBazaarApi() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(
                        "https://api.hypixel.net/skyblock/bazaar");
                HttpResponse response = client.execute(request);

                String result = IOUtils.toString(new BufferedReader
                        (new InputStreamReader(
                                response.getEntity().getContent())));

                JsonElement jelement = new JsonParser().parse(result);
                bazaarApi = jelement.getAsJsonObject().getAsJsonObject("products");

            } catch (IOException ignored) {
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    //Color Utils
    public static String stripColor(String toStrip) {
        for (ChatColor c : ChatColor.values()) {
            toStrip = toStrip.replaceAll(c.toString(), "");
        }
        return toStrip;
    }
}
