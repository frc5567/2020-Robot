  
package frc.robot;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShuffleboardRotatorControl{

    //declare variables
    private ShuffleboardTab controlRotatorTab;
    private ControlRotator controlRotator;
    private NetworkTableEntry setpoint;

    //constructor
    public ShuffleboardRotatorControl(ControlRotator controlRotator) {
        //creates a tab on the shuffleboard for all our rotator needs
        controlRotatorTab = Shuffleboard.getTab("controlRotator");

        //instantiates our private rotator as our passed in controlRotator
        this.controlRotator = controlRotator;

        //creates a persistent widget as text for controlling speed
        setpoint = launcherTab.addPersistent("LaunchSpeed", 0.0)                //creates widget with 0.0 as a default
                              .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
                              .withProperties(Map.of("min", -1.0, "max", 1.0))  //sets min and max values
                              .getEntry();                                      //retrieves the entry to assign our setpoint
    }

    /**
     * Sets the setpoint based on what is currently assigned on the shuffleboard
     * Should be toggled on and off
     */
    public void setSpeed() {
        //assigns the speed based on the shuffleboard with a default value of zero
        double tempSpeed = setpoint.getDouble(0.0);

        //runs the proportional control system based on the aquired speed
        launcher.proportionalSpeedSetter(tempSpeed);
    }

    /**
     * Sets the setpoint to zero, should be used as a default state
     */
    public void zeroSpeed() {
        launcher.proportionalSpeedSetter(0.0);
    }

}