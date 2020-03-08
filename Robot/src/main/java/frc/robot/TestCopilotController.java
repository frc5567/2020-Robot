package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Intake.Position;

public class TestCopilotController {
    private Intake m_intake;
    private Magazine m_magazine;
    private Launcher m_launcher;
    private LimelightTargeting m_targeting;
    private Drivetrain m_drivetrain;
    private XboxController m_controller;
    private boolean isTargeting;

    public TestCopilotController(Intake intake, Magazine magazine, Launcher launcher
                                , LimelightTargeting targeting, Drivetrain drivetrain) {
        m_intake = intake;
        m_magazine = magazine;
        m_launcher = launcher;
        m_targeting = targeting;
        m_drivetrain = drivetrain;
        m_controller = new XboxController(3);
    }

    public void controlSystemsPeriodic() {
        if (m_controller.getAButton()) {
            targetAndLaunch();
        }
        else {
            isTargeting = false;
            m_launcher.setMotor(0.0);
        }
        
        if (m_controller.getXButtonPressed()) {
            dropBarAndIntake();
        }
        else if (m_controller.getYButtonPressed()) {
            raiseBar();
        }

    }

    public void targetAndLaunch() {

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