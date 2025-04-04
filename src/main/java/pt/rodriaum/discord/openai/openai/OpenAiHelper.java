package pt.rodriaum.discord.openai.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.discord.embed.Embeds;
import pt.rodriaum.discord.openai.objects.Constant;
import pt.rodriaum.discord.openai.objects.json.OpenAiJsonObject;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Setter
@Getter
public class OpenAiHelper {
    private final OpenAiJsonObject openAiJsonObject;

    public OpenAiHelper() {
        this.openAiJsonObject = DiscordOpenAI.getOpenAiJsonObject();
    }

    public int getTotalActiveConversations() {
        return getOpenAiJsonObject().getConversationMap().values().size();
    }

    public int getTotalQuestionsChat(Member member) {
        return getOpenAiJsonObject().getMessageHistoryMap().get(member.getIdLong()).size();
    }

    public boolean hasActiveConversation(Member member) {
        return getOpenAiJsonObject().getConversationMap().containsKey(member.getIdLong());
    }

    public OpenAiModel getConversationChatModel(Member member) {
        return getOpenAiJsonObject().getConversationMap().get(member.getIdLong());
    }

    public Member lastActiveConversationMember(Guild guild) {
        List<Long> membersIdLong = new ArrayList<>(getOpenAiJsonObject().getConversationMap().keySet());

        return guild.getMemberById((!membersIdLong.isEmpty() ? membersIdLong.get(membersIdLong.size() - 1) : null));
    }


    public void create(Member member, OpenAiModel model) {
        long memberIdLong = member.getIdLong();

        getOpenAiJsonObject().getConversationMap().put(memberIdLong, model);
        getOpenAiJsonObject().getMessageHistoryMap().put(memberIdLong, new ArrayList<>());
    }

    public void end(Member member) {
        long memberIdLong = member.getIdLong();

        getOpenAiJsonObject().getConversationMap().remove(memberIdLong);
        getOpenAiJsonObject().getMessageHistoryMap().remove(memberIdLong);
    }

    public CompletableFuture<List<Image>> generateImage(Message message, ImageParameter parameter) {
        CompletableFuture<List<Image>> completableFuture = new CompletableFuture<>();

        CompletableFuture.supplyAsync(() -> {
                    List<Image> images = new ArrayList<>();

                    try {
                        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                                .model(parameter.getModel().getMotorModel())
                                .prompt(parameter.getPrompt())
                                .size(parameter.getSize().getPixel())
                                .quality(parameter.getQuality().toString())
                                .n(parameter.getAmount())
                                .build();

                        // Simulating a call to OpenAI asynchronously (in this example, it is just direct)
                        images.addAll(DiscordOpenAI.getOpenAiService().createImage(createImageRequest).getData());

                    } catch (Exception e) {
                        String description = (e.getMessage().equals(Constant.OPENAI_PROMPT_REFUSED) || e.getMessage().equals(Constant.OPENAI_PROMPT_REFUSED_EXTRA) ?
                                "A descrição da imagem contém texto ou palavra que não é permitido pelo nosso sistema de segurança." : e.getMessage());

                        System.out.println("Unable to generate an async image with A.I because:\n" + e.getMessage());

                        message.editMessageEmbeds(Embeds.getImageGeneratorEmbed(null, parameter, description, Color.RED).build()).queue();
                    }

                    return images;

                }).thenAccept(completableFuture::complete)

                .exceptionally(ex -> {
                    completableFuture.completeExceptionally(ex.getCause());
                    return null;
                });

        return completableFuture;
    }


    public CompletableFuture<String> generateQuestion(OpenAiModel model, String prompt) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        CompletableFuture.supplyAsync(() -> {
                    StringBuilder sb = new StringBuilder();

                    try {
                        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                                .model(model.getMotorModel())
                                .messages(Collections.singletonList(new ChatMessage("user", prompt)))
                                .build();

                        DiscordOpenAI.getOpenAiService().createChatCompletion(chatCompletionRequest).getChoices().forEach((it) -> sb.append(it.getMessage().getContent()).append("\n"));

                    } catch (Exception e) {
                        System.out.println("ChatGPT Question Generator Exception: " + e.getMessage());

                        sb.append(Constant.UNABLE_PERFORM_TASK);
                    }

                    return sb.toString();

                }).thenAccept(completableFuture::complete)

                .exceptionally(ex -> {
                    completableFuture.completeExceptionally(ex.getCause());
                    return null;
                });

        return completableFuture;
    }

    public CompletableFuture<String> generateConversation(Member member, OpenAiModel model, String content) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        CompletableFuture.supplyAsync(() -> {
                    StringBuilder sb = new StringBuilder();

                    long memberIdLong = member.getIdLong();

                    try {
                        getOpenAiJsonObject().getMessageHistoryMap().get(memberIdLong).add(new ChatMessage("user", content));

                        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                                .model(model.getMotorModel())
                                .messages(getOpenAiJsonObject().getMessageHistoryMap().get(memberIdLong))
                                .build();

                        ChatMessage responseMessage = DiscordOpenAI.getOpenAiService().createChatCompletion(chatCompletionRequest)
                                .getChoices()
                                .get(0)
                                .getMessage();

                        getOpenAiJsonObject().getMessageHistoryMap().get(memberIdLong).add(responseMessage);

                        sb.append(responseMessage.getContent()).append("\n");

                    } catch (Exception e) {
                        System.out.println("ChatGPT Conversation Generator Exception: " + e.getMessage());
                        sb.append(Constant.UNABLE_PERFORM_TASK);
                    }

                    return sb.toString();

                }).thenAccept(completableFuture::complete)

                .exceptionally(ex -> {
                    completableFuture.completeExceptionally(ex.getCause());
                    return null;
                });

        return completableFuture;
    }
}