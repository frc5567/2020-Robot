package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * A class to control the drivetrain with the pilot controller, currently uses arcade drive
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
    private final DriveType m_driveType;

    /**
     * Creates an object to allow the pilot to control the drivetrain
     * 
     * @param controller The pilot controller to control the drive train with
     * @param drivetrain The robot drivetrain
     * @param driveType The type of drive control that the pilot wants (tank or arcade)
     */
    public PilotController(XboxController controller, ShiftDrive drivetrain, DriveType driveType) {
        m_controller = controller;
        m_drivetrain = drivetrain;
        m_driveType = driveType;
    }

    /**
     * Controls our drivetrain with an arcade control system
     * Triggers are forward and back (left trigger is back, right is forward), left x stick is turn
     */
    private void arcadeDrive() {
        m_drivetrain.arcadeDrive(m_controller.getTriggerAxis(Hand.kRight) - m_controller.getTriggerAxis(Hand.kLeft), m_controller.getX(Hand.kLeft));
    }

    /**
     * Controls our drivetrain with a tank control system
     * Left stick y is left side speed, right stick x is right side speed
     */
    private void tankDrive() {
        m_drivetrain.tankDrive(m_controller.getY(Hand.kLeft), m_controller.getY(Hand.kRight));
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
     */
    public void controlDriveTrain() {
        //runs our drivetrain based on control scheme passed in
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