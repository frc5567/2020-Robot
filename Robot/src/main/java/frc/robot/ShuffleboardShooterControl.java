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
    private NetworkTableEntry m_PEntry;
    private NetworkTableEntry m_IEntry;
    private NetworkTableEntry m_DEntry;
    private NetworkTableEntry m_FEntry;
    private NetworkTableEntry m_currentVelocity;
    private NetworkTableEntry m_currentVelocityText;
    
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
        m_angularVelocityTarget = m_launcherTab.addPersistent("VeloctiyLaunchSpeed (rpm)", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
                              .withProperties(Map.of("min", -10000.0, "max", 10000.0))  //sets min and max values
                              .getEntry();                                      //retrieves the entry to assign our setpoint
        
        m_PEntry = m_launcherTab.addPersistent("P", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView) 
                              .getEntry();

        m_IEntry = m_launcherTab.addPersistent("I", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView) 
                              .getEntry();

        m_DEntry = m_launcherTab.addPersistent("D", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView) 
                              .getEntry();

        m_FEntry = m_launcherTab.addPersistent("F", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView) 
                              .getEntry();

        m_currentVelocity = m_launcherTab.add("Current Velocity Graph", 0.0)
                                        .withWidget(BuiltInWidgets.kGraph)
                                        .getEntry();

        m_currentVelocityText = m_launcherTab.add("Current Velocity", 0.0)
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
        m_launcher.proportionalSpeedSetter(tempSpeed);
    }

    /**
     * Sets the velocity based on what is currently assigned on the shuffleboard
     * Should be on while button held and off while button released
     * <p>The value on the shuffleboard is in rpm, we convert it to rev per 100ms
     */
    public void setVelocity() {
        //assigns the speed based on the shuffleboard with a default value of zero
        double tempVelocity = m_angularVelocityTarget.getDouble(0.0);

        //divide imput by 600 to convert from rpm to rev per 100ms
        tempVelocity /= 600;

        //runs the proportional control system based on the aquired speed
        m_launcher.setVelocity(tempVelocity);
    }

    public void setPIDF() {
        m_launcher.setPIDF(m_PEntry.getDouble(0), m_IEntry.getDouble(0), m_DEntry.getDouble(0), m_FEntry.getDouble(0));
        System.out.print("P: " + m_PEntry.getDouble(0) + " | ");
        System.out.print("I: " + m_IEntry.getDouble(0) + " | ");
        System.out.print("D: " + m_DEntry.getDouble(0) + " | ");
        System.out.print("F: " + m_FEntry.getDouble(0) + " |\n");
    }

    public void updateVelocity() {
        double tempVelocity = (m_launcher.getEncoderVelocity() / 600 );
        m_currentVelocity.setDouble(tempVelocity);
        m_currentVelocityText.setDouble(tempVelocity);
    }

    /**
     * Sets the setpoint to zero, should be used as a default state
     */
    public void zeroSpeed() {
        m_launcher.proportionalSpeedSetter(0.0);
    }

}
