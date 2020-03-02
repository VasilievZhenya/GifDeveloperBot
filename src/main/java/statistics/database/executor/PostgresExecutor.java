package statistics.database.executor;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import statistics.database.connection.PostgresConnection;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

/**
 * Executes operations on Postgres database.
 */
public class PostgresExecutor {
	private static final Logger log = LoggerFactory.getLogger(PostgresExecutor.class);
	private PostgresConnection postgresConnection = new PostgresConnection();

	/**
	 * Inserts into Records table new field.
	 * If key duplicate, then it updates amount as amount += 1.
	 */
	public void addRecord() {
		try (Connection connection = postgresConnection.getConnection()) {
			QueryRunner runner = new QueryRunner();
			String sql = "insert into records values (?, 1) on conflict (record_date) do update set amount = records.amount + 1";
			runner.execute(connection, sql, new java.sql.Date(new Date().getTime()));
		} catch (SQLException e) {
			log.error("Failed to insert record.", e);
		}
	}

	/**
	 * Returns amount column for the current_date key.
	 *
	 * @return value of amount column
	 */
	public BigInteger getStatistic() {
		BigInteger amount = null;
		try (Connection connection = postgresConnection.getConnection()) {
			QueryRunner runner = new QueryRunner();
			String sql = "select amount from records where record_date = current_date";
			Long res = runner.query(connection, sql, new ScalarHandler<>());
			amount = BigInteger.valueOf(Optional.ofNullable(res).orElse(0L));
		} catch (SQLException e) {
			log.error("Failed to get statistic.", e);
		}

		return amount;
	}
}
