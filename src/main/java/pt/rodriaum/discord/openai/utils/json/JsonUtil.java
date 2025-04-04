package pt.rodriaum.discord.openai.utils.json;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;

import java.io.*;
import java.lang.reflect.Type;

public class JsonUtil {

    public static String convert(Object object) {
        return DiscordOpenAI.GSON.toJson(object);

    }

    public static boolean write(String fileName, Object object) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(convert(object));
            LogUtil.log("CONFIG", "JSON written successfully in " + fileName);
            return true;
        } catch (IOException e) {
            LogUtil.error("CONFIG", "The last configuration changes could not be saved because:", e.getMessage());
            return false;
        }
    }

    public static boolean write(Object object) {
        return write("config.json", object);
    }

    public static Object read(String fileName, Type type) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "windows-1252"))) {
            return DiscordOpenAI.GSON.fromJson(reader, type);

        } catch (IOException e) {
            LogUtil.log("CONFIG", "Failed to read JSON file: " + e.getMessage());

        } catch (JsonIOException | JsonSyntaxException e) {
            LogUtil.error("CONFIG", "Error parsing JSON file:", e.getMessage());
        }

        return null;
    }

    public static Object read(Type type) {
        return read("config.json", type);
    }
}