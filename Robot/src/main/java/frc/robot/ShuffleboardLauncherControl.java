package frc.robot;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A class to control the speed of the launcher via shuffleboard for testing purposes
 * 
 * @version 1/25/2020
 * @author Josh Overbeek
 */
public class ShuffleboardLauncherControl {

    //declare private variables for creating a tab, instantiating a launcher and retrieving data
    public ShuffleboardTab testingTab;
    private Launcher m_launcher;
    private NetworkTableEntry m_percentTarget;
    private NetworkTableEntry m_angularVelocityTarget;
    //private NetworkTableEntry m_velocityGraph;
    
    /**
     * Constructor for ShuffleboardShooterControl objects
     * <p>This retrieves data from the shuffleboard and controls based on that
     * 
     * @param launcher The launcher to be controlled
     */
    public ShuffleboardLauncherControl(Launcher launcher) {
        //creates a tab on the shuffleboard for all our launcher needs
        testingTab = Shuffleboard.getTab("Launcher");

        //instantiates our private launcher as our passed in launcher
        m_launcher = launcher;

        //creates a persistent widget as text for controlling speed
        m_percentTarget = testingTab.addPersistent("Percent Launch Speed", 0.0)//creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
                              .withProperties(Map.of("min", -1.0, "max", 1.0))  //sets min and max values
                              .getEntry();                                      //retrieves the entry to assign our setpoint

        //creates a persistent widget as text for controlling speed
        m_angularVelocityTarget = testingTab.addPersistent("Veloctiy Launch Speed (rpm)", 0.0) //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)                             //sets widget to a text view
                              .withProperties(Map.of("min", -10000.0, "max", 10000.0))          //sets min and max values
                              .getEntry();                                                      //retrieves the entry to assign our setpoint

        /*m_velocityGraph = testingTab.addPersistent("Velocity Graph", 0.0)
                            .withWidget(BuiltInWidgets.kGraph)
                            .withProperties(Map.entry(m_launcher.getEncoderVelocity(),   ))
                            .getEntry();
*/
        SmartDashboard.putNumber("Velocity", getVelocity());

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

    public double getVelocity(){
        double velocity = m_launcher.getEncoderVelocity();
        velocity *= 600;
        return velocity;
    }

    /**
     * Sets the setpoint to zero, should be used as a default state
     */
    public void zeroSpeed() {
        m_launcher.proportionalSpeedSetter(0.0);
    }

}
