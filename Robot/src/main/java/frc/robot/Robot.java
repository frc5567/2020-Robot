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
    TalonSRX leftTalon;
    TalonSRX rightTalon;

    VictorSPX leftVictor;
    VictorSPX rightVictor;

    VictorSPX shooterMotor;

    Launcher shooter;
    ShuffleboardShooterControl shooterControl;

    XboxController testController;

    //toggle for the limelight
    boolean isDriverCamera;

    NetworkTable limelightTable;

    //declare private variables for creating a tab, instantiating a launcher and retrieving data
    private ShuffleboardTab cameraTab;
    private NetworkTableEntry cameraHeight;
    private NetworkTableEntry cameraAngle;
    private NetworkTableEntry targetHeight;
    private NetworkTableEntry distance;

    public Robot() {
        leftTalon = new TalonSRX(1);
        rightTalon = new TalonSRX(2);

        leftVictor = new VictorSPX(11);
        rightVictor = new VictorSPX(12);

        shooter = new Launcher(0.5, leftTalon, rightTalon);
        shooterControl = new ShuffleboardShooterControl(shooter);

        shooterMotor = new VictorSPX(15);

        testController = new XboxController(0);

        limelightTable = NetworkTableInstance.getDefault().getTable("limelight");
        isDriverCamera = false;

        cameraTab = Shuffleboard.getTab("Camera");

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

        distance = cameraTab.addPersistent("Distance", 0.0)
                              .getEntry();

    }

    @Override
    public void robotInit() {
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
        shooterControl.zeroSpeed();
    }

    @Override
    public void testPeriodic() {
        // if(testController.getAButton()) {
        //     shooterControl.setSpeed();
        // }
        // else {
        //     shooterControl.zeroSpeed();
        // }

        if(testController.getBButtonReleased()) {
            if(isDriverCamera) {
                limelightTable.getEntry("camMode").setNumber(0);
                limelightTable.getEntry("ledMode").setNumber(0);
            }
            else {
                limelightTable.getEntry("camMode").setNumber(1);
                limelightTable.getEntry("ledMode").setNumber(1);
            }
            isDriverCamera = !isDriverCamera;
        }
        double netHeight = (targetHeight.getDouble(0) - cameraHeight.getDouble(0));
        double netAngle = Math.tan((Math.PI / 180) * (cameraAngle.getDouble(0) + limelightTable.getEntry("ty").getDouble(0)));

        distance.setDouble(  netHeight / netAngle );
    }

}
