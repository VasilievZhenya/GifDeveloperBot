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
import statistics.BotStatistics;
import statistics.database.executor.PostgresExecutor;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;

/**
 * This bot creates gifs and sends them to user with specified text filled.
 */
public class GifDeveloperBot extends TelegramLongPollingBot {
	private static final Logger log = LoggerFactory.getLogger(GifDeveloperBot.class);
	private GifCreator gifCreator = new GifCreator();
	private String botUsername;
	private String botToken;


	public GifDeveloperBot() {
		botUsername = System.getenv("botUsername");
		botToken = System.getenv("botToken");
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			Message message = update.getMessage();
			if (message.hasText()) {
				String text = message.getText();
				switch (text) {
					case "/stat":
						new GifDeveloperBotStatistic().sendDailyStatistic(new PostgresExecutor().getStatistic());
						return;
				}
				long chatId = message.getChatId();
				addRecord();
				try {
					try {
						File gif = gifCreator.createGif(text);
						SendAnimation response = new SendAnimation().setAnimation(gif).setChatId(chatId);
						execute(response);
						gif.delete();
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

	/**
	 * Adds new record to Records table if new request received.
	 */
	private void addRecord() {
		BotStatistics botStatistics = new BotStatistics();
		botStatistics.setDaemon(true);
		botStatistics.start();
	}

	/**
	 * Starts sending bot usage statistic on every 24 hour.
	 */
	public void collectStatistic() {
		GifDeveloperBotStatistic statistic = new GifDeveloperBotStatistic();
		statistic.start();
	}
}
