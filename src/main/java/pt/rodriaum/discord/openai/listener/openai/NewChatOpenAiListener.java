package pt.rodriaum.discord.openai.listener.openai;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.objects.json.GuildJsonObject;
import pt.rodriaum.discord.openai.objects.json.JsonObject;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModelCategory;
import pt.rodriaum.discord.openai.openai.OpenAiHelper;

import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

public class NewChatOpenAiListener extends ListenerAdapter {

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        /// When the message is not from an A.I. chat and is different from a text channel, it will return an error, so instead of using "asTextChannel", we get it from the id.
        TextChannel channel = event.getGuild().getTextChannelById(event.getChannel().getId());
        if (channel == null) return;

        Guild guild = event.getGuild();

        JsonObject jsonObject = DiscordOpenAI.getJsonObject();
        if (jsonObject == null) return;

        GuildJsonObject guildJsonObject = jsonObject.getGuildJsonMap(guild.getId());
        if (guildJsonObject == null) return;

        if (!Objects.equals(channel.getParentCategoryId(), guildJsonObject.getAiCategoryId())) return;

        OpenAiHelper openAiHelper = DiscordOpenAI.getOpenAiHelper();

        Member member = openAiHelper.lastActiveConversationMember(channel.getGuild());

        OpenAiModel model = openAiHelper.getConversationChatModel(member);
        if (model == null) return;

        StringBuilder sb = new StringBuilder();

        if (Objects.equals(model.getOpenAiModelCategory(), OpenAiModelCategory.CHAT))
            sb.append("Insira a sua pergunta para iniciar a conversa.\n\n");

        else if (Objects.equals(model.getOpenAiModelCategory(), OpenAiModelCategory.IMAGE))
            sb.append("Insira a sua frase para gerar a imagem.\n\n");

        sb.append("Motor Atual: **").append(model.getDisplayName()).append("**\n\n");
        sb.append("\uD83D\uDCDA **Regras:** \n");
        sb.append("Tenha respeito no canal atual.\n\n");
        sb.append("⏳ ****Tempo:** \n");
        sb.append("Pode demorar segundos ou minutos para processar o seu pedido.");

        if (Objects.equals(channel.getParentCategoryId(), guildJsonObject.getAiCategoryId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Olá, " + member.getUser().getName())
                    .setDescription(sb.toString())
                    .setColor(Color.GREEN)
                    .setFooter(LocalDate.now().getYear() + " | " + event.getGuild().getName())
                    .build();

            channel.sendMessageEmbeds(embed)
                    .addActionRow(ActionRow.of(
                            Button.danger("close-chat", "Terminar Conversa")
                    ).getComponents())
                    .queue();
        }
    }
}