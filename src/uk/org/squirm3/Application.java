package uk.org.squirm3;

import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.LocalEngine;
import uk.org.squirm3.ui.GUI;
import uk.org.squirm3.ui.Resource;

public final class Application 
{
	static private final String translationsDirectory = "/translations";
	static private final String levelsTranslationFilePath = translationsDirectory+"/levels";
	static private final String interfaceTranslationFilePath = translationsDirectory+"/interface";
	
	static final private Properties levelsProps = new Properties();
	static final private Properties interfaceProps = new Properties();;
	
	public Application(final JApplet applet, String language) {
		initTranslator(language);
		final IApplicationEngine iApplicationEngine = new LocalEngine();
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	GUI.createGUI(iApplicationEngine, applet);
	        }
	    });
	}
	
	public Application(String language) {
		this(null,language);
	}
	
	static private void initTranslator(String language) {
		// use files in the specified language
		// for more information
		// http://www.loc.gov/standards/iso639-2/englangn.html
		try{
			levelsProps.load(Resource.class.getResourceAsStream(levelsTranslationFilePath+"_"+language+".properties"));
		    interfaceProps.load(Resource.class.getResourceAsStream(interfaceTranslationFilePath+"_"+language+".properties"));
			
		} catch(Exception e) {// use default files
			try {
				levelsProps.load(Resource.class.getResourceAsStream(levelsTranslationFilePath+".properties"));
				interfaceProps.load(Resource.class.getResourceAsStream(interfaceTranslationFilePath+".properties"));
			} catch(Exception ex) {;}
		}
	}
	
	static public String localize(String[] code) {
		final String bundle = code[0];
		String key = code[1];
		for( int i = 2 ; i<code.length ; i++) {
			key += "."+code[i];
		}
		if(bundle.equals("levels")) {
			return levelsProps.getProperty(key,"ERROR : STRING NOT FOUND!");
		} else if(bundle.equals("interface")) {
			return interfaceProps.getProperty(key,"ERROR : STRING NOT FOUND!");
		} else return bundle+"/"+key;
	}
	
	// if you want to run the organic builder as an application
	public static void main(String argv[]) {
		String horizontalBar = "*********************************************";
		System.out.println();
		System.out.println(horizontalBar);
		if(argv==null || argv.length==0 || (argv[0].length()==2 && argv[0].charAt(0)!='-')) {
			System.out.println("Starting the Organic Builder...");
			new Application((argv==null || argv.length==0)?null:argv[0]);
			System.out.println("Organic Builder is running.");
		} else {
			String parameter = argv[0].toLowerCase();
			if(parameter.equals("-help") || parameter.equals("-h")) {
				printOutFile("help.txt");
			}else if(parameter.equals("-about")) {
				printOutFile("about.txt");
			}else if(parameter.equals("-license")) {
				printOutFile("gpl.txt");
			}else {
				System.err.println("Wrong parameters!");
				System.out.println("To display the help use : java -jar OrganicBuilder.jar -help");
			}
		}
		System.out.println(horizontalBar);
		System.out.println();
	}
	
	
	private static void printOutFile(String name) {
		System.out.print(Resource.getFileContent(name));
	}
}