package eu.aria.dm.gui;


import eu.aria.dm.managers.Manager;
import eu.aria.dm.util.Say;

import java.util.ArrayList;

public class BehaviourToGUI{
    GUIController gui;

    Manager m;

    private String informationState;

    private String agentName = "Agent";

    public void execute(ArrayList<String> argNames, ArrayList<String> argValues) {

        Say newSay = new Say(argValues.get(0), agentName, true);
        if(this.gui == null)
        {
            this.gui = GUIController.getInstance(informationState);
        }
        gui.addAgentSay(newSay);

    }

}
