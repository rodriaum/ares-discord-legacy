package pt.rodriaum.discord.openai.commands.openai.chat;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import pt.rodriaum.discord.openai.DiscordOpenAI;
import pt.rodriaum.discord.openai.objects.Constant;
import pt.rodriaum.discord.openai.objects.json.GuildJsonObject;
import pt.rodriaum.discord.openai.objects.json.JsonObject;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModelCategory;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;

public class QuestionChatCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        if (event.getName().equals("question")) {
            InteractionHook hook = event.deferReply().complete();
            Member member = event.getMember();

            if (member != null) {
                hook.sendMessage("Ops! Não foi possível encontrar o seu perfil. Tente novamente!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            JsonObject jsonObject =DiscordOpenAI.getJsonObject();
            if (jsonObject == null) return;

            GuildJsonObject guildJsonObject = jsonObject.getGuildJsonMap(guild.getId());
            if (guildJsonObject == null) return;

            Role chatGptRole = guild.getRoleById(guildJsonObject.getChatGptRoleId());

            if (chatGptRole == null) {
                hook.sendMessage("Ops! Parece que o cargo de acesso a esse comando foi eliminado.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            if (event.getMember().getRoles().contains(chatGptRole)) {
                hook.sendMessage("Ops! Você precisa possuir o cargo " + chatGptRole.getAsMention() + " para executar esse comando.")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            OptionMapping motorModelMapping = event.getOption("chatgpt-motor-model");

            if (motorModelMapping == null) return;

            OpenAiModel motorModel = OpenAiModel.GPT_3_TURBO;
            String motorModelString = motorModelMapping.getAsString();

            if (!(motorModelString.isEmpty() || motorModelString.isBlank())) {
                OpenAiModel motorModelAux = OpenAiModel.getByMotorModelName(motorModelString);

                if (motorModelAux != null && motorModelAux.getOpenAiModelCategory().equals(OpenAiModelCategory.CHAT))
                    motorModel = motorModelAux;
            }

            OptionMapping promptMapping = event.getOption("chatgpt-prompt-text");

            if (promptMapping != null) {
                String prompt = promptMapping.getAsString();

                if (prompt.isEmpty()) {
                    hook.sendMessage("Ops! Verifiquei que o seu texto está vazio! Insira alguma pergunta.").queue();
                    return;
                }

                DiscordOpenAI.getOpenAiHelper().generateQuestion(motorModel, prompt)
                        .thenAccept(response -> hook.sendMessage(response).queue())
                        .exceptionally(e -> {
                            hook.sendMessage(Constant.UNABLE_PERFORM_TASK).queue();
                            LogUtil.error("EXCEPTION", "It was not possible to generate a question because:", e.getMessage());
                            return null;
                        });
            }
        }
    }
}
