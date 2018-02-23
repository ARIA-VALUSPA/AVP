package eu.aria.dm.managers;

import eu.aria.dm.gui.GUIController;
import eu.aria.dm.util.Say;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by WaterschootJB on 3-7-2017.
 */
public class GUIManager extends SimpleManager{

    private static Logger logger = LoggerFactory.getLogger(GUIManager.class.getName());
    private String informationState;
    GUIController controller;
    public GUIManager() {
        // Enforce GUI
        controller = GUIController.getInstance(informationState);
    }

    public void setIS(String informationState){
        this.informationState = informationState;
    }


    public void process(){
        controller.addUserSay(new Say("Hello","User",true));
        logger.info("Added a user say");
    }

}
