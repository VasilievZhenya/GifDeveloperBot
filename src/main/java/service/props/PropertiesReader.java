package service.props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Properties;

public class PropertiesReader {
	private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);
	private static final String FAILED_TO_READ_PROPERTIES = "Failed to read properties: {0}";

	/**
	 * Reads properties file by a given path.
	 *
	 * @param path path to properties
	 * @return read properties
	 */
	public static Properties readProperties(String path) {
		Properties properties = new Properties();
		try (Reader reader = new FileReader(path)) {
			properties.load(reader);
		} catch (IOException e) {
			log.error(MessageFormat.format(FAILED_TO_READ_PROPERTIES, path), e);
		}

		return properties;
	}
}
