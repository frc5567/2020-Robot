package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.ShiftDrive.Gear;

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
    private ShiftDrive m_drivetrain;

    private DriveType m_driveType;

    private double m_inputScalar = 0.6;
    private NetworkTableEntry m_inputScalarEntry;
    private ShuffleboardTab m_driverTab;

    /**
     * Creates an object to allow the pilot to control the drivetrain
     * 
     * @param controller The pilot controller to control the drive train with
     * @param drivetrain The robot drivetrain
     * @param driveType The type of drive control that the pilot wants (tank or arcade)
     * @param launcherTargting The targeting object used to lock on to our target
     */
    public PilotController(XboxController controller, ShiftDrive drivetrain, DriveType driveType) {
        m_controller = controller;
        m_drivetrain = drivetrain;
        m_driveType = driveType;

        m_driverTab = Shuffleboard.getTab("Driver Tab");

        m_inputScalarEntry = m_driverTab.add("Input Scalar", 0.6)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry();
    }

    public void setInputScalar() {
        m_inputScalar = m_inputScalarEntry.getDouble(0.6);
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

        turnInput *= m_inputScalar;

        //run our drivetrain with the adjusted input
        m_drivetrain.arcadeDrive(m_controller.getTriggerAxis(Hand.kRight) - m_controller.getTriggerAxis(Hand.kLeft) * m_inputScalar, turnInput);
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
     * Toggles our gear when the x button is pressed
     */
    private void controlGear() {
        if (m_controller.getXButtonReleased()) {
            m_drivetrain.shiftGear(Gear.kHigh);
        }
        else if (m_controller.getYButtonReleased()) {
            m_drivetrain.shiftGear(Gear.kLow);
        }
    }
    
    /**
     * Controls all pilot controlled systems
     */
    public void controlDriveTrain() {
        //runs our drivetrain based on control scheme passed in
        setInputScalar();

        if (m_driveType == DriveType.kArcade) {
            arcadeDrive();
        }
        else if (m_driveType == DriveType.kTank) {
            tankDrive();
        }

        //Controls shifting the gears off of the x button
        controlGear();
    }
}