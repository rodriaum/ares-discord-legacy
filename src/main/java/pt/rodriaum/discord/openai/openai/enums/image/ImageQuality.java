package pt.rodriaum.discord.openai.openai.enums.image;

public enum ImageQuality {

    HD,
    STANDARD;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
