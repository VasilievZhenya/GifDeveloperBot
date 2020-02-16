package service.gif;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;

/**
 * Creates gif file from generated Buffered Images filled with given text inside.
 * The main logic was taken from https://codereview.stackexchange.com/questions/113998/making-gifs-with-java
 */
public class GifCreator {
	private static final int DELAY = 30;
	private static final int LENGTH_PER_LINE = 25;
	private static final int MAX_ROWS_AMOUNT = 9;
	private static final int MAX_TEXT_LENGTH = LENGTH_PER_LINE * MAX_ROWS_AMOUNT;
	private static final String WORD_IS_TOO_LONG = "Single word length cannot be more than {0} symbols.";
	private static final String TEXT_IS_TOO_LONG = "Text should be less than {0} symbols.";
	private static final File OUTPUT_FILE = new File(String.format("res%s.gif", new Date().getTime()));

	private FrameCreator frameCreator = new FrameCreator();

	/**
	 * Creates gif file with given text printed.
	 *
	 * @param text input text
	 * @return gif file
	 * @throws InputMismatchException if the whole text or single word is too long
	 * @throws IOException
	 * @see GifCreator#MAX_TEXT_LENGTH
	 * @see GifCreator#LENGTH_PER_LINE
	 */
	public File createGif(String text) throws InputMismatchException, IOException {
		List<String> lines = splitByLines(text);
		List<BufferedImage> frames = frameCreator.createFrames(lines);

		generateFromBI(frames, DELAY, true);

		return OUTPUT_FILE;
	}

	/**
	 * Splits input text into lines to be printed.
	 *
	 * @param text input text
	 * @return list of lines to be printed
	 * @throws InputMismatchException if the whole text or single word is too long
	 * @see GifCreator#MAX_TEXT_LENGTH
	 * @see GifCreator#LENGTH_PER_LINE
	 */
	private List<String> splitByLines(String text) throws InputMismatchException {
		if (text.length() > MAX_TEXT_LENGTH) {
			throw new InputMismatchException(MessageFormat.format(TEXT_IS_TOO_LONG, MAX_TEXT_LENGTH));
		}
		String[] words = text.trim().split("\\s");
		StringBuilder line = new StringBuilder();
		List<String> lines = new ArrayList<>();
		for (String word : words) {
			int wordLength = word.length();
			if (wordLength > LENGTH_PER_LINE) {
				throw new InputMismatchException(MessageFormat.format(WORD_IS_TOO_LONG, LENGTH_PER_LINE));
			}

			int lineLength = line.length();
			if (lineLength + wordLength + 1 < LENGTH_PER_LINE) {
				if (lineLength != 0) {
					line.append(" ");
				}
				line.append(word);
			} else {
				lines.add(line.toString());
				line = new StringBuilder(word);
			}
		}

		if (line.length() > 0) {
			lines.add(line.toString());
		}

		return lines;
	}

	/**
	 * Generate gif from BufferedImage list
	 * Make the gif loopable if loop is true
	 * Set the delay for each frame according to the delay, 100 = 1s
	 *
	 * @param images
	 * @param delay
	 * @param loop
	 * @throws IOException
	 */
	public void generateFromBI(List<BufferedImage> images, int delay, boolean loop) throws IOException {
		ImageWriter gifWriter = getWriter();
		ImageOutputStream ios = getImageOutputStream();
		IIOMetadata metadata = getMetadata(gifWriter, delay, loop);

		gifWriter.setOutput(ios);
		gifWriter.prepareWriteSequence(null);
		for (BufferedImage img : images) {
			IIOImage temp = new IIOImage(img, null, metadata);
			gifWriter.writeToSequence(temp, null);
		}
		gifWriter.endWriteSequence();

		ios.close();
	}

	/**
	 * Retrieve gif writer
	 *
	 * @return
	 * @throws IIOException
	 */
	private ImageWriter getWriter() throws IIOException {
		Iterator<ImageWriter> itr = ImageIO.getImageWritersByFormatName("gif");
		if (itr.hasNext()) {
			return itr.next();
		}

		throw new IIOException("GIF writer doesn't exist on this JVM!");
	}

	/**
	 * Retrieve output stream from the OUTPUT_FILE.
	 *
	 * @return
	 * @throws IOException
	 * @see GifCreator#OUTPUT_FILE
	 */
	private ImageOutputStream getImageOutputStream() throws IOException {
		return ImageIO.createImageOutputStream(OUTPUT_FILE);
	}

	/**
	 * Prepare metadata from the user input, add the delays and make it loopable
	 * based on the method parameters
	 *
	 * @param writer
	 * @param delay
	 * @param loop
	 * @return
	 * @throws IIOInvalidTreeException
	 */
	private IIOMetadata getMetadata(ImageWriter writer, int delay, boolean loop)
			throws IIOInvalidTreeException {
		// Get the whole metadata tree node, the name is javax_imageio_gif_image_1.0
		// Not sure why I need the ImageTypeSpecifier, but it doesn't work without it
		ImageTypeSpecifier img_type = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		IIOMetadata metadata = writer.getDefaultImageMetadata(img_type, null);
		String native_format = metadata.getNativeMetadataFormatName();
		IIOMetadataNode node_tree = (IIOMetadataNode) metadata.getAsTree(native_format);

		// Set the delay time you can see the format specification on this page
		// https://docs.oracle.com/javase/7/docs/api/javax/imageio/metadata/doc-files/gif_metadata.html
		IIOMetadataNode graphics_node = getNode("GraphicControlExtension", node_tree);
		graphics_node.setAttribute("delayTime", String.valueOf(delay));
		graphics_node.setAttribute("disposalMethod", "none");
		graphics_node.setAttribute("userInputFlag", "FALSE");

		if (loop) {
			makeLoopy(node_tree);
		}

		metadata.setFromTree(native_format, node_tree);

		return metadata;
	}

	/**
	 * Add an extra Application Extension node if the user wants it to be loopable
	 *
	 * @param root
	 */
	private void makeLoopy(IIOMetadataNode root) {
		IIOMetadataNode app_extensions = getNode("ApplicationExtensions", root);
		IIOMetadataNode app_node = getNode("ApplicationExtension", app_extensions);

		app_node.setAttribute("applicationID", "NETSCAPE");
		app_node.setAttribute("authenticationCode", "2.0");
		app_node.setUserObject(new byte[]{0x1, 0, 0});

		app_extensions.appendChild(app_node);
		root.appendChild(app_extensions);
	}

	/**
	 * Retrieve the node with the name from the parent root node
	 * Append the node if the node with the given name doesn't exist
	 *
	 * @param node_name
	 * @param root
	 * @return
	 */
	private IIOMetadataNode getNode(String node_name, IIOMetadataNode root) {
		IIOMetadataNode node;

		for (int i = 0; i < root.getLength(); i++) {
			if (root.item(i).getNodeName().compareToIgnoreCase(node_name) == 0) {
				node = (IIOMetadataNode) root.item(i);
				return node;
			}
		}

		// Append the node with the given name if it doesn't exist
		node = new IIOMetadataNode(node_name);
		root.appendChild(node);

		return node;
	}
}
