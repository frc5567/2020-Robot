/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Intake.Position;
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
  Climber m_climber;

  //declares an xbox controller used for testing prototype code
  XboxController m_testController;
  XboxController m_testController2;
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

  private Auton m_auton;

  public Robot() {
    //instantiates our test controller
    m_testController = new XboxController(RobotMap.TEST_CONTROLLER_PORT);
    m_testController2 = new XboxController(4);

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

    //instantiate climber, this needs to be moved to copilot controller post testing
    m_climber = new Climber();

    m_auton = new Auton(m_pilotController.getTargeting(), m_magazine, m_launcher, m_pilotController.getDrivetrain());

    //sets up our camera testing tab
    shuffleboardConfig();
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  @Override
  public void autonomousInit() {
    m_auton.init();
  }
 
  @Override
  public void autonomousPeriodic() {
    m_auton.periodic();
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
    // periodicLauncherTest();
    m_pilotController.controlDriveTrainPeriodic();
    periodicClimberTest(); 
    // m_intake.setOuterIntakeMotor(0);
    // m_intake.setInnerIntakeMotor(0);
    // System.out.print("pe1:\t"+pe1.get());
    // System.out.print("pe2:\t"+pe2.get()+"\n");
  }

  /**
   * This method will print out values of all sensors on the robot for prematch checks
   */
  public void preMatchSensorTesting() {

  }
  
  /**
   * Prints out the y degrees to target and 
   * calculates distance based on shuffleboard and limelight
   */
  public void periodicDistanceTuning() {
    System.out.println("Y Degrees to target : "+  m_limelightReader.getYDegreesToTarget());
    
    double lengthToHeightRatio = Math.tan(RobotMap.DEG_TO_RAD_CONVERSION * (m_cameraAngle.getDouble(0) + m_limelightReader.getYDegreesToTarget()));
    m_distance.setDouble((m_targetHeight.getDouble(0) - m_cameraHeight.getDouble(0)) / lengthToHeightRatio);
  }

  /**
   * Call this during test periodic for climber testing
   * <p> Controls climber manually (for now)
   */
  public void periodicClimberTest() {
    int extensionCurrent = m_climber.getExtensionMotor().getSelectedSensorPosition();
    //TODO: RobotMap 29700 (Hard limit)
    if((extensionCurrent < 29700) && m_testController.getAButton()) {
      m_climber.setExtensionSpeed(0.4);
    }
    else if ((extensionCurrent > 0) && m_testController.getBButton()) {
      m_climber.setExtensionSpeed(-0.4);
    }
    else if ((extensionCurrent < 29700) && m_testController.getBumper(Hand.kRight)) {
      m_climber.extendClimber();
    }
    else if ((extensionCurrent > 0) && m_testController.getBumper(Hand.kLeft)) {
      m_climber.retractClimber();
    } 
    else {
      m_climber.zeroExtensionMotor();
    }

    if(m_testController.getXButton()) {
      m_climber.setLiftSpeed(0.5);
    }
    else {
      m_climber.zeroLiftMotor();
    }

    if (m_testController.getStartButton()) {
      m_climber.encoderReset();
    }

    m_launcherControl.zeroSpeed();
    m_magazine.runBelt(0);
    m_intake.setInnerIntakeMotor(0);
    m_intake.setOuterIntakeMotor(0);
   // m_climber.zeroLiftMotor();
    System.out.println("Current Encoder Value: \t" + m_climber.getExtensionMotor().getSelectedSensorPosition());
  }

  /**
   * Call this during test periodic for launcher testing
   * <p> Controls launcher, magazine, and zeros intake
   */
  public void periodicLauncherTest() {
    // runs velocity control while b button is pressed
    if(m_testController.getYButtonPressed()) {
      m_launcherControl.setVelocity();
    }
    //zeros speed while not actively controlled
    else if (m_testController.getBButtonPressed()) {
      m_launcherControl.setPercentSpeed();
      m_launcherControl.m_currentVel.setDouble(m_launcher.getMasterMotor().getSelectedSensorVelocity() / RobotMap.RPM_TO_UNITS_PER_100MS);
    }
    else if (m_testController.getXButtonPressed()) {
      m_launcherControl.zeroSpeed();
    }
    
    //runs the magazine forward while the right bumper is held, and backward while the left one is
    if (m_testController2.getXButton()) {
      m_magazine.sensorBeltControl();
    }
    else if (m_testController2.getBumper(Hand.kRight)) {
        m_magazine.runBelt(0.7);
    }
    else if (m_testController2.getBumper(Hand.kLeft)) {
        m_magazine.runBelt(-0.7);
    }
    //kills the velocity while not holding
    else {
      m_magazine.runBelt(0);
    }

    if(m_testController2.getStartButtonPressed()) {
      m_intake.setPosition(Position.kLowered);
    }
    else if(m_testController2.getBackButtonPressed()) {
      m_intake.setPosition(Position.kRaised);
    }

    //resets the encoder when the start button is pressed
    if (m_testController.getStartButton()) {
        m_launcher.getMasterMotor().setSelectedSensorPosition(0);
    }

    m_launcherControl.setPIDF();
    m_launcherControl.publishData();

    //disable the intake motors while its unused
    if (m_testController2.getYButton()) {
      m_intake.setInnerIntakeMotor(.3);
      m_intake.setOuterIntakeMotor(0.8);
    } 
    else if (m_testController2.getAButton()) {
      m_intake.setInnerIntakeMotor(-.3);
      m_intake.setOuterIntakeMotor(-0.6);
    }
    else {
      m_intake.setInnerIntakeMotor(0);
      m_intake.setOuterIntakeMotor(0);
    }
  }

  @Override
  public void disabledInit() {
    m_pilotController.getTargeting().getPID_Values();
    m_pilotController.getDrivetrain().setNeutralMode(NeutralMode.Coast);
  }

  @Override
  public void disabledPeriodic() {
      //this method pulls our input scalars off of the driver tab on shuffleboard
      //and sets them on our drivetrain class. Our driver input is multiplied by our scalar values
      //in order to scale back drivetrain speed.
      m_pilotController.setInputScalar();
      m_pilotController.getTargeting().setPID();
      m_pilotController.getDrivetrain().setNeutralMode(NeutralMode.Coast);
      
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
