package eu.aria.output;

import vib.core.util.IniManager;

/**
 * Created by adg on 17/08/2016.
 *
 */
public class Main {

    public static void main(String[] args) {
        try {javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {}

        IniManager iniManager = new IniManager("Agent-Output.ini");
        Greta greta = new Greta();
        greta.init(iniManager);
    }
}
