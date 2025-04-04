package pt.rodriaum.discord.openai.utils.string;

import org.jetbrains.annotations.NotNull;
import pt.rodriaum.discord.openai.objects.openai.ImageParameter;

public class StringBuilders {
    @NotNull
    public static StringBuilder getImageDescStringBuilder(ImageParameter parameter, String stateString) {
        StringBuilder sb = new StringBuilder();

        sb.append(stateString);
        sb.append("\n\n");
        sb.append("Motor Atual: **").append(parameter.getModel().getDisplayName()).append("\n\n");
        sb.append("Resolução: **").append(parameter.getSize().getPixel()).append("**\n");
        sb.append("Qualidade: **").append(parameter.getQuality().toString()).append("**\n");
        sb.append("Quantidade: **").append(parameter.getAmount()).append("**\n\n");
        sb.append("Texto: **").append(parameter.getPrompt()).append("**\n\n");
        sb.append("⏳ **Tempo:** \n");
        sb.append("Pode demorar até 1 minuto para processar a sua imagem.");

        return sb;
    }
}
