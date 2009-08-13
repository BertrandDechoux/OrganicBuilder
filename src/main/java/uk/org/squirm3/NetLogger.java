package uk.org.squirm3;

import uk.org.squirm3.data.Reaction;

import java.util.Collection;
import java.util.Iterator;
import java.net.URL;
import java.net.URLConnection;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * ${my.copyright}
 */ // NetLogger : an implementation of the ILogger interface
public class NetLogger implements ILogger {

    private final String url;

    public NetLogger(String url) {
        this.url = url;
    }

    public void writeSolution(int levelNumber, Collection reactions) {
        if (levelNumber > 0) // do you want to log the solution or not?
        {
            try {
                URL url = new URL(this.url);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                connection.setConnectTimeout(100); // don't wait too long

                PrintWriter out = new PrintWriter(connection.getOutputStream());
                // chalenge number
                out.println(String.valueOf(levelNumber));
                // number of reactions
                out.println(String.valueOf(reactions.size()));
                //TODO size is the number of reactions or the number of possibles reactions ? (size!=length)
                Iterator it = reactions.iterator();
                while (it.hasNext()) out.println(((Reaction) it.next()).toString());
                out.close();
                //to read (debug)
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) System.out.println(inputLine);
                in.close();
            }
            // it doesn't matter too much if we couldn't connect, just skip it
            // catch all exceptions : MalformedURLException, IOException and others
            catch (Exception error) {
            }
        }
    }

}
