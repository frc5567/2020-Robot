package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Drivetrain.Gear;

/**
 * This is the basic autonomous class
 * @author Josh Overbeek
 * @version 3/9/2020
 */
public class Auton {

    /**
     * Enum for storing states of auton
     */
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

    //Declaring all member variables
    private Drivetrain m_drivetrain;
    private LimelightTargeting m_targeting;
    private Magazine m_magazine;
    private Launcher m_launcher;
    private AutonState m_state;
    private double m_tempTime = 0;

    /**
     * Constructor for auton objects
     * <p>We pass in all the objects to avoid creating multiple drivetrains, etc.
     */
    public Auton(LimelightTargeting targeting, Magazine magazine, Launcher launcher, Drivetrain drivetrain) {
        m_magazine = magazine;
        m_launcher = launcher;
        m_drivetrain = drivetrain;
        m_targeting = targeting;
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
        m_drivetrain.zeroEncoders();
    }

    /**
     * To be run in auton periodic.
     * This runs through our auton sequence
     */
    //This method follows this structure: 
    //Check state in enclosing if statement
    //Run method which returns whether it is finished
    //If the method is complete, change the state, otherwise return out
    public void periodic() {
        //back up a short amount at a low speed
        if (m_state.equals(AutonState.kInitialReverse)) {
            System.out.println("Move back");
            if (reverse(-0.15, -30000)) {
                m_state = AutonState.kTarget;
            }
            else {
                return;
            }
        }
        //lock on to the target and rev the launcher to holding speed
        else if (m_state.equals(AutonState.kTarget)) {
            System.out.println("target");
            m_launcher.setMotor(0.5);
            //if we are on target and our launcher is up to speed, progress the state
            if (m_targeting.target() && (m_launcher.getMasterMotor().getMotorOutputPercent() > 0.47)) {
                m_state = AutonState.kRevToVelocity;
            }
            else {
                return;
            }
        }
        //revs the launcher up to launch speed
        else if (m_state.equals(AutonState.kRevToVelocity)) {
            m_targeting.target();
            System.out.println("rev");
            //if we are at speed. exit out
            if (revToVelocity(4800)) {
                m_state = AutonState.kRunBalls;
                m_tempTime = Timer.getFPGATimestamp();
            }
        }
        //drives the magazine for launching
        else if (m_state.equals(AutonState.kRunBalls)) {
            //zero drivetrain
            m_drivetrain.arcadeDrive(0, 0);
            System.out.println("mag");

            //run our magazineto launch balls
            m_magazine.runBelt(RobotMap.MAGAZINE_LAUNCH_SPEED);

            //progress state after three seconds
            if (m_tempTime + 3 < Timer.getFPGATimestamp()) {
                m_state = AutonState.kEnd;
            }
        }
        //kill all motors as the final state
        else if (m_state.equals(AutonState.kEnd)) {
            System.out.println("end");
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
        if (m_drivetrain.getLeftDriveEncoderPosition() < target || m_drivetrain.getRightDriveEncoderPosition() < target) {
            m_drivetrain.arcadeDrive(0, 0);
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
        m_launcher.setVelocity(velocity * RobotMap.RPM_TO_UNITS_PER_100MS);
        if ((m_launcher.getMasterMotor().getSelectedSensorVelocity() / RobotMap.RPM_TO_UNITS_PER_100MS) > (velocity - 50)) {
            return true;
        }
        else {
            return false;
        }
    }
}