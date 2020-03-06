/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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
  //declares our launcher system and our controls for that system over the launcher tab
  Launcher m_launcher;
  ShuffleboardLauncherControl m_launcherControl;

  //declare our other copilot systems
  Magazine m_magazine;
  Intake m_intake;

  //declares an xbox controller used for testing prototype code
  XboxController m_testController;

  //declares controller and drivetrain for testing drive code
  /** The class that we wrote to read values from the controller and control the drivetrain */
  PilotController m_pilotController;

  //toggle for the limelight
  //control for toggling the limelight should be moved to either limelight reader or pilot controller
  boolean m_isDriverCamera;

  //declare our limelight reader object
  LimelightReader m_limelightReader;

  //declare private variables for creating a camera tab, and putting up variables to test for angles and distance
  //this tab is exclusivly for testing, but could still be moved into limelight targeting test mode
  private ShuffleboardTab m_cameraTab;
  private NetworkTableEntry m_cameraHeight;
  private NetworkTableEntry m_cameraAngle;
  private NetworkTableEntry m_targetHeight;
  private NetworkTableEntry m_distance;

  public Robot() {
    //instantiates our test controller
    m_testController = new XboxController(RobotMap.TEST_CONTROLLER_PORT);

    //instantiate launcher motor controllers and shuffleboard control for those motors
    m_launcher = new Launcher();
    m_launcherControl = new ShuffleboardLauncherControl(m_launcher);

    m_limelightReader = new LimelightReader();

    //intantiates our PilotController, which controls all systems on the drivetrain
    m_pilotController = new PilotController(DriveType.kArcade, m_limelightReader);
    
    //sets our default state to the vision pipeline
    m_isDriverCamera = false;

    //instantiate magazine, this needs to be moved to copilot controller post testing
    m_magazine = new Magazine();

    //instantiate intake, this needs to be moved to copilot controller post testing
    m_intake = new Intake();

    //sets up our camera testing tab
    shuffleboardConfig();
    m_limelightReader.disableLEDS();
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    //zeros used motor controllers
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
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
      m_pilotController.controlDriveTrainPeriodic();
  }

  @Override
  public void testInit() {
    //zeros the shooter
    m_launcherControl.zeroSpeed();
  }

  @Override
  public void testPeriodic() {
    //this test periodic is designed for launcher velocity testing
    //sets the velocity of the launcher while holding the b button
    //Both intake motors must be inverted

    //runs velocity control while b button is pressed
    if(m_testController.getBButton()) {
      m_launcherControl.setVelocity();
    }
    //runs percent control while a button is pressed
    else if (m_testController.getAButton()) {
        m_launcherControl.setPercentSpeed();
    }
    //zeros speed while not actively controlled
    else {
        m_launcherControl.zeroSpeed();
    }

    //note that the magazine cannot run over 0.4 without load, or else the polycore will fly off
    //runs the magazine forward while the right bumper is held, and backward while the left one is
    if (m_testController.getBumper(Hand.kRight)) {
        m_magazine.runBelt(0.37);
    }
    else if (m_testController.getBumper(Hand.kLeft)) {
        m_magazine.runBelt(-0.37);
    }
    //kills the velocity while not holding
    else {
      m_magazine.runBelt(0);
    }

    //resets the encoder when the start button is pressed
    if (m_testController.getStartButton()) {
        m_launcher.getMasterMotor().setSelectedSensorPosition(0);
    }

    m_launcherControl.setPIDF();
    m_launcherControl.publishData();

    //disable the intake motors while its
    m_intake.setInnerIntakeMotor(0);
    m_intake.setOuterIntakeMotor(0);
  }

  @Override
  public void disabledInit() {
    m_pilotController.getTargeting().getPID_Values();
  }

  @Override
  public void disabledPeriodic() {
      //this method pulls our input scalars off of the driver tab on shuffleboard
      //and sets them on our drivetrain class. Our driver input is multiplied by our scalar values
      //in order to scale back drivetrain speed.
      m_pilotController.setInputScalar();
      m_pilotController.getTargeting().setPID();
      
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
