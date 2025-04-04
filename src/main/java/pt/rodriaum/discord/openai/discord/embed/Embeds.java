package pt.rodriaum.discord.openai.discord.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;
import pt.rodriaum.discord.openai.utils.string.StringBuilders;

import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

public class Embeds {

    public static EmbedBuilder getChatResponseEmbed(Member member, Role premiumRole, boolean isPremium) {
        return new EmbedBuilder()
                .setTitle("Resposta")
                .setDescription("")
                .setColor(!isPremium ? Color.YELLOW : Color.GREEN)
                .setFooter(!isPremium ?
                        "Perguntas Disponíveis: " + pt.rodriaum.discord.openai.DiscordOpenAI.getOpenAiHelper().getTotalQuestionsChat(member) + "/" + Integer.parseInt(pt.rodriaum.discord.openai.DiscordOpenAI.getDotenv().get("CHAT_TOTAL_QUESTION")) :
                        "Você tem limite ilimitado devido ao cargo: " + premiumRole.getName()
                );
    }

    public static EmbedBuilder getImageGeneratorEmbed(Guild guild, ImageParameter parameters, String stateString, Color color) {
        return new EmbedBuilder()
                .setTitle("Gerador de Imagem AI")
                .setDescription(StringBuilders.getImageDescStringBuilder(parameters, stateString).toString())
                .setColor(color)
                .setFooter(LocalDate.now().getYear() + " | " + (guild != null ? guild.getName() : "Rodriaum"));
    }
}