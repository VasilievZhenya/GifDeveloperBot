package service.gif;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GifCreator {
    private static final int MAX_TEXT_LENGTH = 100;
    private static final int DELAY = 30;

    public InputStream createGif(String text) {
        List<String> letters = splitBySymbols(text);
        List<BufferedImage> frames = new FrameCreator().createFrames(letters);

        byte[] bytes = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            generateFromBI(frames, DELAY, true, out);
            out.flush();
            bytes = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return new FileInputStream("res.gif");//new ByteArrayInputStream(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<String> splitBySymbols(String text) {
        while (text.length() > MAX_TEXT_LENGTH) {
            text = text.substring(0, text.lastIndexOf(" "));
        }

        return Arrays.asList(text.split(""));
    }

    // Generate gif from BufferedImage array
    // Make the gif loopable if loop is true
    // Set the delay for each frame according to the delay, 100 = 1s
    // Use the name given in String output for output file
    public void generateFromBI(List<BufferedImage> images, int delay, boolean loop, OutputStream out) throws IOException {
        ImageWriter gifWriter = getWriter();
        ImageOutputStream ios = getImageOutputStream(out);
        IIOMetadata metadata = getMetadata(gifWriter, delay, loop);

        gifWriter.setOutput(ios);
        gifWriter.prepareWriteSequence(null);
        for (BufferedImage img : images) {
            IIOImage temp = new IIOImage(img, null, metadata);
            gifWriter.writeToSequence(temp, null);
        }
        gifWriter.endWriteSequence();
    }

    // Retrieve gif writer
    private ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> itr = ImageIO.getImageWritersByFormatName("gif");
        if (itr.hasNext())
            return itr.next();

        throw new IIOException("GIF writer doesn't exist on this JVM!");
    }

    // Retrieve output stream from the given file name
    private ImageOutputStream getImageOutputStream(OutputStream out) throws IOException {
        File file = new File("res.gif");
        return ImageIO.createImageOutputStream(file);
    }

    // Prepare metadata from the user input, add the delays and make it loopable
    // based on the method parameters
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

        if (loop)
            makeLoopy(node_tree);

        metadata.setFromTree(native_format, node_tree);

        return metadata;
    }

    // Add an extra Application Extension node if the user wants it to be loopable
    // I am not sure about this part, got the code from StackOverflow
    // TODO: Study about this
    private void makeLoopy(IIOMetadataNode root) {
        IIOMetadataNode app_extensions = getNode("ApplicationExtensions", root);
        IIOMetadataNode app_node = getNode("ApplicationExtension", app_extensions);

        app_node.setAttribute("applicationID", "NETSCAPE");
        app_node.setAttribute("authenticationCode", "2.0");
        app_node.setUserObject(new byte[]{0x1, (byte) (0 & 0xFF), (byte) ((0 >> 8) & 0xFF)});

        app_extensions.appendChild(app_node);
        root.appendChild(app_extensions);
    }

    // Retrieve the node with the name from the parent root node
    // Append the node if the node with the given name doesn't exist
    private IIOMetadataNode getNode(String node_name, IIOMetadataNode root) {
        IIOMetadataNode node = null;

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
