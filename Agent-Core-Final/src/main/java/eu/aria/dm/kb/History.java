package eu.aria.dm.kb;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by WaterschootJB on 4-7-2017.
 */
public class History {

    ArrayList<Dialogue> d;
    private String participant;

    public History(String participant, String location){
        this.participant = participant;
        d = readHistory(participant,location);

    }

    /**
     * Writer method for writing a list of dialogues to a file
     * @param participant, the participant of the dialogue
     * @param location, the file location to write to
     * @param d, the dialogue list
     * @return if the operation was successful
     */
    public static boolean writeHistory(String participant, String location, ArrayList<Dialogue> d){
        try {
            FileOutputStream fou = new FileOutputStream(location);
            ObjectOutputStream dialogue = new ObjectOutputStream(fou);
            dialogue.writeChars(participant);
            dialogue.writeObject(d);
            dialogue.flush();
            dialogue.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reader method for reading dialogues from history
     * @param participant, the participant of the dialogue
     * @param location, the file location to read from
     * @return, the dialogue list
     */
    public static ArrayList<Dialogue> readHistory(String participant, String location){
        ArrayList<Dialogue> d = new ArrayList();
        try{
            FileInputStream fiu = new FileInputStream(location);
            ObjectInputStream dialogue = new ObjectInputStream(fiu);
            d = (ArrayList<Dialogue>) dialogue.readObject();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return d;
    }

    public ArrayList<Dialogue> getD() {
        return d;
    }

    public String getParticipant() {
        return participant;
    }
}
