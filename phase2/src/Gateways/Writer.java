package Gateways;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * A class that deals with the output of data into one file
 * @author Joyce Huang
 */
public class Writer {

    /**
     * A method that saves an object to a .txt file
     * @return whether or not the save was successful
     * @param filename name of the file to save to
     * @param thing the object being saved
     */
    public boolean writeToFile(String filename, Object thing[]) {
        try {
            OutputStream file = new FileOutputStream(filename);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);

            for (int i = 0; i < thing.length; i++) {
                output.writeObject(thing[i]);
            }

            output.flush();
            output.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
