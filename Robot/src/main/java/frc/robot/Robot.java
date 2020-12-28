/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.LimelightReader.Pipeline;
import frc.robot.PilotController.DriveType;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * 
 * @version 3/9/2020
 */
public class Robot extends TimedRobot {

    //declares controller and drivetrain for testing drive code
    /** The class that we wrote to read values from the controller and control the drivetrain */
    private PilotController m_pilotController;

    /** The copilot class for controlling all other systems on the robot */
    private CopilotController m_copilotController;
    
    /** The auton class that runs our auton */
    private Auton m_auton;

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

        m_limelightReader = new LimelightReader();

        //intantiates our PilotController, which controls all systems on the drivetrain
        m_pilotController = new PilotController(DriveType.kArcade, m_limelightReader);
    
        //sets our default state to the vision pipeline
        m_isDriverCamera = false;

        m_copilotController = new CopilotController(m_limelightReader, m_pilotController.getTargeting(), m_pilotController.getDrivetrain());

        m_auton = new Auton(m_pilotController.getTargeting(), m_copilotController.getMagazine(), 
                            m_copilotController.getLauncher(), m_pilotController.getDrivetrain());

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
        //force limelight into targeting mode pre-auton
        m_limelightReader.setPipeline(Pipeline.kStandard);
    }
    
    @Override
    public void autonomousPeriodic() {
        m_auton.periodic();
    }

    @Override
    public void teleopInit() {
        m_pilotController.getDrivetrain().setNeutralMode(NeutralMode.Brake);
    }
        
    @Override
    public void teleopPeriodic() {
        m_pilotController.controlDriveTrainPeriodic();
        m_copilotController.periodicCopilotControl();
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
        
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