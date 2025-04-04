package pt.rodriaum.discord.openai.objects.json;

import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class OpenAiJsonObject {
    public final Map<Long, OpenAiModel> conversationMap;
    public final Map<Long, List<ChatMessage>> messageHistoryMap;
}