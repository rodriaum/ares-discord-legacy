package pt.rodriaum.discord.openai.openai.enums.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageSize {

    SIZE_1792_1024("1792x1024"),
    SIZE_1024_1792("1024x1792"),
    SIZE_1024_1024("1024x1024");

    private final String pixel;

}