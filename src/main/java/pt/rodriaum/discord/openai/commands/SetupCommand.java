package pt.rodriaum.discord.openai.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import pt.rodriaum.discord.openai.openai.enums.model.OpenAiModel;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetupCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        StringBuilder sb = new StringBuilder();
        EmbedBuilder embed = new EmbedBuilder();

        if (guild != null && event.getName().equals("setup")) {
            event.deferReply(false).queue();

            InteractionHook hook = event.getHook();

            switch (Objects.requireNonNull(event.getOption("option")).getAsString()) {
                case "setup-verification":
                    sb.append("Clique no botão `verificar` para enviar o seu pedido de verificação");
                    sb.append("\n\n");
                    sb.append("✅ **Acesso:** Após você ser aceito, os canais seram liberados para você.");

                    embed.setTitle("Verificação")
                            .setDescription(sb.toString())
                            .setColor(Color.GREEN)
                            .setFooter(LocalDate.now().getYear() + " | " + guild.getName());

                    hook.sendMessageEmbeds(embed.build())
                            .addActionRow(ActionRow.of(
                                    Button.success("verify", "Verificar")
                            ).getComponents())
                            .queue();
                    break;

                case "setup-openai":
                    sb.append("Inicie uma conversa com um modelo motor da OpenAI!");
                    sb.append("\n\n");
                    sb.append("\uD83E\uDD14 **Como Funciona:** \nEscolha um modelo e um canal privado será criado.");
                    sb.append("\n\n");
                    sb.append("⚠️ **Alerta:** \nInfelizmente a OpenAI é paga, então, não use desnecessariamente, obrigado.");
                    sb.append("\n\n");
                    sb.append("♾️ **Versão:** \nProjeto em fase beta! apresentou alguns erros e bugs? Por favor, reporte-os!");

                    embed.setTitle("Inteligência Artificial")
                            .setDescription(sb.toString())
                            .setColor(Color.GREEN)
                            .setThumbnail("https://i.giphy.com/media/v1.Y2lkPTc5MGI3NjExc2p4NmRwOWowOWtjcGlwczNtdnZqdXRhcDA2cTgzYm8zZzJmdmZ4eCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/0lGd2OXXHe4tFhb7Wh/giphy.gif")
                            .setFooter(LocalDate.now().getYear() + " | " + guild.getName());

                    List<SelectOption> options = new ArrayList<>();

                    for (OpenAiModel model : OpenAiModel.values()) {
                        options.add(SelectOption.of(model.getDisplayName(), model.toString().toLowerCase()));
                    }

                    hook.sendMessageEmbeds(embed.build())
                            .addActionRow(
                                    StringSelectMenu.create("choose-model-openai")
                                            .addOptions(options)
                                            .build())
                            .queue();
                    break;

                case "setup-private-voice-channel":
                    sb.append("Clique aqui para criar um espaço de conversa exclusivo e seguro! Com apenas um");
                    sb.append("toque, você poderá se conectar em um chat de voz particular, onde suas");
                    sb.append("interações ficam protegidas e apenas acessíveis aos convidados selecionados.");
                    sb.append("\n\n");
                    sb.append("**Psiu!** Nem sempre é confidencial \uD83E\uDD2D");

                    embed.setTitle("Canal de Voz Particular")
                            .setDescription(sb.toString())
                            .setColor(Color.GREEN)
                            .setThumbnail("https://i.giphy.com/media/v1.Y2lkPTc5MGI3NjExNWR3aDFwYjdlYjZ2aHZxbjFtN2U4ZjlqZnVlemx6OGZoZHN6dWdkYyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3OCHDEduPPId0bxBAp/giphy.gif")
                            .setFooter(LocalDate.now().getYear() + " | " + guild.getName());

                    hook.sendMessageEmbeds(embed.build()).addComponents(ActionRow.of(
                            Button.success("create-private-call", "Criar")
                    )).queue();
                    break;
            }
        }
    }
}
