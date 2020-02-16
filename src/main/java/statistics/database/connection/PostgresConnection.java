package statistics.database.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Retrieves connection with remote Postgres server.
 */
public class PostgresConnection {
	private static final Logger log = LoggerFactory.getLogger(PostgresConnection.class);

	/**
	 * Gets database connection according to parameters.
	 *
	 * @return Connection instance
	 */
	public Connection getConnection() {
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(
					System.getenv("url"),
					System.getenv("user"),
					System.getenv("password")
			);
		} catch (SQLException e) {
			log.error("Failed to get connection.", e);
		}

		return connection;
	}
}
