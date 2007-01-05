package uk.org.squirm3;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.LocalEngine;
import uk.org.squirm3.ui.GUI;
import uk.org.squirm3.ui.Resource;

public final class Application 
{
	static private final String translationsDirectory = "translations";
	static private final String levelsTranslationFilePath = translationsDirectory+"/levels";
	static private final String interfaceTranslationFilePath = translationsDirectory+"/interface";
	
	static private ResourceBundle levelsRB;
	static private ResourceBundle interfaceRB;
	
	public Application(final JApplet applet) {
		initTranslator();
		final IApplicationEngine iApplicationEngine = new LocalEngine();
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	GUI.createGUI(iApplicationEngine, applet);
	        }
	    });
	}
	
	public Application() {
		this(null);
	}
	
	static private void initTranslator() {
		// if we have several possibilities, we might ask the user and then use
		// Locale.setDefault(newLocale);
		Locale currentLocale =  Locale.getDefault();
        levelsRB = ResourceBundle.getBundle(levelsTranslationFilePath, currentLocale);
        interfaceRB = ResourceBundle.getBundle(interfaceTranslationFilePath, currentLocale);
	}
	
	static public String localize(String[] code) {
		final String bundle = code[0];
		String key = code[1];
		for( int i = 2 ; i<code.length ; i++) {
			key += "."+code[i];
		}
		if(bundle.equals("levels")) {
			return levelsRB.getString(key);
		} else if(bundle.equals("interface")) {
			return interfaceRB.getString(key);
		} else return bundle+"/"+key;
	}
	
	// if you want to run the organic builder as an application
	public static void main(String argv[]) {
		if(argv==null || argv.length==0) {
			new Application();
			return;
		} else {
			String parameter = argv[0].toLowerCase();
			String horizontalBar = "*********************************************";
			if(parameter.equals("-help") || parameter.equals("-h")) {
				System.out.println();
				System.out.println(horizontalBar);
				System.out.println("To run this application type : java -jar OrganicBuilder.jar -help");
				System.out.println("To display this help use : java -jar OrganicBuilder.jar -help");
				System.out.println("To display information about this application : java -jar OrganicBuilder.jar -about");
				System.out.println("To display the full license of this application : java -jar OrganicBuilder.jar -license");
				System.out.println("You can't choose yet by parameters the user interface or the engine used.");
				System.out.println(horizontalBar);
				System.out.println();
				return;
			}
			if(parameter.equals("-about")) {
				System.out.println();
				System.out.println(horizontalBar);
				printOutFile("about.txt");
				System.out.println();
				System.out.println("Full text of GPL license using the -license parameter.");
				System.out.println(horizontalBar);
				System.out.println();
				return;
			}
			if(parameter.equals("-license")) {
				System.out.println();
				System.out.println(horizontalBar);
				printOutFile("license.txt");
				System.out.println(horizontalBar);
				System.out.println();
				return;
			}			
		}
		System.err.println("Wrong parameters!");
		return;
	}
	
	private static void printOutFile(String name) {
		System.out.print(Resource.getFileContent(name));
	}
}