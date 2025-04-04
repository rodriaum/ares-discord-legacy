package pt.rodriaum.discord.openai.utils.logger;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.objects.Constant;
import pt.rodriaum.discord.openai.utils.formatter.FormatterUtil;

import java.awt.*;
import java.time.LocalDateTime;

public class LogUtil {

    @Getter
    private static LocalDateTime time = LocalDateTime.now();

    public static void discord(String title, String description, Color color) {
        String date = FormatterUtil.formatNumber(getTime().getHour()) + ":" + FormatterUtil.formatNumber(getTime().getMinute()) + ":" + FormatterUtil.formatNumber(getTime().getSecond());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(description)
                .setColor(color)
                .setFooter(date, Constant.LOGO_LINK);

        Guild guild = DiscordOpenAI.getJda().getGuildById(DiscordOpenAI.getDotenv().get("GUILD_ID"));
        if (guild == null) return;

        TextChannel channel = guild.getTextChannelById(DiscordOpenAI.getDotenv().get("LOGS_CHANNEL_ID"));
        if (channel == null) return;

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public static void discord(String title, String description) {
        discord(title, description, Color.GREEN);
    }

    public static void log(String prefix, String s) {
        String date = FormatterUtil.formatNumber(getTime().getHour()) + ":" + FormatterUtil.formatNumber(getTime().getMinute()) + ":" + FormatterUtil.formatNumber(getTime().getSecond());
        System.out.println(!(prefix.isEmpty() && s.isEmpty()) ? "[" + (date) + " - " + prefix.toUpperCase() + "] " + s : "");
    }

    public static void error(String prefix, String s, String error) {
        String date = FormatterUtil.formatNumber(getTime().getHour()) + ":" + FormatterUtil.formatNumber(getTime().getMinute()) + ":" + FormatterUtil.formatNumber(getTime().getSecond());
        System.err.println("[" + (date) + " - " + prefix.toUpperCase() + "] " + s + (!error.isEmpty() ? "\n -> " + error : "."));
    }
}
