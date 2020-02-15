package frc.robot;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * A class to control the speed of the launcher via shuffleboard for testing purposes
 * 
 * @version 1/25/2020
 * @author Josh Overbeek
 */
public class ShuffleboardShooterControl {

    //declare private variables for creating a tab, instantiating a launcher and retrieving data
    private ShuffleboardTab m_launcherTab;
    private Launcher m_launcher;
    private NetworkTableEntry m_percentTarget;
    private NetworkTableEntry m_angularVelocityTarget;
    
    /**
     * Constructor for ShuffleboardShooterControl objects
     * <p>This retrieves data from the shuffleboard and controls based on that
     * 
     * @param launcher The launcher to be controlled
     */
    public ShuffleboardShooterControl(Launcher launcher) {
        //creates a tab on the shuffleboard for all our launcher needs
        m_launcherTab = Shuffleboard.getTab("Launcher");

        //instantiates our private launcher as our passed in launcher
        m_launcher = launcher;

        //creates a persistent widget as text for controlling speed
        m_percentTarget = m_launcherTab.addPersistent("PercentLaunchSpeed", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
                              .withProperties(Map.of("min", -1.0, "max", 1.0))  //sets min and max values
                              .getEntry();                                      //retrieves the entry to assign our setpoint

        //creates a persistent widget as text for controlling speed
        m_angularVelocityTarget = m_launcherTab.addPersistent("VeloctiyLaunchSpeed (rev/100ms)", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
                              .withProperties(Map.of("min", -10.0, "max", 10.0))  //sets min and max values
                              .getEntry();                                      //retrieves the entry to assign our setpoint
    }

    /**
     * Sets the percent speed based on what is currently assigned on the shuffleboard
     * Should be on while button held and off while button released
     */
    public void setPercentSpeed() {
        //assigns the speed based on the shuffleboard with a default value of zero
        double tempSpeed = m_percentTarget.getDouble(0.0);

        //runs the proportional control system based on the aquired speed
        m_launcher.proportionalSpeedSetter(tempSpeed);
    }

    /**
     * Sets the velocity based on what is currently assigned on the shuffleboard
     * Should be on while button held and off while button released
     */
    public void setVelocity() {
        //assigns the speed based on the shuffleboard with a default value of zero
        double tempVelocity = m_angularVelocityTarget.getDouble(0.0);

        //runs the proportional control system based on the aquired speed
        m_launcher.setVelocity(tempVelocity);
    }

    /**
     * Sets the setpoint to zero, should be used as a default state
     */
    public void zeroSpeed() {
        m_launcher.proportionalSpeedSetter(0.0);
    }

}
