package statistics;

import statistics.database.executor.PostgresExecutor;

/**
 * Adds new record to Record table.
 */
public class BotStatistics extends Thread {
	private PostgresExecutor executor = new PostgresExecutor();

	/**
	 * Adds new record to Record table.
	 * This method runs in separate thread.
	 */
	@Override
	public void run() {
		executor.addRecord();
	}
}
