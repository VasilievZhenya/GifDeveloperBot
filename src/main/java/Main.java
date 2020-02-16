import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import telegram.bot.GifDeveloperBot;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        log.info("Telegram bot started.");
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        GifDeveloperBot bot = new GifDeveloperBot();

        try {
            api.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            log.error("Telegram api exception.", e);
        }

        bot.collectStatistic();
    }
}
