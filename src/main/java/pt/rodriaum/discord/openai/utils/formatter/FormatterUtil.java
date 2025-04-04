package pt.rodriaum.discord.openai.utils.formatter;

import java.time.format.DateTimeFormatter;

public class FormatterUtil {

    public static String formatNumber(long number) {
        String s = String.valueOf(number);

        if (s.endsWith(".0"))
            s = s.replace(".0", "");

        if (s.length() == 1)
            s = "0" + s;

        return s;
    }

    public static String formatMs(long ms) {
        return (System.currentTimeMillis() - ms) + "ms";
    }

    public static String formatS(long ms) {
        return ((System.currentTimeMillis() - ms) / 1000) + "s";
    }

    public static DateTimeFormatter formatDate() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public static DateTimeFormatter formatDateTime() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }
}
