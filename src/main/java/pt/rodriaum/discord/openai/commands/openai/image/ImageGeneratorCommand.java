package pt.rodriaum.discord.openai.commands.openai.image;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import pt.rodriaum.discord.openai.discord.embed.Embeds;
import pt.rodriaum.discord.openai.objects.Constant;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;
import pt.rodriaum.discord.openai.openai.enums.image.ImageQuality;
import pt.rodriaum.discord.openai.openai.enums.image.ImageSize;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;
import pt.rodriaum.discord.openai.utils.Util;
import pt.rodriaum.discord.openai.utils.actionrow.ActionRows;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;
import pt.rodriaum.discord.openai.utils.openai.OpenAiUtil;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class ImageGeneratorCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        if (guild == null) return;

        if (event.getName().equals("image-generator")) {
            InteractionHook hook = event.deferReply().complete();

            Member member = event.getMember();

            if (member == null) {
                hook.sendMessage("Ops! Não foi possível encontrar o seu perfil. Tente novamente!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            Role premiumRole = guild.getRoleById(pt.rodriaum.discord.openai.DiscordOpenAI.getDotenv().get("CHATGPT_ROLE_ID"));

            if (premiumRole == null) {
                hook.sendMessage("Ops! Parece que o cargo de acesso a esse comando foi eliminado.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            if (!member.getRoles().contains(premiumRole)) {
                hook.sendMessage("Ops! Você precisa possuir o cargo " + premiumRole.getAsMention() + " para executar esse comando.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            OptionMapping promptMapping = event.getOption("image-prompt-text");

            if (promptMapping == null) return;
            String prompt = promptMapping.getAsString();

            OpenAiModel model = OpenAiModel.DALL_E_3;
            ImageParameter parameter = new ImageParameter(prompt, model, ImageSize.SIZE_1024_1024, ImageQuality.STANDARD, 1);

            EmbedBuilder embed = Embeds.getImageGeneratorEmbed(guild, parameter, "A processar imagem...", Color.YELLOW);
            Message message = hook.sendMessageEmbeds(embed.build()).complete();

            CompletableFuture<Void> future = OpenAiUtil.handleImageGenerationV2(parameter, message, hook, embed, ActionRows.getImageGenerationActionRows());

            // Variable always returns null but ends up ignoring the check, and without the check it returns an error. It works for now.
            if (future == null || future.isCompletedExceptionally()) return;

            future.thenAccept(it -> LogUtil.log("IMAGE GENERATOR", "Image generated and sent successfully."))
                    .exceptionally(e -> {
                        hook.sendMessage(Constant.UNABLE_PERFORM_TASK).queue();
                        LogUtil.error("EXCEPTION", "", e.getMessage());

                        return null;
                    });
        }
    }
}