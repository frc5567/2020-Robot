/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.PilotController.DriveType;


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

  //declares our drivetrain motor controllers
  TalonFX m_masterLeftDriveFalcon;
  TalonFX m_masterRightDriveFalcon;
  TalonFX m_slaveLeftDriveFalcon;
  TalonFX m_slaveRightDriveFalcon;

  //declare the gyroscope used to rotate our drivetrain and monitor heading
  NavX m_gyro;

  //declare the launcher motor controllers
  TalonSRX m_masterLauncher;

  /**the launcher slave motor on the same side of the launcher as the master */
  VictorSPX m_closeLauncherSlave;

  /**the launcher slave motors on the opposite side of the launcher from the master */
  VictorSPX m_farLauncherSlave1, m_farLauncherSlave2;

  //declares our launcher system and our controls for that system over the launcher tab
  Launcher m_launcher;
  ShuffleboardShooterControl m_shooterControl;

  //declares an xbox controller used for testing prototype code
  XboxController m_testController;

  //declares controllers and drivetrain for testing drive code
  /** The physical Xbox controller that reads inputs */
  XboxController m_driveController;
  
  /** The class that we wrote to read values from the controller and control the drivetrain */
  PilotController m_pilotController;

  //our two speed drivetrain
  Drivetrain m_drivetrain;

  //declare the solenoids for controlling the drive gear
  DoubleSolenoid m_leftPiston;
  DoubleSolenoid m_rightPiston;

  //toggle for the limelight
  //control for toggling the limelight should be moved to either limelight reader or pilot controller
  boolean m_isDriverCamera;

  //declare our limelight reader object
  LimelightReader m_limelightReader;

  //declare our launcher targeting object
  LauncherTargeting m_launcherTargeting;

  //declare private variables for creating a camera tab, and putting up variables to test for angles and distance
  //this tab is exclusivly for testing, but could still be moved into limelight targeting test mode
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

    //instantiate double solenoids for gear shifting
    m_leftPiston = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.LEFT_SOLENOID_FORWARD_PORT, RobotMap.LEFT_SOLENOID_REVERSE_PORT);
    m_rightPiston = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.RIGHT_SOLENOID_FORWARD_PORT, RobotMap.RIGHT_SOLENOID_REVERSE_PORT);

    //instantiate drivetrain object
    m_drivetrain = new Drivetrain(m_masterLeftDriveFalcon, m_masterRightDriveFalcon, m_slaveLeftDriveFalcon, m_slaveRightDriveFalcon, m_leftPiston, m_rightPiston, true);

    //runs the configure method on the drivetrain. 
    //This method should be moved into the drivetrain contstructor, when it is this should be removed
    m_drivetrain.configDriveTrain();

    //instantiates our test controller
    m_testController = new XboxController(RobotMap.TEST_CONTROLLER_PORT);

    //instantiate xbox controller for controlling the drivetrain
    m_driveController = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

    //instantiate launcher motor controllers and shuffleboard control for those motors
    m_masterLauncher = new TalonSRX(RobotMap.MASTER_LAUNCHER_ID);
    m_closeLauncherSlave = new VictorSPX(RobotMap.CLOSE_LAUNCHER_SLAVE_ID);
    m_farLauncherSlave1 = new VictorSPX(RobotMap.FAR_LAUNCHER_SLAVE1_ID);
    m_farLauncherSlave2 = new VictorSPX(RobotMap.FAR_LAUNCHER_SLAVE2_ID);

    m_launcher = new Launcher(RobotMap.LAUNCHER_ADJUSTMENT_VALUE, m_masterLauncher, m_closeLauncherSlave, m_farLauncherSlave1, m_farLauncherSlave2);
    m_shooterControl = new ShuffleboardShooterControl(m_launcher);

    //catch an error on instantiating the navX if it is not plugged in
    //Note that we do not actually handle this error, we just prevent the robot from crashing here
    try {
        m_gyro = new NavX(SPI.Port.kMXP);
    } catch (RuntimeException ex) {
        System.out.println("Error instantiating navX MXP");
    }

    //create an object to read values off of our limelight
    m_limelightReader = new LimelightReader();

    //create our targeting object
    //"this" is the current robot, we pass it in so that the targeting can see what periodic function we are in
    m_launcherTargeting = new LauncherTargeting(m_drivetrain, m_limelightReader, this);

    //intantiates our PilotController, which controls all systems on the drivetrain
    m_pilotController = new PilotController(m_driveController, m_drivetrain, DriveType.kArcade, m_launcherTargeting);
    
    //sets our default state to the vision pipeline
    m_isDriverCamera = false;

    //sets up our camera testing tab
    shuffleboardConfig();
    
  }

  @Override
  public void robotInit() {
    //zeros used motor controllers
    m_masterLeftDriveFalcon.set(ControlMode.PercentOutput, 0);
    m_masterRightDriveFalcon.set(ControlMode.PercentOutput, 0);
    m_masterLeftDriveFalcon.set(ControlMode.PercentOutput, 0);
    m_masterRightDriveFalcon.set(ControlMode.PercentOutput, 0);
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
    //this test periodic is designed for launcher velocity testing
    //sets the velocity of the launcher while holding the b button
    if(m_testController.getBButton()) {
      m_shooterControl.setVelocity();
    }
    //kills the velocity while not holding
    else {
      m_shooterControl.zeroSpeed();
    }
  }

  @Override
  public void disabledInit() {

  }

  @Override
  public void disabledPeriodic() {
      //this method pulls our input scalars off of the driver tab on shuffleboard
      //and sets them on our drivetrain class. Our driver input is multiplied by our scalar values
      //in order to scale back drivetrain speed.
      m_pilotController.setInputScalar();
  }

    /**
     * instantiates all of our network table entries and displays them under the camera tab
     * <p>the point of this method is to move the shuffleboard code out of init/constructor
     */
    public void shuffleboardConfig() {
        //creates a tab on the shuffleboard for the camera
        m_cameraTab = Shuffleboard.getTab("Camera");

        //Creates editable text fields to set camera height, fixed angle, and target height
        m_cameraHeight = m_cameraTab.addPersistent("Camera Height (m)", 0.0)                
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .withWidget(BuiltInWidgets.kTextView)             
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

}
