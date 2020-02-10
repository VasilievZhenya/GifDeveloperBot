import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import telegram.bot.GifMakerBot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        log.info("Telegram bot started.");
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new GifMakerBot());
        } catch (TelegramApiRequestException e) {
            log.error("Telegram api exception.", e);
        }

        try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(System.getenv("PORT")))) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
            }
        } catch (IOException e) {
            log.error("Error while listening port.", e);
        }
    }
}
