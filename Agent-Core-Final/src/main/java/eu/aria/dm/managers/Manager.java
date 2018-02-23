package eu.aria.dm.managers;

//import hmi.winger.flipperextensions.ManageableBehaviourClass;
//import hmi.winger.flipperextensions.ManageableFunction;

import java.util.Map;

/**
 * Created by WaterschootJB on 19-5-2017.
 */
public interface Manager {

    public void process();

//    public long timeUntilNextProcess();
//
//    /**
//     * Returns the activation interval of the manager
//     * @return the interval
//     */
//    public long getInterval();
//
        String getName();
        void setName(String name);
        String getID();
        void setID(String id);
//
//    /**
//     * Sets the interval of the manager
//     * @param ms, time in milliseconds
//     */
//    void setInterval(int ms);
//
//    /**
//     * Add a template file from which the manager must read rules
//     * @param templatePath, the filepath of the the template
//     */
//    void addTemplateFile(String templatePath);
//
//    /**
//     * Parses parameters of a custom class and calls them.
//     * @param namedValues, the calls
//     * @param namedLists, the lists of arguments
//     */
//    void setParams(Map<String, String> namedValues, Map<String, String[]> namedLists);
//
//    /**
//     * Add a custom Java function to the manager
//     * @param functionInstance, functions to call from the manager
//     */
//    void addFunction(ManageableFunction functionInstance);
//
//    /**
//     * With this function you can add an extra behaviour class to the manager
//     *
//     * @param name
//     * @param instance
//     */
//    void addGlobalBehaviour(String name, ManageableBehaviourClass instance);
}
