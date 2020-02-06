package service.gif;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FrameCreator {
    private static final int HEIGHT = 100;
    private static final int WIDTH = 200;

    public List<BufferedImage> createFrames(List<String> letters) {
        final int size = letters.size();
        List<BufferedImage> frames = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            String textToPrint = letters.stream().limit(i + 1).collect(Collectors.joining(""));
            BufferedImage frame = create(textToPrint);
            frames.add(frame);
            System.out.println(textToPrint);
        }

        return frames;
    }

    private BufferedImage create(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        graphics.drawString(text, 20, 20);
        return image;
    }
}
