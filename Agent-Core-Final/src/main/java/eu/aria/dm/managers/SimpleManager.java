package eu.aria.dm.managers;

/**
 * Created by WaterschootJB on 22-5-2017.
 */
public class SimpleManager implements Manager {

    protected long interval;
    protected long previousTime;
    protected String name;
    protected String id;
    InputManager gm;

    public SimpleManager(){

    }

    @Override
    public void process() {

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public void setID(String id) {
        this.id = id;

    }
}
