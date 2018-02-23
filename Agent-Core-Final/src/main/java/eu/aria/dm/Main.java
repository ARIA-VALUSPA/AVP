package eu.aria.dm;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import hmi.flipper2.FlipperException;
import hmi.flipper2.launcher.FlipperLauncher;
import hmi.flipper2.launcher.FlipperLauncherThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author WaterschootJB
 */
public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class.getName());
    private static FlipperLauncherThread flt;
    private static String host = "localhost";
    private static String port = "61616";
    private static String topic = "SSI";


    public static void main(String[] args) throws FlipperException, IOException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        ClassLoader loader = Main.class.getClassLoader();
        context.reset();

        try {
            URL ariaFlipper = loader.getResource("ARIAFlipper.xml");
            if (null == ariaFlipper)
                throw new IOException("I couldn't find the `ARIAFlipper.xml` - likely resources aren't setup correctly");
            configurator.doConfigure(ariaFlipper);
        } catch (JoranException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage(),e);
        }
        StatusPrinter.printIfErrorsOccured(context);

        String help = "Expecting commandline arguments in the form of \"-<argname> <arg>\".\nAccepting the following argnames: config";
        String flipperPropFile = "aria.properties";

        if(args.length % 2 != 0){
            System.err.println(help);
            System.exit(0);
        }

        for(int i = 0; i < args.length; i = i + 2){
            if(args[i].equals("-config")) {
                flipperPropFile = args[i+1];
            } else {
                System.err.println("Unknown commandline argument: \""+args[i]+" "+args[i+1]+"\".\n"+help);
                System.exit(0);
            }
        }

        Properties ps = new Properties();
        InputStream flipperPropStream = FlipperLauncher.class.getClassLoader().getResourceAsStream(flipperPropFile);

        try {
            ps.load(flipperPropStream);
        } catch (IOException ex) {
            logger.warn("Could not load flipper settings from "+flipperPropFile);
            ex.printStackTrace();
        }
        // If you want to check templates based on events (i.e. messages on middleware),
        // you can run  flt.forceCheck(); from a callback to force an immediate check.
        logger.debug("FlipperLauncher: Starting Thread");
        flt = new FlipperLauncherThread(ps);
        flt.start();

        //flt.stopGracefully();
    }
}
