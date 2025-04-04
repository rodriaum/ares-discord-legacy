package pt.rodriaum.discord.openai.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;
import pt.rodriaum.discord.openai.utils.string.StringBuilders;

import java.awt.*;


public class Util {

    public static void handleMethodAsyncError(Message message, EmbedBuilder embed, ImageParameter parameters, String debug, LayoutComponent... components) {
        LogUtil.error("EXCEPTION", "The generate image command could not be executed because the image could not be generated: ", debug);

        message.editMessageEmbeds(
                        embed.setDescription(StringBuilders.getImageDescStringBuilder(parameters, "Ops! Não foi possível gerar a imagem.").toString())
                                .setColor(Color.RED)
                                .build()
                ).setComponents(components)
                .queue();
    }
}
