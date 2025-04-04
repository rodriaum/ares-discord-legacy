package pt.rodriaum.discord.openai.openai.enums.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum OpenAiModel {
    // GPT_4_O(OpenAiMotorModelType.CHAT, "GPT-4 Omni", "gpt-4o", true),
    GPT_4_O_MINI(OpenAiModelCategory.CHAT, "GPT-4o Mini", "gpt-4o-mini"),
    // GPT_4(OpenAiMotorModelType.CHAT, "GPT-4", "gpt-4"),
    GPT_4_TURBO(OpenAiModelCategory.CHAT, "GPT-4 Turbo", "gpt-4-turbo"),
    GPT_3_TURBO(OpenAiModelCategory.CHAT, "GPT-3 Turbo", "gpt-3.5-turbo"),
    DALL_E_3(OpenAiModelCategory.IMAGE, "DALL·E 3", "dall-e-3");
    // DALL_E_2(OpenAiMotorModelType.IMAGE, "DALL·E 2", "dall-e-2");

    private final OpenAiModelCategory openAiModelCategory;
    private final String displayName;
    private final String motorModel;
    private final boolean exclusive;

    OpenAiModel(OpenAiModelCategory openAiModelCategory, String displayName, String motorModel) {
        this.openAiModelCategory = openAiModelCategory;
        this.displayName = displayName;
        this.motorModel = motorModel;
        this.exclusive = false;
    }

    public static List<OpenAiModel> getByModelType(OpenAiModelCategory openAiModelCategory) {
        return Arrays.stream(OpenAiModel.values())
                .filter(model -> model.getOpenAiModelCategory().equals(openAiModelCategory))
                .collect(Collectors.toList());
    }

    public static OpenAiModel getByMotorModelName(String modelName) {
        return Arrays.stream(values())
                .filter(model -> model.getMotorModel().equals(modelName))
                .findFirst()
                .orElse(null);
    }

    public static OpenAiModel getByEnumName(String name) {
        return Arrays.stream(values())
                .filter(model -> model.toString().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
