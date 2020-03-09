package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Intake.Position;

public class TestCopilotController {
    private Intake m_intake;
    private Magazine m_magazine;
    private Launcher m_launcher;
    private XboxController m_controller;
    private PilotController m_pilotController;
    private boolean atLaunchSpeed = false;

    public TestCopilotController(Intake intake, Magazine magazine, Launcher launcher, PilotController pilotController, LimelightReader limelight) {
        m_intake = intake;
        m_magazine = magazine;
        m_launcher = launcher;
        m_pilotController = pilotController;
        m_controller = new XboxController(3);
    }

    public void controlSystemsPeriodic() {
        if (m_controller.getAButton()) {
            targetAndLaunch();
        }
        else {
            m_launcher.setMotor(0.0);
            m_pilotController.controlDriveTrainPeriodic();
        }
        
        if (m_controller.getXButtonPressed()) {
            dropBarAndIntake();
        }
        else if (m_controller.getYButtonPressed()) {
            raiseBar();
        }

    }

    public void targetAndLaunch() {
        m_pilotController.getTargeting().target();
    }

    public void dropBarAndIntake() {
        m_intake.setPosition(Position.kLowered);
        m_intake.setInnerIntakeMotor(0.3);
        m_intake.setOuterIntakeMotor(0.6);
        m_magazine.sensorBeltControl();
    }

    public void raiseBar() {
        m_intake.setPosition(Position.kRaised);
        m_intake.setInnerIntakeMotor(0.0);
        m_intake.setOuterIntakeMotor(0.0);
    }

}