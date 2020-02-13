/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.LimelightReader.Pipeline;
import frc.robot.PilotController.DriveType;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * 
 * @version 1/25/2019
 */
public class Robot extends TimedRobot {
    /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */


    //declares our drivetrain motor controllers and a currently unused shooter motor
    TalonFX m_masterLeftDriveFalcon;
    TalonFX m_masterRightDriveFalcon;

    TalonFX m_slaveLeftDriveFalcon;
    TalonFX m_slaveRightDriveFalcon;

    // VictorSPX m_intakeMotor;

    //declares our launcher system and our controls for that system over the launcher tab
    Launcher m_shooter;
    ShuffleboardShooterControl m_shooterControl;

    //declares an xbox controller used for testing prototype code
    XboxController m_testController;

    //declares controllers and drivetrain for testing drive code
    XboxController m_driveController;
    PilotController m_pilotController;
    ShiftDrive m_drivetrain;

    DoubleSolenoid m_leftPiston;
    DoubleSolenoid m_rightPiston;

    //toggle for the limelight
    boolean m_isDriverCamera;

    //declares the network table for limelight info so that we can access it
    NetworkTable m_limelightTable;

    //declare our limelight reader object
    LimelightReader m_limelightReader;

    //declare our launcher targeting object
    LauncherTargeting m_launcherTargeting;

    //declare private variables for creating a camera tab, and putting up variables to test for angles and distance
    private ShuffleboardTab m_cameraTab;
    private NetworkTableEntry m_cameraHeight;
    private NetworkTableEntry m_cameraAngle;
    private NetworkTableEntry m_targetHeight;
    private NetworkTableEntry m_distance;

    public Robot() {
        //instantiates master motors for drive
        m_masterLeftDriveFalcon = new TalonFX(RobotMap.MASTER_LEFT_FALCON_ID);
        m_masterRightDriveFalcon = new TalonFX(RobotMap.MASTER_RIGHT_FALCON_ID);

        //instantiates slave motors for drive
        m_slaveLeftDriveFalcon = new TalonFX(RobotMap.SLAVE_LEFT_FALCON_ID);
        m_slaveRightDriveFalcon = new TalonFX(RobotMap.SLAVE_RIGHT_FALCON_ID);

        //instantiates our test controller
        m_testController = new XboxController(RobotMap.TEST_CONTROLLER_PORT);

        //instantiate drivetrain for tested
        m_driveController = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

        m_leftPiston = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.LEFT_SOLENOID_FORWARD_PORT, RobotMap.LEFT_SOLENOID_REVERSE_PORT);
        m_rightPiston = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.RIGHT_SOLENOID_FORWARD_PORT, RobotMap.RIGHT_SOLENOID_REVERSE_PORT);
        m_drivetrain = new ShiftDrive(m_masterLeftDriveFalcon, m_masterRightDriveFalcon, m_slaveLeftDriveFalcon, m_slaveRightDriveFalcon, m_leftPiston, m_rightPiston, true);

        //gives us access to the network table for the limelight
        m_limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

        //create an object to read values off of our limelight
        m_limelightReader = new LimelightReader(m_limelightTable);

        //create our targeting object
        //"this" is the current robot, we pass it in so that the targeting can see what periodic function we are in
        m_launcherTargeting = new LauncherTargeting(m_drivetrain, m_limelightReader, this);

        m_pilotController = new PilotController(m_driveController, m_drivetrain, DriveType.kArcade, m_launcherTargeting);


        //sets our default state to the vision pipeline
        m_isDriverCamera = false;

        //creates a tab on the shuffleboard for the camera
        m_cameraTab = Shuffleboard.getTab("Camera");

        //Creates editable text fields to set camera height, fixed angle, and target height
        m_cameraHeight = m_cameraTab.addPersistent("Camera Height (m)", 0.0)                
                                .withWidget(BuiltInWidgets.kTextView)             
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .getEntry();

        m_cameraAngle = m_cameraTab.addPersistent("Camera Angle (deg)", 0.0)                
                               .withWidget(BuiltInWidgets.kTextView)             
                               .withProperties(Map.of("min", 0.0, "max", 90.0)) 
                               .getEntry();
        
        m_targetHeight = m_cameraTab.addPersistent("Target Height (m)", 0.0)                
                                .withWidget(BuiltInWidgets.kTextView)             
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .getEntry();

        //creates a field to display calculated distance
        m_distance = m_cameraTab.addPersistent("Distance", 0.0)
                            .getEntry();

    }

    @Override
    public void robotInit() {
        //zeros used motor contollers
        m_masterLeftDriveFalcon.set(ControlMode.PercentOutput, 0);
        m_masterRightDriveFalcon.set(ControlMode.PercentOutput, 0);
        m_slaveLeftDriveFalcon.set(ControlMode.PercentOutput, 0);
        m_slaveRightDriveFalcon.set(ControlMode.PercentOutput, 0);

        //starts camera in driver mode. This should be our default camera mode
        m_limelightReader.setPipeline(Pipeline.kDriver);
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
        m_pilotController.controlDriveTrain();
    }

    @Override
    public void testInit() {
        //zeros the shooter
        m_shooterControl.zeroSpeed();
    }

    @Override
    public void testPeriodic() {
        //controls for toggling the camera mode between driver mode and vision mode
        if(m_testController.getBButtonReleased()) {
            //if it's in driver mode, set the camera to vision mode
            if(m_isDriverCamera) {
                m_limelightTable.getEntry("pipeline").setNumber(0);
            }
            //if it's in vision mode, set the camera to driver mode
            else {
                m_limelightTable.getEntry("pipeline").setNumber(3);
            }
            //toggle the variable
            m_isDriverCamera = !m_isDriverCamera;
           
        }
        //calls GetModifiedDegrees in order to test and receive the print outs of either left or right
        m_limelightReader.getModifiedDegreesToTarget();
        //calculates and reports the distance from the robot to the base of the target
        double netHeight = (m_targetHeight.getDouble(0) - m_cameraHeight.getDouble(0));
        double lengthToHeightRatio = Math.tan((Math.PI / 180) * (m_cameraAngle.getDouble(0) + m_limelightTable.getEntry("ty").getDouble(0)));
        //reports the distance to the smart dashboard
        m_distance.setDouble(netHeight /  lengthToHeightRatio);
    }

}
