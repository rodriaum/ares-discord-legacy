package pt.rodriaum.discord.openai.listener.openai;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import pt.rodriaum.discord.openai.DiscordOpenAI;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CloseChatListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!Objects.equals(event.getButton().getId(), "close-chat")) return;

        Member member = event.getInteraction().getMember();
        if (member == null) return;

        DiscordOpenAI.getOpenAiHelper().end(member);

        event.reply("\nObrigado por usar a **OpenAI**! A fechar a conversa...\n")
                .delay(1, TimeUnit.SECONDS)
                .flatMap((it) -> Objects.requireNonNull(it.getInteraction().getChannel()).delete())
                .queue();
    }
}