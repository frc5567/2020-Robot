package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Drivetrain.Gear;

/**
 * This is the basic autonomous class
 * @author Josh Overbeek
 * @version 3/9/2020
 */
public class Auton implements AutonBase {

    /**
     * This enum stores the possible states for auton in order
     * <p>Possible values:
     * <li>{@link #kInitialReverse}</li>
     * <li>{@link #kTarget}</li>
     * <li>{@link #kRevToVelocity}</li>
     * <li>{@link #kRunBalls}</li>
     * <li>{@link #kEnd}</li>
     */
    public enum AutonState {
        /**
         * The initial reverse where we back away from the starting line
         */
        kInitialReverse(0), 
        /**
         * The targeting phase where we lock onto the target
         */
        kTarget(1), 
        /**
         * The phase where we rev the launcher to launch velocity
         */
        kRevToVelocity(2), 
        /**
         * The phase where we feed the balls through the launcher
         */
        kRunBalls(3), 
        /**
         * The end phase. This should always be the last case that we run
         */
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

    //the launch speed of the launcher in encoder units per 100ms
    private double m_launchSpeed = 4800;

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
        m_drivetrain.shiftGear(Gear.kLowGear);  //set the gearing to low gear to ensure expected movement
        m_launcher.resetPIDF();                 //reset the PIDF on the launcher to clear any accumulated error
        m_launcher.zeroEncoder();               //zero the encoder on the launcher
        m_launcher.setMotor(0);                 //ensure the launcher is not moving
        m_magazine.setStoredBalls(3);           //set the magazine to contain three balls. (UNUSED CURRENTLY)
        m_magazine.runBelt(0);                  //ensure the magazine is not moving
        m_targeting.resetPID();                 //reset the PID constants to their RobotMap values
        m_targeting.resetError();               //reset the PID on the targeting to clear any accumulated error
        m_state = AutonState.kInitialReverse;   //set the auton to its initial state
        m_drivetrain.zeroEncoders();            //zero drivetrain encoders to ensure consistency
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
            //commented out, uncomment for debugging auton
            //System.out.println("Move back");
            
            if (reverse(-0.15, -30000)) { //TODO: these values should not be hardcoded in a final version
                m_state = AutonState.kTarget;
            }
            else {
                return;
            }
        }
        //lock on to the target and rev the launcher to holding speed
        else if (m_state.equals(AutonState.kTarget)) {
            //commented out, uncomment for debugging auton
            //System.out.println("target");

            //rev the launcher to the holding speed in open loop control to avoid(?) brownouts
            m_launcher.setMotor(RobotMap.LAUNCHER_HOLDING_SPEED);

            //if we are on target and our launcher is up to speed, progress the state
            if (m_targeting.target() && 
               (m_launcher.getMasterMotor().getMotorOutputPercent() > RobotMap.LAUNCHER_HOLDING_SPEED - 0.03)) {
                m_state = AutonState.kRevToVelocity;
            }
            else {
                return;
            }
        }
        //revs the launcher up to launch speed
        else if (m_state.equals(AutonState.kRevToVelocity)) {
            //run the limelight targeting method to lock onto the high target
            m_targeting.target();

            //commented out, uncomment for debugging auton
            //System.out.println("rev");

            //if we are at speed. exit out
            if (revToVelocity(m_launchSpeed)) {
                m_state = AutonState.kRunBalls;
                m_tempTime = Timer.getFPGATimestamp();
            }
        }
        //drives the magazine for launching
        else if (m_state.equals(AutonState.kRunBalls)) {
            //zero drivetrain
            m_drivetrain.arcadeDrive(0, 0);

            //commented out, uncomment for debugging auton
            //System.out.println("mag");

            //run our magazine to launch balls
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
     * 
     * @param speed percent speed
     * @param target target in encoder ticks
     * @return Whether we have hit our target
     */
    public boolean reverse(double speed, double target) {
        //ensure that our target and speed are negative to prevent unexpected results
        speed = -Math.abs(speed);
        target = -Math.abs(target);

        //if we have moved further back than our target on either side, stop moving and return true
        if (m_drivetrain.getLeftDriveEncoderPosition() < target || m_drivetrain.getRightDriveEncoderPosition() < target) {
            m_drivetrain.arcadeDrive(0, 0);
            return true;
        }
        //otherwise, drive at the inputted speed
        else {
            m_drivetrain.arcadeDrive(speed, 0);
            return false;
        }
    }

    /**
     * Revs the launcher up to a holding speed to prevent or limit brownouts
     * with open loop, fixed acceleration control
     * 
     * @param holdingSpeed The holding speed of the launcher in percent speed
     */
    public void revToHold(double holdingSpeed) {
        m_launcher.setMotor(holdingSpeed);
    }

    /**
     * Revs the launcher to its launch speed with closed loop velocity control
     * 
     * @param velocity The launch speed of the launcher in RPM
     */
    public boolean revToVelocity(double velocity) {
        //convert the speed in RPM to units per 100ms and set the launcher velocity with that
        m_launcher.setVelocity(velocity * RobotMap.RPM_TO_UNITS_PER_100MS);

        //if our current speed is close enough to our target, return that we have reached it
        if ((m_launcher.getMasterMotor().getSelectedSensorVelocity() / RobotMap.RPM_TO_UNITS_PER_100MS) > (velocity - 50)) {
            return true;
        }
        else {
            return false;
        }
    }
}