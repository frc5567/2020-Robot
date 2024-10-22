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
public class ShuffleboardLauncherControl {

    //declare private variables for creating a tab, instantiating a launcher and retrieving data
    private ShuffleboardTab m_launcherTab;
    private Launcher m_launcher;
    private NetworkTableEntry m_percentTarget;
    private NetworkTableEntry m_angularVelocityTarget;

    private NetworkTableEntry m_pLaunch;
    private NetworkTableEntry m_iLaunch;
    private NetworkTableEntry m_dLaunch;
    private NetworkTableEntry m_fLaunch;
    NetworkTableEntry m_currentVel;
    
    /**
     * Constructor for ShuffleboardShooterControl objects
     * <p>This retrieves data from the shuffleboard and controls based on that
     * 
     * @param launcher The launcher to be controlled
     */
    public ShuffleboardLauncherControl(Launcher launcher) {
        //creates a tab on the shuffleboard for all our launcher needs
        m_launcherTab = Shuffleboard.getTab("Launcher");

        //instantiates our private launcher as our passed in launcher
        m_launcher = launcher;

        //creates a persistent widget as text for controlling speed
        m_percentTarget = m_launcherTab.addPersistent("Percent Launch Speed", 0.0)//creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
                              .withProperties(Map.of("min", -1.0, "max", 1.0))  //sets min and max values
                              .getEntry();                                      //retrieves the entry to assign our setpoint

        //creates a persistent widget as text for controlling speed
        m_angularVelocityTarget = m_launcherTab.addPersistent("Velocity Launch Speed (rpm)", 0.0) //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)                             //sets widget to a text view
                              .withProperties(Map.of("min", -10000.0, "max", 10000.0))          //sets min and max values
                              .getEntry();                                                      //retrieves the entry to assign our setpoint

        m_pLaunch = m_launcherTab.addPersistent("P", 0.0)
                                 .withWidget(BuiltInWidgets.kTextView)
                                 .getEntry();

        m_iLaunch = m_launcherTab.addPersistent("I", 0.0)
                                 .withWidget(BuiltInWidgets.kTextView)
                                 .getEntry();

        m_dLaunch = m_launcherTab.addPersistent("D", 0.0)
                                 .withWidget(BuiltInWidgets.kTextView)
                                 .getEntry();

        m_fLaunch = m_launcherTab.addPersistent("F", 0.0)
                                 .withWidget(BuiltInWidgets.kTextView)
                                 .getEntry();

        m_currentVel = m_launcherTab.addPersistent("Current Velocity", 0.0)
                                    .withWidget(BuiltInWidgets.kTextView)
                                    .getEntry();

    }

    /**
     * Sets the percent speed based on what is currently assigned on the shuffleboard
     * Should be on while button held and off while button released
     */
    public void setPercentSpeed() {
        //assigns the speed based on the shuffleboard with a default value of zero
        double tempSpeed = m_percentTarget.getDouble(0.0);

        //runs the proportional control system based on the aquired speed
        m_launcher.setMotor(tempSpeed);
    }

    /**
     * Sets the velocity based on what is currently assigned on the shuffleboard
     * Should be on while button held and off while button released
     * <p>The value on the shuffleboard is in rpm, we convert it to rev per 100ms
     */
    public void setVelocity() {
        //assigns the speed based on the shuffleboard with a default value of zero
        double tempVelocity = m_angularVelocityTarget.getDouble(0.0);

        //Convert inputted velocity in rpm to raw units per 100 ms
        tempVelocity *= RobotMap.RPM_TO_UNITS_PER_100MS;

        //runs the proportional control system based on the aquired speed
        m_launcher.setVelocity(tempVelocity);
    }

    /**
     * Sets PIDF values based on shuffleboard values
     */
    public void setPIDF() {
        m_launcher.configPIDF(m_pLaunch.getDouble(0), m_iLaunch.getDouble(0), m_dLaunch.getDouble(0), m_fLaunch.getDouble(0));
    }

    /**
     * Resets PIDF values to RobotMap constants
     */
    public void resetPIDF() {
        m_launcher.resetPIDF();
    }

    /**
     * This method publishes all requested data to shuffleboard widgets
     * <p> This currently holds velocity in RPM, but we can put anything we want here
     */
    public void publishData() {
        m_currentVel.setDouble(m_launcher.getMasterMotor().getSelectedSensorVelocity(0) / RobotMap.RPM_TO_UNITS_PER_100MS);
    }

    /**
     * Sets the setpoint to zero, should be used as a default state
     */
    public void zeroSpeed() {
        m_launcher.setMotor(0.0);
    }
}
