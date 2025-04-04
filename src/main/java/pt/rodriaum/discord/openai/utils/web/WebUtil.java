package pt.rodriaum.discord.openai.utils.web;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import pt.rodriaum.discord.openai.utils.Util;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;

import java.io.IOException;

public class WebUtil {

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";

    public static String uploadImageToImgur(String imageUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("image", imageUrl)
                .build();

        Request request = new Request.Builder()
                .url(IMGUR_UPLOAD_URL)
                .post(requestBody)
                .addHeader("Authorization", "Client-ID " + pt.rodriaum.discord.openai.DiscordOpenAI.getDotenv().get("IMGUR_CLIENT_ID"))
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();

            if (response.isSuccessful() && body != null)
                return extractLinkFromJson(body.string());
            else
                LogUtil.error("API", "Imgur api error:", response.message());
        }

        return "";
    }

    private static String extractLinkFromJson(String jsonData) {
        JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonObject data = json.getAsJsonObject("data");

        return data.get("link").getAsString();
    }
}
