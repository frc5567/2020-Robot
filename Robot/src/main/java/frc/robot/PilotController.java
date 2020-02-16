package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Drivetrain.Gear;

/**
 * A class to control the drivetrain with the pilot controller
 * @author Josh Overbeek
 * @version 1/25/2020
 */
public class PilotController {
    /**
     * Enum to indicate different control systems
     */
    public enum DriveType {
        kTank, kArcade;
    }

    //declare our drivetrain and our controller
    private XboxController m_controller;
    private Drivetrain m_drivetrain;

    private LauncherTargeting m_launcherTargeting;
    private final DriveType m_driveType;

    //scalars and network table entries to scale our input on our drivetrain
    //this is to reduce our speed for driver testing and potentially for comp
    private double m_highGearVelocityScalar = 0.6;
    private double m_highGearTurnScalar = 0.6;
    private double m_lowGearVelocityScalar = 0.6;
    private double m_lowGearTurnScalar = 0.6;
    private NetworkTableEntry m_highGearVelocityScalarEntry;
    private NetworkTableEntry m_highGearTurnScalarEntry;
    private NetworkTableEntry m_lowGearVelocityScalarEntry;
    private NetworkTableEntry m_lowGearTurnScalarEntry;
    private ShuffleboardTab m_driverTab;


    /**
     * Creates an object to allow the pilot to control the drivetrain
     * 
     * @param controller The pilot controller to control the drive train with
     * @param drivetrain The robot drivetrain
     * @param driveType The type of drive control that the pilot wants (tank or arcade)
     * @param launcherTargting The targeting object used to lock on to our target
     */
    public PilotController(XboxController controller, Drivetrain drivetrain, DriveType driveType, LauncherTargeting launcherTargeting) {
        m_controller = controller;
        m_drivetrain = drivetrain;
        m_driveType = driveType;
        m_launcherTargeting = launcherTargeting;
    }

    /**
     * Controls our drivetrain with an arcade control system
     * Triggers are forward and back (left trigger is back, right is forward), left x stick is turn
     */
    private void arcadeDrive() {
        //read our current turn
        double turnInput =  m_controller.getX(Hand.kLeft);

        //if our input is less than our deadband, ignore it by setting the input to zero
        if (Math.abs(turnInput) < RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            turnInput = 0;
        }

        //correct input so that just barely over the deadband is just barely over zero
        //effectively this centers our input on zero rather than on 0+/- deadband
        if (turnInput > RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            turnInput -= RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;
        }
        else if (turnInput < -RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            turnInput += RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;
        }

        //run our drivetrain with the adjusted input
        m_drivetrain.arcadeDrive(m_controller.getTriggerAxis(Hand.kRight) - m_controller.getTriggerAxis(Hand.kLeft), turnInput);
    }

    /**
     * Controls our drivetrain with a tank control system
     * Left stick y is left side speed, right stick x is right side speed
     */
    private void tankDrive() {
        //read our current stick input
        double leftInput =  m_controller.getY(Hand.kLeft);
        double rightInput =  m_controller.getY(Hand.kRight);

        //if our input is less than our deadband, ignore it by setting the input to zero
        if (Math.abs(leftInput) < RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            leftInput = 0;
        }

        //correct input so that just barely over the deadband is just barely over zero
        //effectively this centers our input on zero rather than on 0+/- deadband
        if (leftInput > RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            leftInput -= RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;
        }
        else if (leftInput < -RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            leftInput += RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;
        }

        if (Math.abs(rightInput) < RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            rightInput = 0;
        }

        //correct input so that just barely over the deadband is just barely over zero
        //effectively this centers our input on zero rather than on 0+/- deadband
        if (rightInput > RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            rightInput -= RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;
        }
        else if (rightInput < -RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            rightInput += RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;
        }

        //run our drivetrain with the adjusted input
        //inputs are negative because forward on the stick is naturally negative, so we invert it to make controls intuitive
        m_drivetrain.tankDrive(-leftInput, -rightInput);
    }

    /**
     * Sets us to high gear on x button input and low gear on y button input
     */
    private void controlGear() {
        if (m_controller.getXButtonReleased()) {
            m_drivetrain.shiftGear(Gear.kHighGear);
        }
        else if (m_controller.getYButtonReleased()) {
            m_drivetrain.shiftGear(Gear.kLowGear);
        }
    }
    
    /**
     * Controls all pilot controlled systems
     */
    public void controlDriveTrain() {
        //if the b button is pressed, lock onto the high target
        if (m_controller.getBButton()) {
            m_launcherTargeting.target();
        }
        //when the b button isn't pressed, run the drive train as normal
        else {
            //runs our drivetrain based on control scheme passed in
            if (m_driveType == DriveType.kArcade) {
                arcadeDrive();
            }
            else if (m_driveType == DriveType.kTank) {
                tankDrive();
            }
        }

        //Controls shifting the gears off of the x button
        controlGear();
    }
}