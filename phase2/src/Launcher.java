
import Controllers.TechConferenceSystem;
import GUI.Dashboard;

/**
 * Main file of the program; run this file to run the entire program
 * @author Joyce Huang and Peter Chen
 */
public class Launcher {

    public static void main(String[] args) {

        final Dashboard dashboard = new Dashboard();
        new TechConferenceSystem(dashboard);
    }

}
