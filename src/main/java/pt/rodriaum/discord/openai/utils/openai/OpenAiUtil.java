package pt.rodriaum.discord.openai.utils.openai;

import com.theokanning.openai.billing.BillingUsage;
import com.theokanning.openai.image.Image;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.utils.FileUpload;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;
import pt.rodriaum.discord.openai.utils.Util;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;
import pt.rodriaum.discord.openai.utils.string.StringBuilders;
import pt.rodriaum.discord.openai.utils.web.WebUtil;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OpenAiUtil {

    @Deprecated
    public static BillingUsage getBillingUsage() {
        // The pre-defined date was the first payment on the OpenAI API.
        return pt.rodriaum.discord.openai.DiscordOpenAI.getOpenAiService().billingUsage(LocalDate.of(2024, 8, 7), LocalDate.now());
    }

    public static CompletableFuture<Void> handleImageGenerationV2(ImageParameter parameter, Message message, InteractionHook hook, EmbedBuilder embed, LayoutComponent component) {
        CompletableFuture<List<FileUpload>> completableFuture = new CompletableFuture<>();

        CompletableFuture.supplyAsync(() -> {
            pt.rodriaum.discord.openai.DiscordOpenAI.getOpenAiHelper().generateImage(message, parameter).thenAccept(images -> {

                if (images.isEmpty()) {
                    if (hook != null)
                        hook.sendMessage("Ops! Verifiquei que o seu texto está vazio! Insira alguma pergunta.").setEphemeral(true).queue();

                    return;
                }

                String url = images.get(0).getUrl();

                try {
                    url = WebUtil.uploadImageToImgur(url);

                    LogUtil.log("IMAGE CONVERTED", url);

                } catch (IOException e) {
                    LogUtil.error(
                            "EXCEPTION",
                            "It was not possible to convert the OpenAI image link to imgur, the OpenAI link will be used, this link will be provisional and will soon become unavailable.\n",
                            e.getMessage()
                    );
                }

                // Verificar dpeois se o canal existe.
                message.editMessageEmbeds(
                                embed.setDescription(StringBuilders.getImageDescStringBuilder(parameter, "Prontinho!").toString())
                                        .setColor(Color.GREEN)
                                        .setImage(url)
                                        .build()
                        ).setComponents(component)
                        .queue();

                for (Image image : images) {
                    LogUtil.log("IMAGE", "New images generated.");
                    LogUtil.log("IMAGE", image.getUrl());
                }

                LogUtil.log("", "");

            }).exceptionally(e -> {
                Util.handleMethodAsyncError(message, embed, parameter, e.getMessage(), component);
                return null;
            });

            return completableFuture;
        });

        return null;
    }
}
