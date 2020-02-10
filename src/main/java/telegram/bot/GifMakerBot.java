package telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.gif.GifCreator;
import service.props.PropertiesReader;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;

public class GifMakerBot extends TelegramLongPollingBot {
	private static final Logger log = LoggerFactory.getLogger(GifMakerBot.class);
	private static final String PROPERTIES_PATH = "config/gifDeveloperBot.properties";
	private GifCreator gifCreator = new GifCreator();
	private String botUsername;
	private String botToken;

	public GifMakerBot() {
		//Properties properties = PropertiesReader.readProperties(PROPERTIES_PATH);
		botUsername = System.getenv("botUsername");
		botToken = System.getenv("botToken");
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			Message message = update.getMessage();
			if (message.hasText()) {
				long chatId = message.getChatId();
				try {
					try {
						File gif = gifCreator.createGif(message.getText());
						SendAnimation response = new SendAnimation().setAnimation(gif).setChatId(chatId);
						execute(response);
					} catch (InputMismatchException e) {
						SendMessage response = new SendMessage(chatId, e.getMessage());
						execute(response);
					} catch (IOException e) {
						log.error("Failed to create gif.", e);
					}
				} catch (TelegramApiException e) {
					log.error("Failed to execute response.", e);
				}
			}
		}
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}
}
