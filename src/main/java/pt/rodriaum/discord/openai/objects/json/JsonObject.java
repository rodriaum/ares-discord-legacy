package pt.rodriaum.discord.openai.objects.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
@AllArgsConstructor
public class JsonObject {
    public final LinkedHashMap<String, GuildJsonObject> guildJsonMap;

    public GuildJsonObject getGuildJsonMap(String guildId) {
        return guildJsonMap.get(guildId);
    }
}
