package pt.rodriaum.discord.openai.commands.system;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;


public class ShutdownCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("shutdown")) return;

        InteractionHook hook = event.deferReply(true).complete();

        Member member = event.getMember();
        if (member == null) return;

        if (member.isOwner()) {
            hook.sendMessage("Desligando...").queue();
            event.getJDA().shutdown();
        }
    }
}