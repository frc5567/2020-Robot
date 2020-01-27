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
<<<<<<< HEAD
import edu.wpi.first.wpilibj.GenericHID.Hand;
=======
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.PilotController.DriveType;
>>>>>>> 138e5b91832f43218931a19c9ff02299c6e73ac7

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.Drivetrain;

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
<<<<<<< HEAD
  Drivetrain m_drivetrain;
  Pathing m_pather;

  TalonSRX leftTalon;
  TalonSRX rightTalon;
=======
>>>>>>> 138e5b91832f43218931a19c9ff02299c6e73ac7

    //declares our drivetrain motor controllers and a currently unused shooter motor
    TalonSRX m_leftTalon;
    TalonSRX m_rightTalon;

    VictorSPX m_leftVictor;
    VictorSPX m_rightVictor;

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

    //declare private variables for creating a camera tab, and putting up variables to test for angles and distance
    private ShuffleboardTab m_cameraTab;
    private NetworkTableEntry m_cameraHeight;
    private NetworkTableEntry m_cameraAngle;
    private NetworkTableEntry m_targetHeight;
    private NetworkTableEntry m_distance;

    public Robot() {
        //instantiates master motors for drive
        m_leftTalon = new TalonSRX(RobotMap.LEFT_TALON_ID);
        m_rightTalon = new TalonSRX(RobotMap.RIGHT_TALON_ID);

        //instantiates slave motors for drive
        m_leftVictor = new VictorSPX(RobotMap.LEFT_VICTOR_ID);
        m_rightVictor = new VictorSPX(RobotMap.RIGHT_VICTOR_ID);

        //instantiates the shooter using our drive motors
        //Note that this will need to be fixed when we have all systems on the robot
        m_shooter = new Launcher(0.5, m_leftTalon, m_rightTalon);
        m_shooterControl = new ShuffleboardShooterControl(m_shooter);

        //instantiates currently unused shooter motor
        // m_intakeMotor = new VictorSPX(RobotMap.INTAKE_VICTOR_ID);

        //instantiates our test controller
        m_testController = new XboxController(RobotMap.TEST_CONTROLLER_PORT);

        //instantiate drivetrain for tested
        m_driveController = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

        m_leftPiston = new DoubleSolenoid(RobotMap.LEFT_SOLENOID_FORWARD_PORT, RobotMap.LEFT_SOLENOID_REVERSE_PORT);
        m_rightPiston = new DoubleSolenoid(RobotMap.RIGHT_SOLENOID_FORWARD_PORT, RobotMap.RIGHT_SOLENOID_REVERSE_PORT);
        m_drivetrain = new ShiftDrive(m_leftTalon, m_rightTalon, m_leftVictor, m_rightVictor, m_leftPiston, m_rightPiston, true);

        m_pilotController = new PilotController(m_driveController, m_drivetrain, DriveType.kArcade);

        //gives us access to the network table for the limelight
        m_limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

<<<<<<< HEAD
    m_drivetrain = new Drivetrain(m_gyro);

    try {
      m_pather = new Pathing(m_drivetrain, m_gyro, testController);
    } catch (Exception e) {
      System.out.println("Pather failed to instantiate");
    }

    m_drivetrain.talonDriveConfig();


  }

  @Override
  public void robotInit() {
    leftTalon.set(ControlMode.PercentOutput, 0);
    rightTalon.set(ControlMode.PercentOutput, 0);
    leftVictor.set(ControlMode.PercentOutput, 0);
    rightVictor.set(ControlMode.PercentOutput, 0);

    m_drivetrain.talonDriveConfig();
  }

  @Override
  public void autonomousInit() {
     if (m_pather != null) {
      m_pather.resetFlags();
    }
  }
=======
        //sets our default state to the vision pipeline
        m_isDriverCamera = false;

        //creates a tab on the shuffleboard for the camera
        m_cameraTab = Shuffleboard.getTab("Camera");

        //Creates editable text fields to set camera height, fixed angle, and target height
        m_cameraHeight = m_cameraTab.addPersistent("Camera Height (m)", 0.0)                
                                .withWidget(BuiltInWidgets.kTextView)             
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .getEntry();
>>>>>>> 138e5b91832f43218931a19c9ff02299c6e73ac7

        m_cameraAngle = m_cameraTab.addPersistent("Camera Angle (deg)", 0.0)                
                               .withWidget(BuiltInWidgets.kTextView)             
                               .withProperties(Map.of("min", 0.0, "max", 90.0)) 
                               .getEntry();
        
        m_targetHeight = m_cameraTab.addPersistent("Target Height (m)", 0.0)                
                                .withWidget(BuiltInWidgets.kTextView)             
                                .withProperties(Map.of("min", 0.0, "max", 6.0)) 
                                .getEntry();

<<<<<<< HEAD
  @Override
  public void teleopInit() {
    if (m_pather != null) {
      m_pather.resetFlags();
    }
  }
=======
        //creates a field to display calculated distance
        m_distance = m_cameraTab.addPersistent("Distance", 0.0)
                            .getEntry();
>>>>>>> 138e5b91832f43218931a19c9ff02299c6e73ac7

    }

    @Override
    public void robotInit() {
        //zeros used motor contollers
        m_leftTalon.set(ControlMode.PercentOutput, 0);
        m_rightTalon.set(ControlMode.PercentOutput, 0);
        m_leftVictor.set(ControlMode.PercentOutput, 0);
        m_rightVictor.set(ControlMode.PercentOutput, 0);
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
        //commented out for limelight testing, uncomment for shooter testing
        // if(testController.getAButton()) {
        //     shooterControl.setSpeed();
        // }
        // else {
        //     shooterControl.zeroSpeed();
        // }

        //controls for toggling the camera mode between driver mode and vision mode
        if(m_testController.getBButtonReleased()) {
            //if it's in driver mode, set the camera to vision mode
            if(m_isDriverCamera) {
                m_limelightTable.getEntry("camMode").setNumber(0);
                m_limelightTable.getEntry("ledMode").setNumber(0);
            }
            //if it's in vision mode, set the camera to driver mode
            else {
                m_limelightTable.getEntry("camMode").setNumber(1);
                m_limelightTable.getEntry("ledMode").setNumber(1);
            }
            //toggle the variable
            m_isDriverCamera = !m_isDriverCamera;
        }

        //calculates and reports the distance from the robot to the base of the target
        double netHeight = (m_targetHeight.getDouble(0) - m_cameraHeight.getDouble(0));
        double lengthToHeightRatio = Math.tan((Math.PI / 180) * (m_cameraAngle.getDouble(0) + m_limelightTable.getEntry("ty").getDouble(0)));
        //reports the distance to the smart dashboard
        m_distance.setDouble(netHeight /  lengthToHeightRatio);
    }

  m_drivetrain.talonArcadeDrive((testController.getTriggerAxis(Hand.kRight) - testController.getTriggerAxis(Hand.kLeft)), testController.getX(Hand.kLeft), true);

    // Command for switching between the two solenoid positions. The positions are forward and reverse.
    if(testController.getXButton()){
      if(SOLENOID_POSITION == true){
        solenoidReverse();
      } else {
        solenoidForward();
        }
    }

}
