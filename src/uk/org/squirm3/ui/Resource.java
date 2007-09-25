package uk.org.squirm3.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;


/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

//*********************************************************
//class Resource : use to get pictures or to log something
// all thingd related to I/O
//*********************************************************
public class Resource
{
		
	private static final String spikyImageName = "spiky.gif";
	private static Image spikyImage;
	
	private static BufferedImage loadCompatibleImage(String name) throws IOException {
		URL url = Resource.class.getResource("/pictures/"+name);
	    BufferedImage image = ImageIO.read(url);
	    GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	    BufferedImage compatibleImage = configuration.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);
	    //BufferedImage compatibleImage = configuration.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);
	    Graphics g = compatibleImage.getGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return compatibleImage;
	  }
		
	public static void loadPictures() { //TODO find a better way
		// listener
		JFrame f = new JFrame();
		spikyImage = loadImage(spikyImageName,f);
	}
		
	// used to load an image from the jar into an Image structure
	private static Image loadImage(String name, Component c) {
		boolean old_version = true;
		if(old_version) { //TODO make a benchmark or something
			// in order to chose the most efficent method
			URL url = Resource.class.getResource("/pictures/"+name);
			Image img = c.getToolkit().createImage(url);
			try {
				MediaTracker tracker = new MediaTracker(c);
				tracker.addImage(img, 0);
				tracker.waitForID(0);
			} catch (Exception e) {}
			return img;
		} else {
			try {
				return loadCompatibleImage(name);
			} catch (Exception e) {}
		}
		return null;
	}

	// get the image
	public static Image getSpikyImage() {
		return spikyImage;
	}
	
	public static Icon getIcon(String name) {
		URL url = Resource.class.getResource("/icons/"+name+".png");
		return new ImageIcon( Toolkit.getDefaultToolkit().createImage(url));
	}
	
	// return the content of the file
	public static String getFileContent(String fileName) {
		StringBuffer sb = new StringBuffer();
		InputStream is;
		try {
			is = Resource.class.getResourceAsStream("/"+fileName); 
			int read;
			while((read=is.read())!=-1) {
				sb.append((char)read);
			}
			is.close();
		} catch(Exception e) {
			System.out.println("error : "+e);
			e.printStackTrace();
		}
		if(sb!=null) return sb.toString();
		return "ERROR : "+fileName;
	}
}
