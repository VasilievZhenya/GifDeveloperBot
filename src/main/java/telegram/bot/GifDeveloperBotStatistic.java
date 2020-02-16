package telegram.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import statistics.database.executor.PostgresExecutor;

import java.math.BigInteger;
import java.util.Optional;

import static java.lang.Thread.sleep;

/**
 * Selects bot usage statistic from database and sends it to chat with bot creator.
 */
public class GifDeveloperBotStatistic extends GifDeveloperBot {
	private static final Logger log = LoggerFactory.getLogger(GifDeveloperBotStatistic.class);
	private PostgresExecutor executor = new PostgresExecutor();
	private String creatorChatId;

	public GifDeveloperBotStatistic() {
		creatorChatId = System.getenv("creatorChatId");
	}

	/**
	 * Sends bot usage statistic to chat with bot creator.
	 *
	 * @param data usage statistic
	 */
	public void sendDailyStatistic(BigInteger data) {
		if (creatorChatId != null) {
			String text = String.format("Usage for the last 24 hours: %d", Optional.ofNullable(data).orElse(BigInteger.ZERO));
			SendMessage message = new SendMessage(creatorChatId, text);
			try {
				execute(message);
			} catch (TelegramApiException e) {
				log.error("Failed to send bot statistic.", e);
			}
		}
	}

	/**
	 * Every 24 hour select bot usage statistic and sends it to chat with bot creator.
	 * This work in separate thread.
	 */
	public void start() {
		Thread thread = new Thread(() -> {
			while (true) {
				sendDailyStatistic(executor.getStatistic());
				try {
					sleep(1000 * 3600 * 24);
				} catch (InterruptedException e) {
					log.error("Failed to sleep thread.", e);
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
}
