package GUI;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ScrollPaneConstants;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Creates all GUI elements and interactions for the menu that allows users to sign up for events
 * @author Joyce Huang
 */
public class SignUpDashboard extends JPanel {
    private String currentMenu, previousMenu, currentUsername;
    private JButton seeAllEvent, seeSignedEvent, back, nextPanel;
    private JButton browseEvent, signUpEvent, cancelAttendEvent;
    private JButton confirmEventSignup, confirmEventRemoval;
    private final Viewable sendsInfo;
    private JTextField textInput;
    private JLabel errorText, eventName;
    private final Dashboard dashboard;

    /**
     * Constructor that creates the SignUpDashboard based off inputs from the Dashboard class
     * @param sendsInfo the instance of Viewable from the Dashboard class
     * @param currentUsername the username of the user who is attempting to use options in the sign up menu
     * @param dashboard the original GUI instance
     */
    SignUpDashboard(Viewable sendsInfo, String currentUsername, Dashboard dashboard) {
        this.sendsInfo = sendsInfo;
        this.currentUsername = currentUsername;
        this.dashboard = dashboard;
        this.setOpaque(true);
        currentMenu = "SignUpMenu";
        previousMenu = "LoggedIn";
        createButtons();
        run();
    }

    private void run() {
        currentMenu = "SignUpEvent";
        this.removeAll();
        this.add(browseEvent);
        this.add(signUpEvent);
        this.add(cancelAttendEvent);
        this.add(back);
        dashboard.refresh();
    }

    private void browseEventMenu() {
        currentMenu = "BrowseEvent";
        this.removeAll();
        this.add(seeAllEvent);
        this.add(seeSignedEvent);
        this.add(back);
        dashboard.refresh();
        
    }

    private void displayEvents(boolean allOrNot) {
        currentMenu = "DisplayEvents";
        this.removeAll();
        String[] info;
        if (allOrNot) {
            info = sendsInfo.displayAllEvents();
        } else {
            info = sendsInfo.displaySignedUpEvents(currentUsername);
        }
        if (info.length == 0) {
            errorText.setText("no events :(");
            this.add(errorText);
        } else {
            JScrollPane events =new JScrollPane(new JList(info), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            events.setPreferredSize(new Dimension(1100, 720));
            this.add(events);
        }
        this.add(back);
        dashboard.refresh();
        
    }

    private void signUpForEvent() {
        currentMenu = "SignUpForEvent";
        this.removeAll();
        this.add(eventName);
        this.add(textInput);
        this.add(confirmEventSignup);
        this.add(back);
        dashboard.refresh();
        
    }

    private void cancelAttendEvent() {
        currentMenu = "CancelAttendEvent";
        this.removeAll();
        this.add(eventName);
        this.add(textInput);
        this.add(confirmEventRemoval);
        this.add(back);
        dashboard.refresh();
        
    }

    private void createButtons() {
        browseEvent = new JButton("Browse Events");
        browseEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "SignUpMenu";
                browseEventMenu();
            }
        });
        signUpEvent = new JButton("Sign Up for Event");
        signUpEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "SignUpMenu";
                signUpForEvent();
            }
        });
        seeAllEvent = new JButton("See All Events");
        seeAllEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "BrowseMenu";
                displayEvents(true);
            }
        });
        seeSignedEvent = new JButton("See Signed Up Events");
        seeSignedEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "BrowseMenu";
                displayEvents(false);
            }
        });
        cancelAttendEvent = new JButton("Cancel Attendance to Event");
        cancelAttendEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "SignUpMenu";
                cancelAttendEvent();
            }
        });
        back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu();
            }
        });
        confirmEventSignup = new JButton("Confirm");
        confirmEventSignup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "SignUpMenu";
                int result = sendsInfo.signUpForEvent(currentUsername, textInput.getText());
                switch (result) {
                    case 0:
                        previousMenu();
                        if (sendsInfo.userIsVIP(currentUsername)) {
                            dashboard.changeTheme("vip");
                        }
                        break;
                    case 1:
                        failedMenu("You have signed up for this event before.");
                        break;
                    case 2:
                        failedMenu("The event you have entered is already full.");
                        break;
                    case 3:
                        failedMenu("The event title you have entered is invalid.");
                        break;
                    case 4:
                        failedMenu("This is a VIP event, but you are not a VIP.");
                        break;
                }
                if (sendsInfo.userIsVIP(currentUsername)){
                    dashboard.loginType();
                }
                clearTextField();
            }
        });
        confirmEventRemoval = new JButton("Confirm");
        confirmEventRemoval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previousMenu = "SignUpMenu";
                int result = sendsInfo.cancelAttendEvent(currentUsername, textInput.getText());
                switch (result) {
                    case 0:
                        if (!sendsInfo.userIsVIP(currentUsername)) {
                            dashboard.changeTheme("regular");
                        }
                        previousMenu();
                        break;
                    case 1:
                        failedMenu("You haven't signed up for this event yet.");
                        break;
                    case 2:
                        failedMenu("The event title you have entered is invalid.");
                        break;
                }
                clearTextField();
            }
        });
        nextPanel = new JButton("Next");
        nextPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToSameMenu();
            }
        });
        textInput = new JTextField(12);
        errorText = new JLabel();
        eventName = new JLabel("Event Name");
    }

    private void previousMenu() {
        switch (previousMenu) {
            case "LoggedIn":
                //return to dashboard
                dashboard.backToMain("SignUp");
                break;
            case "BrowseMenu":
                browseEventMenu();
                previousMenu = "SignUpMenu";
                break;
            case "SignUpMenu":
                previousMenu = "LoggedIn";
                run();
                break;
        }
    }

    private void returnToSameMenu() {
        switch (currentMenu) {
            case "SignUpForEvent":
                signUpForEvent();
                break;
            case "CancelAttendEvent":
                cancelAttendEvent();
                break;
        }
    }

    private void failedMenu(String failedMessage) {
        this.removeAll();
        errorText.setText(failedMessage);
        this.add(errorText);
        this.add(nextPanel);
        dashboard.refresh();
    }

    private void clearTextField() {
        textInput.setText("");
    }

}
