package telegram.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.gif.GifCreator;

import java.io.File;
import java.io.InputStream;

public class GifMakerBot extends TelegramLongPollingBot {
    private GifCreator gifCreator = new GifCreator();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {

                //SendMessage response = new SendMessage(message.getChatId(), message.getText());
                InputStream in = gifCreator.createGif(message.getText());

                InputFile inputFile = new InputFile(in, "message");
                SendAnimation response = new SendAnimation().setAnimation(new File("res.gif")).setChatId(message.getChatId());
                try {
                    execute(response);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "GifDeveloperBot";
    }

    @Override
    public String getBotToken() {
        return "1076366689:AAFZbx3zp7FyMts4PT0d-Q8xnJUOn9ciOaA";
    }
}
