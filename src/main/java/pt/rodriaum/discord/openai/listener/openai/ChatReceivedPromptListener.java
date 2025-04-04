package pt.rodriaum.discord.openai.listener.openai;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.objects.Constant;
import pt.rodriaum.discord.openai.objects.json.GuildJsonObject;
import pt.rodriaum.discord.openai.objects.json.JsonObject;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;
import pt.rodriaum.discord.openai.openai.enums.image.ImageQuality;
import pt.rodriaum.discord.openai.openai.enums.image.ImageSize;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;
import pt.rodriaum.discord.openai.openai.OpenAiHelper;
import pt.rodriaum.discord.openai.utils.Util;
import pt.rodriaum.discord.openai.utils.actionrow.ActionRows;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;
import pt.rodriaum.discord.openai.utils.openai.OpenAiUtil;

import java.awt.*;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ChatReceivedPromptListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Guild guild = event.getGuild();

        Member member = event.getMember();
        if (member == null) return;

        if (event.getGuild().getSelfMember() == member) return;

        TextChannel channel = event.getChannel().asTextChannel();

        JsonObject jsonObject =DiscordOpenAI.getJsonObject();
        if (jsonObject == null) return;

        GuildJsonObject guildJsonObject = jsonObject.getGuildJsonMap(guild.getId());
        if (guildJsonObject == null) return;

        if (!Objects.equals(channel.getParentCategoryId(), guildJsonObject.getAiCategoryId()) || event.getMessage().isEdited())
            return;

        Message message = event.getMessage();

        if (!channel.getName().contains(member.getUser().getName())) {

            message.reply("Ops! Você não pode enviar mensagem neste canal.")
                    .delay(Duration.ofSeconds(1))
                    .map((it) -> {

                        it.delete().queue();
                        message.delete().queue();

                        return it;
                    }).queue();
            return;
        }

        Role premiumRole = event.getGuild().getRoleById(guildJsonObject.getPremiumRoleId());

        OpenAiHelper openAiHelper = DiscordOpenAI.getOpenAiHelper();

        int totalQuestions = openAiHelper.getTotalQuestionsChat(member);
        int chatTotalQuestion = Integer.parseInt(DiscordOpenAI.getDotenv().get("CHAT_TOTAL_QUESTION"));

        boolean isPremium = (premiumRole != null && member.getRoles().contains(premiumRole));

        if (!isPremium && (totalQuestions > chatTotalQuestion)) {
            message.reply("Ops! Você atingiu um limite diário de **5** perguntas!")
                    .addActionRow(ActionRow.of(
                            Button.danger("close-chat", "Terminar Conversa")
                    ).getComponents())
                    .queue();
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Inteligência Artificial")
                .setDescription("Processando...")
                .setColor(Color.YELLOW)
                .setFooter(totalQuestions + "/" + (isPremium ? "∞" : chatTotalQuestion));

        Message aiMessage = message.replyEmbeds(embed.build()).complete();

        String prompt = message.getContentDisplay();

        OpenAiModel model = openAiHelper.getConversationChatModel(member);

        switch (model.getOpenAiModelCategory()) {
            case CHAT:
                openAiHelper.generateConversation(member, model, prompt).thenAccept(response -> {

                    aiMessage.editMessageEmbeds(
                            embed.setDescription(response)
                                    .setColor(!isPremium ? Color.CYAN : Color.GREEN)
                                    .build()
                    ).queue();

                }).exceptionally(e -> {
                    aiMessage.editMessageEmbeds(
                            embed.setDescription(Constant.UNABLE_PERFORM_TASK)
                                    .setColor(Color.RED)
                                    .build()).queue();

                    LogUtil.error("EXCEPTION", "Unable to send message after receiving the prompt because:", e.getMessage());
                    return null;
                });
                break;

            case IMAGE:
                ImageParameter parameter = new ImageParameter(prompt, model, ImageSize.SIZE_1792_1024, ImageQuality.HD, 1);

                CompletableFuture<Void> future = OpenAiUtil.handleImageGenerationV2(parameter, aiMessage, null, embed, ActionRows.getImageGenerationActionRows());

                // Variable always returns null but ends up ignoring the check, and without the check it returns an error. It works for now.
                if (future == null || future.isCompletedExceptionally()) return;

                // Using a new variable to avoid modifying 'future'
                CompletableFuture<Void> wrappedFuture = CompletableFuture.completedFuture(null)
                        .thenCompose(v -> future);

                wrappedFuture.thenAccept(it -> {
                    LogUtil.log("IMAGE GENERATOR", "Image generated and sent successfully.");
                    aiMessage.editMessageEmbeds(
                            embed.setDescription("Prontinho")
                                    .setColor(Color.GREEN)
                                    .build()).queue();

                }).exceptionally(e -> {
                    aiMessage.editMessageEmbeds(
                            embed.setDescription(Constant.UNABLE_PERFORM_TASK)
                                    .setColor(Color.RED)
                                    .build()).queue();
                    LogUtil.error("EXCEPTION", "",e.getMessage());
                    return null;
                });
                break;
        }
    }
}
