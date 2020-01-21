/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */

    //declares our drivetrain motor controllers and a currently unused shooter motor
    TalonSRX leftTalon;
    TalonSRX rightTalon;

    VictorSPX leftVictor;
    VictorSPX rightVictor;

    VictorSPX shooterMotor;

    //declares our launcher system and our controls for that system over the launcher tab
    Launcher shooter;
    ShuffleboardShooterControl shooterControl;

    //declares an xbox controller used for testing prototype code
    XboxController testController;

    //toggle for the limelight
    boolean isDriverCamera;

    //declares the network table for limelight info so that we can access it
    NetworkTable limelightTable;

    //declare private variables for creating a camera tab, and putting up variables to test for angles and distance
    private ShuffleboardTab cameraTab;
    private NetworkTableEntry cameraHeight;
    private NetworkTableEntry cameraAngle;
    private NetworkTableEntry targetHeight;
    private NetworkTableEntry distance;

    public Robot() {
        //instantiates master motors for drive
        leftTalon = new TalonSRX(1);
        rightTalon = new TalonSRX(2);

        //instantiates slave motors for drive
        leftVictor = new VictorSPX(11);
        rightVictor = new VictorSPX(12);

        //instantiates the shooter using our drive motors
        //Note that this will need to be fixed when we have all systems on the robot
        shooter = new Launcher(0.5, leftTalon, rightTalon);
        shooterControl = new ShuffleboardShooterControl(shooter);

        //instantiates currently unused shooter motor
        shooterMotor = new VictorSPX(15);

        //instantiates our test controller
        testController = new XboxController(0);

        //gives us access to the network table for the limelight
        limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

        //sets our default state to the vision pipeline
        isDriverCamera = false;

        //creates a tab on the shuffleboard for the camera
        cameraTab = Shuffleboard.getTab("Camera");

        //Creates editable text fields to set camera height, fixed angle, and target height
        cameraHeight = cameraTab.addPersistent("Camera Height (m)", 0.0)                
                                .withWidget(BuiltInWidgets.kTextView)             
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .getEntry();

        cameraAngle = cameraTab.addPersistent("Camera Angle (deg)", 0.0)                
                               .withWidget(BuiltInWidgets.kTextView)             
                               .withProperties(Map.of("min", 0.0, "max", 90.0)) 
                               .getEntry();
        
        targetHeight = cameraTab.addPersistent("Target Height (m)", 0.0)                
                                .withWidget(BuiltInWidgets.kTextView)             
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .getEntry();

        //creates a field to display calculated distance
        distance = cameraTab.addPersistent("Distance", 0.0)
                            .getEntry();

    }

    @Override
    public void robotInit() {
        //zeros used motor contollers
        leftTalon.set(ControlMode.PercentOutput, 0);
        rightTalon.set(ControlMode.PercentOutput, 0);
        leftVictor.set(ControlMode.PercentOutput, 0);
        rightVictor.set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testInit() {
        //zeros the shooter
        shooterControl.zeroSpeed();
    }

    @Override
    public void testPeriodic() {
        //commented out for limelight testing, uncomment for shooter testing
        // if(testController.getAButton()) {
        //     shooterControl.setSpeed();
        // }
        // else {
        //     shooterControl.zeroSpeed();
        // }

        //controls for toggling the camera mode between driver mode and vision mode
        if(testController.getBButtonReleased()) {
            //if it's in driver mode, set the camera to vision mode
            if(isDriverCamera) {
                limelightTable.getEntry("camMode").setNumber(0);
                limelightTable.getEntry("ledMode").setNumber(0);
            }
            //if it's in vision mode, set the camera to driver mode
            else {
                limelightTable.getEntry("camMode").setNumber(1);
                limelightTable.getEntry("ledMode").setNumber(1);
            }
            //toggle the variable
            isDriverCamera = !isDriverCamera;
        }

        //calculates and reports the distance from the robot to the base of the target
        double netHeight = (targetHeight.getDouble(0) - cameraHeight.getDouble(0));
        double lengthToHeightRatio = Math.tan((Math.PI / 180) * (cameraAngle.getDouble(0) + limelightTable.getEntry("ty").getDouble(0)));
        //reports the distance to the smart dashboard
        distance.setDouble(  netHeight /  lengthToHeightRatio);
    }

}
