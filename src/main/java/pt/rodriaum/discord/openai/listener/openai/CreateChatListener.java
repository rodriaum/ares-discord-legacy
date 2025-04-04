package pt.rodriaum.discord.openai.listener.openai;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.objects.json.GuildJsonObject;
import pt.rodriaum.discord.openai.objects.json.JsonObject;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;

import java.util.EnumSet;

public class CreateChatListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("choose-model-openai")) return;

        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (guild == null || member == null) {
            event.getHook().sendMessage("Ops! Não foi possível encontrar o seu perfil. Tente novamente!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        InteractionHook hook = event.deferReply(true).complete();

        JsonObject jsonObject =DiscordOpenAI.getJsonObject();
        if (jsonObject == null) return;

        GuildJsonObject guildJsonObject = jsonObject.getGuildJsonMap(guild.getId());
        if (guildJsonObject == null) return;

        Role chatGptRoleId = guild.getRoleById(guildJsonObject.getChatGptRoleId());

        if (chatGptRoleId == null) {
            event.getHook().sendMessage("Ops! Parece que o cargo de acesso a esse comando foi eliminado.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!member.getRoles().contains(chatGptRoleId)) {
            hook.sendMessage("Ops! Você precisa possuir o cargo " + chatGptRoleId.getAsMention() + " para executar esse comando.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (pt.rodriaum.discord.openai.DiscordOpenAI.getOpenAiHelper().hasActiveConversation(member)) {
            event.getHook().sendMessage("Ops! Você ainda tem uma conversa aberta!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        OpenAiModel model = OpenAiModel.getByEnumName(event.getValues().get(0));

        Category category = guild.getCategoryById(guildJsonObject.getAiCategoryId());

        if (model == null || category == null) return;

        pt.rodriaum.discord.openai.DiscordOpenAI.getOpenAiHelper().create(member, model);

        // Creation Event
        TextChannel channel = category.createTextChannel("\uD83E\uDDFF┃" + member.getUser().getName()).complete();

        // Permissions
        EnumSet<Permission> permissions = EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.MESSAGE_SEND);

        // Sets channel permissions
        channel.getPermissionContainer().getManager().putMemberPermissionOverride(member.getIdLong(), permissions, null)
                .putRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, permissions).queue();

        event.getHook().sendMessage("**Sucesso!** Acesse sua conversa em " + channel.getAsMention())
                .setEphemeral(true)
                .queue();
    }
}