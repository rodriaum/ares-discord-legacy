package pt.rodriaum.discord.openai.objects.json;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuildJsonObject {
    private String memberRoleId;
    private String chatGptRoleId;
    private String premiumRoleId;
    private String newChatOpenAiChannelId;
    private String aiCategoryId;
}