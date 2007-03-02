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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import uk.org.squirm3.data.Reaction;

//*********************************************************
//class Resource : use to get pictures or to log something
// all thingd related to I/O
//*********************************************************
public class Resource
{
	private static final String[] TYPE_IMAGE_FILENAMES = {"yellow.gif","grey.gif","blue.gif","purple.gif","red.gif","green.gif"};
	private static final Image[] TYPE_IMAGES = new Image[TYPE_IMAGE_FILENAMES.length];
		
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
		
	public static void loadPictures()
	{
		JFrame f = new JFrame();
		for(byte i=0;i<TYPE_IMAGE_FILENAMES.length;i++) TYPE_IMAGES[i]= loadImage(TYPE_IMAGE_FILENAMES[i],f);
		spikyImage = loadImage(spikyImageName,f);
	}
		
	// used to load an image from the jar into an Image structure
	private static Image loadImage(String name, Component c) {
		boolean old_version = true;
		if(old_version) { //TODO make a benchmark or something
			// in order to chose the most efficent method
			URL url = Resource.class.getResource("/pictures/"+name);
			Image img = c.getToolkit().getImage(url);
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
		
	// get the image of the atom with type equals to "type"
	public static Image getAtomImageOfType(byte type) {
		return TYPE_IMAGES[type];
	}
	
	public static Icon getIcon(String name) {
		URL url = Resource.class.getResource("/icons/"+name+".png");
		return new ImageIcon( Toolkit.getDefaultToolkit().getImage(url));
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
		
	public static void logSolution(int levelNumber, Vector reactions){
	  if(levelNumber>0) // do you want to log the solution or not?
	  {
		 try {
			  URL url = new URL("http://www.sq3.org.uk/Evolution/Squirm3/OrganicBuilder/logger.pl");
			  URLConnection connection = url.openConnection();
			  connection.setDoOutput(true);
			  
			  PrintWriter out = new PrintWriter(connection.getOutputStream());
			  out.println("Organic Builder CVS version (current state : refactoring)");
			  out.println("challenge "+String.valueOf(levelNumber+" solved"));
			  Iterator it = reactions.iterator();
			  while(it.hasNext()) out.println(((Reaction)it.next()).getString());
			  out.close();
			  
			  BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			  String inputLine;
			  while ((inputLine = in.readLine()) != null);
			  in.close();
		  } 
		  // it doesn't matter too much if we couldn't connect, just skip it
		  catch (MalformedURLException error) { } catch (IOException error) {}
	  }
}
}
