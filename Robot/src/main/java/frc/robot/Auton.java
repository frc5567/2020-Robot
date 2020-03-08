package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Drivetrain.Gear;

public class Auton {
    public enum AutonState {
        kInitialReverse(0), 
        kTarget(1), 
        kRevToVelocity(2), 
        kRunBalls(3), 
        kEnd(4);

        private int stateNumber;

        private AutonState(int stateNumber) {
            this.stateNumber = stateNumber;
        }

        public int getStateNumber() {
            return stateNumber;
        }

        public boolean equals(AutonState autonState) {
            return (this.stateNumber == autonState.getStateNumber());
        }
    }

    private Drivetrain m_drivetrain;
    private LimelightTargeting m_targeting;
    private Magazine m_magazine;
    private Launcher m_launcher;
    private AutonState m_state;
    private double m_tempTime = 0;

    public Auton(LimelightReader limelight) {
        m_magazine = new Magazine();
        m_launcher = new Launcher();
        m_drivetrain = new Drivetrain(true);
        m_targeting = new LimelightTargeting(m_drivetrain, limelight);
        m_state = AutonState.kInitialReverse;
    }

    /**
     * To be run in auton init.
     * Runs all config methods, zeros all motors, etc.
     */
    public void init() {
        m_drivetrain.shiftGear(Gear.kLowGear);
        m_launcher.resetPIDF();
        m_launcher.zeroEncoder();
        m_launcher.setMotor(0);
        m_magazine.setStoredBalls(3);
        m_magazine.runBelt(0);
        m_targeting.resetPID();
        m_targeting.resetError();
        m_state = AutonState.kInitialReverse;

    }

    public void periodic() {
        //back up for brief period
        if (m_state.equals(AutonState.kInitialReverse)) {
            if (reverse(-0.3, -200)) {
                m_state = AutonState.kTarget;
            }
            else {
                return;
            }
        }
        else if (m_state.equals(AutonState.kTarget)) {
            m_launcher.setMotor(0.5);
            m_drivetrain.arcadeDrive(0, 0);
            if (m_targeting.target()) {
                m_state = AutonState.kRevToVelocity;
            }
            else {
                return;
            }
        }
        else if (m_state.equals(AutonState.kRevToVelocity)) {
            if (revToVelocity(6000)) {
                m_state = AutonState.kRunBalls;
                m_tempTime = Timer.getFPGATimestamp();
            }
        }
        else if (m_state.equals(AutonState.kRunBalls)) {
            runMagazine(0.8);
            if (m_tempTime + 5 < Timer.getFPGATimestamp()) {
                m_state = AutonState.kEnd;
            }
        }
        else if (m_state.equals(AutonState.kEnd)) {
            m_drivetrain.arcadeDrive(0, 0);
            m_launcher.setMotor(0);
            m_magazine.runBelt(0);
        }
    }

    /**
     * Backs the robot up
     * @param speed percent speed
     * @param target target in encoder ticks
     * @return Whether we have hit our target
     */
    public boolean reverse(double speed, double target) {
        if (m_drivetrain.getLeftDriveEncoderPosition() < target && m_drivetrain.getLeftDriveEncoderPosition() < target) {
            return true;
        }
        else {
            m_drivetrain.arcadeDrive(speed, 0);
            return false;
        }
    }

    public void revToHold(double holdingSpeed) {
        m_launcher.setMotor(holdingSpeed);
    }

    public boolean revToVelocity(double velocity) {
        m_launcher.setVelocity(velocity);
        if (Math.abs(m_launcher.getMasterMotor().getClosedLoopError()) < RobotMap.LAUNCHER_ACCEPTABLE_ERROR) {
            return true;
        }
        else {
            return false;
        }
    }

    public void runMagazine(double speed) {
        m_magazine.runBelt(speed);
    }
 
}