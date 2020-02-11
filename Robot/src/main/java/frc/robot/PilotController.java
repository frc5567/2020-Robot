package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

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
     * 
     *  @param setter The setter is true when the speed is being adjusted to conserve battery, if it is false it uses the raw input
     */
    private void arcadeDrive(boolean setter) {
        //read our current turn
        double turnInput =  m_controller.getX(Hand.kLeft);

        //if our input is less than our deadband, ignore it by setting the input to zero
        if (Math.abs(turnInput) < RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            turnInput = 0;
        }

        //run our drivetrain with the adjusted input
        m_drivetrain.arcadeDrive(m_controller.getTriggerAxis(Hand.kRight) - m_controller.getTriggerAxis(Hand.kLeft), turnInput, setter);
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

        if (Math.abs(rightInput) < RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            rightInput = 0;
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
            m_drivetrain.switchGear();
        }
    }
    
    /**
     * Controls all pilot controlled systems
     * @param setter The setter is true when the speed is being adjusted to conserve battery, if it is false it uses the raw input
     */
    public void controlDriveTrain(boolean setter) {
        //if the b button is pressed, lock onto the high target
        if (m_controller.getBButton()) {
            m_launcherTargeting.target();
        }
        //when the b button isn't pressed, run the drive train as normal
        else {
            //runs our drivetrain based on control scheme passed in
            if (m_driveType == DriveType.kArcade) {
                arcadeDrive(setter);
            }
            else if (m_driveType == DriveType.kTank) {
                tankDrive();
            }
        }

        //Controls shifting the gears off of the x button
        controlGear();
    }
}