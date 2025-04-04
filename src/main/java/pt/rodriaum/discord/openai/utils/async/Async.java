package pt.rodriaum.discord.openai.utils.async;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {
    @Getter
    private static final ExecutorService service = Executors.newCachedThreadPool();
}
