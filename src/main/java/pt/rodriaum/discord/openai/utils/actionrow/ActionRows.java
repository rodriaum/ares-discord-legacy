package pt.rodriaum.discord.openai.utils.actionrow;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class ActionRows {

    @NotNull
    public static ActionRow getImageGenerationActionRows() {
        return ActionRow.of(
                Button.secondary("previous-image", "◀️"),

                Button.success("image-satisfied", "Satisfeito"),
                Button.danger("image-try-again", "Repetir"),

                Button.secondary("next-image", "▶️"));
    }
}
