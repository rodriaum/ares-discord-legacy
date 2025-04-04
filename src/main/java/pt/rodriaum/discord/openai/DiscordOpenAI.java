package pt.rodriaum.discord.openai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import pt.rodriaum.discord.openai.commands.SetupCommand;
import pt.rodriaum.discord.openai.commands.openai.chat.QuestionChatCommand;
import pt.rodriaum.discord.openai.commands.openai.image.ImageGeneratorCommand;
import pt.rodriaum.discord.openai.commands.system.PingCommand;
import pt.rodriaum.discord.openai.commands.system.ShutdownCommand;
import pt.rodriaum.discord.openai.listener.openai.ChatReceivedPromptListener;
import pt.rodriaum.discord.openai.listener.openai.CloseChatListener;
import pt.rodriaum.discord.openai.listener.openai.CreateChatListener;
import pt.rodriaum.discord.openai.listener.openai.NewChatOpenAiListener;
import pt.rodriaum.discord.openai.objects.json.JsonObject;
import pt.rodriaum.discord.openai.objects.json.OpenAiJsonObject;
import pt.rodriaum.discord.openai.openai.OpenAiHelper;
import pt.rodriaum.discord.openai.utils.json.JsonUtil;
import pt.rodriaum.discord.openai.utils.logger.LogUtil;

import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;

@Getter
@Setter
public class DiscordOpenAI extends ListenerAdapter {

    @Getter
    @Setter
    protected static JDA jda;

    /**
     * Secret Key System
     */

    @Getter
    @Setter
    protected static Dotenv dotenv;

    /**
     * OpenAI System
     */

    @Getter
    @Setter
    protected static OpenAiService openAiService;

    @Getter
    @Setter
    protected static OpenAiHelper openAiHelper;

    /**
     * Json
     */

    @Getter
    @Setter
    protected static JsonObject jsonObject;

    @Getter
    @Setter
    protected static OpenAiJsonObject openAiJsonObject;

    /**
     * Checks
     */

    @Getter
    @Setter
    protected static boolean devBuild;

    /**
     * Variables
     */

    public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static JsonParser PARSER = new JsonParser();
    public static Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        // Key System
        setDotenv(Dotenv.load());

        // Json
        setJsonObject((JsonObject) JsonUtil.read(JsonObject.class));
        setOpenAiJsonObject((OpenAiJsonObject) JsonUtil.read("openai.json", OpenAiJsonObject.class));

        if (getOpenAiJsonObject() == null) {
            setOpenAiJsonObject(new OpenAiJsonObject(new HashMap<>(), new HashMap<>()));
        }

        // JDA
        JDABuilder build = JDABuilder.create(getDotenv().get("BOT_KEY"), EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.watching("Feito com ❤️ pelo Rodrigo!"))
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(
                        // This
                        new DiscordOpenAI(),

                        // Commands
                        new QuestionChatCommand(),
                        new ImageGeneratorCommand(),

                        new PingCommand(),
                        new ShutdownCommand(),

                        new SetupCommand(),

                        // Listeners
                        new CreateChatListener(),
                        new NewChatOpenAiListener(),
                        new ChatReceivedPromptListener(),
                        new CloseChatListener()
                );

        setJda(build.build());

        CommandListUpdateAction commands = getJda().updateCommands();

        commands.addCommands(
                Commands.slash("setup", "Escolha uma opção e a mensagem de setup será enviada no canal atual.")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                        .addOptions(new OptionData(OptionType.STRING, "option", "Escolha o tipo de setup que deseja realizar no canal atual.", true)
                                .addChoice("OpenAI", "setup-openai")),

                Commands.slash("question", "Faça uma questão ao ChatGPT 3")
                        .addOption(OptionType.STRING, "chatgpt-prompt-text", "Insira a sua pergunta", true)
                        .addOption(OptionType.STRING, "chatgpt-motor-model", "Opcional: Insira o nome do motor AI"),

                Commands.slash("image-generator", "Crie uma imagem AI")
                        .addOption(OptionType.STRING, "image-prompt-text", "Insira a descrição da imagem", true),

                Commands.slash("ping", "Ping do gateway atual"),

                Commands.slash("shutdown", "Desligue o sistema atual")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );

        commands.queue();

        getJda().awaitReady();

        // OpenAI System
        setOpenAiService(new OpenAiService(getDotenv().get("OPENAI_API_KEY"), Duration.ofMinutes(1)));
        setOpenAiHelper(new OpenAiHelper());

        if (getOpenAiService() != null) {
            LogUtil.log("API", "OpenAI API Connected.");
        }

        // Dev Build
        setDevBuild(Boolean.getBoolean(getDotenv().get("DEV_BUILD", "false")));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LogUtil.log("STATUS", "Successfully Started.");
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        JsonUtil.write("openai.json", getOpenAiJsonObject());

        LogUtil.log("STATUS", "Good Bye!");
    }
}