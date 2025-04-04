package pt.rodriaum.discord.openai.objects.openai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pt.rodriaum.discord.openai.openai.enums.image.ImageQuality;
import pt.rodriaum.discord.openai.openai.enums.image.ImageSize;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;

@Getter
@AllArgsConstructor
public class ImageParameter {
    public String prompt;
    public OpenAiModel model;
    public ImageSize size;
    public ImageQuality quality;
    public int amount;
}
