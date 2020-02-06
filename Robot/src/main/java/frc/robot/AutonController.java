package frc.robot;

/**
 * Shell class to run our auton
 * <p> This needs to be filled out
 * 
 * @author Josh Overbeek
 * @version 2/3/2020
 */
public class AutonController {
    /**
     * This enum stores our starting field position for selecting auton
     */
    public enum Position {
        kLeft, kCenter, kRight
    }

    /**
     * This enum holds the various stages of auton
     * <p>These values are currently sample values, these should be replaced with actual steps in auton
     */
    public enum State {
        kFirstDrive, kFirstTurn, kTargeting, kIntake, kLaunching
    }

    /**
     * Constructor for auton object
     */
    public AutonController() {

    }

    //There should be a set of auton movement methods

    /**
     * This method should hold all the auton methods in sequence 
     */
    public void auton(){

    }
}