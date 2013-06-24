package org.robminfor.swing;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLoader {

	// TODO use a proper Guava cache for these
	// better memory mangement etc
	private static Map<String, Image> cache = new HashMap<String, Image>();

	public static Image getImage(String name) throws IOException {
		if (cache.containsKey(name)) {
			return cache.get(name);
		} else {
			String filename = "/img/" + name + ".png";
			URL fileurl = System.class.getResource(filename);
			if (fileurl == null) {
				throw new IOException("Unable to find resource " + filename);
			}
			Image image = ImageIO.read(fileurl);
			cache.put(name, image);
			return image;
		}
	}

}
