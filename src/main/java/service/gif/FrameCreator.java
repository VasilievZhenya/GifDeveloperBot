package service.gif;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates list of Buffered images to be packed into gif.
 */
public class FrameCreator {
	private static final int HEIGHT = 200;
	private static final int WIDTH = 300;

	/**
	 * Creates list Buffered images with given text printed.
	 *
	 * @param linesToPrint list of lines of text to be printed
	 * @return list of Buffered images
	 */
	public List<BufferedImage> createFrames(List<String> linesToPrint) {
		final int size = linesToPrint.size();
		List<BufferedImage> frames = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			BufferedImage frame = createEmptyFrame();
			Graphics2D graphics = frame.createGraphics();
			setBackground(graphics, frame.getWidth(), frame.getHeight());
			for (int j = 0; j < i; ++j) {
				addLine(graphics, linesToPrint.get(j), (j + 1) * 20);
			}

			String newLine = linesToPrint.get(i);
			int len = newLine.length();
			for (int j = 0; j < len; ++j) {
				BufferedImage im = copy(frame);
				String textToPrint = newLine.substring(0, j + 1);
				addLine(im.createGraphics(), textToPrint, (i + 1) * 20);
				frames.add(im);
			}
		}

		return frames;
	}

	/**
	 * Creates empty buffered image.
	 *
	 * @return empty buffered image
	 */
	private BufferedImage createEmptyFrame() {
		return new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Makes image background color of white.
	 *
	 * @param graphics Graphics2D object taken from target image
	 * @param width    image width
	 * @param height   image height
	 */
	private void setBackground(Graphics2D graphics, int width, int height) {
		graphics.setPaint(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * Prints line of text to image.
	 *
	 * @param graphics Graphics2D object taken from target image
	 * @param text     text to print
	 * @param offset   pixels offset. This makes possible to print text on separate line
	 */
	private void addLine(Graphics2D graphics, String text, int offset) {
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		graphics.drawString(text, 10, offset);
	}

	/**
	 * Makes a copy of image.
	 *
	 * @param src source image
	 * @return copy of source image
	 */
	private BufferedImage copy(BufferedImage src) {
		ColorModel cm = src.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = src.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}
