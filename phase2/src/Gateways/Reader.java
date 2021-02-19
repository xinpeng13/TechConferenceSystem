package Gateways;

import UseCase.UserManager;
import UseCase.RoomManager;
import UseCase.EventManager;
import UseCase.ChatManager;
import UseCase.RequestManager;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * A class that reads data from a file and converts said data to their original form
 * @author Joyce Huang
 */
public class Reader {

    /**
     * A method that information from a previous session from a .txt file
     * @param filename the name of the file to open
     * @return a list of lists that contains all the data needed
     */
    public Object[] loadData(String filename) {
        File file = new File(filename);
        Object helper[] = new Object[5];
        try {
            if (file.exists()) {
                if (file.length() != 0) {
                    InputStream inputFile = new FileInputStream(file);
                    InputStream buffer = new BufferedInputStream(inputFile);
                    ObjectInput input = new ObjectInputStream(buffer);

                    if (verifySaves(filename)) {
                        for (int i = 0; i < 5; i++) {
                            helper[i] = input.readObject();
                        }
                    }
                    input.close();
                }
            }
        } catch (IOException e) {

        } catch (ClassNotFoundException h) {

        }
        System.out.println(helper);
        return helper;
    }

    /**
     * Checks all the save files contain a valid object to load
     * @return a boolean stating whether or not the save files can be loaded
     * @param filename the filename of the object to check
     */
    public boolean verifySaves(String filename) {
        File file = new File(filename);
        InputStream inputFile;
        InputStream buffer;
        ObjectInput input;
        Object helper;
        try {
            inputFile = new FileInputStream(file);
            buffer = new BufferedInputStream(inputFile);
            input = new ObjectInputStream(buffer);
            for (int i = 0; i < 5; i++) {
                System.out.println(i);
                switch (i) {
                    case 0:
                        helper = (ChatManager) input.readObject();
                        break;
                    case 1:
                        helper = (EventManager) input.readObject();
                        break;
                    case 2:
                        helper = (RoomManager) input.readObject();
                        break;
                    case 3:
                        helper = (UserManager) input.readObject();
                        break;
                    case 5:
                        helper = (RequestManager) input.readObject();
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (ClassCastException cc) {
            return false;
        } catch (IOException er) {
            return false;
        } catch (ClassNotFoundException cnf) {
            return false;
        }
        return true;
    }

}
