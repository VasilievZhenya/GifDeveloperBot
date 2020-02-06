import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import service.gif.FrameCreator;
import service.gif.GifCreator;
import telegram.bot.GifMakerBot;

public class Main {
    public static void main(String[] args) {
        //new GifCreator().createGif("hello");

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new GifMakerBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
