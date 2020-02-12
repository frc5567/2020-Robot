package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * Currently empty class to organize everything the copilot controller will do
 * <p>
 * This should be all non-drivetrain systems, such as launcher, climber, intake
 * and magazine
 */
public class CopilotController {
    //declare private instance variables
    //launcher
    Launcher m_launcher;

    //climber
    Climber m_climber;

    //intake
    Intake m_intake;

    //magazine?
    Magazine m_magazine;

    LimelightReader m_limelight;

    GenericHID m_gamepad;

    boolean m_isFinishedMovingBalls = false;
    boolean m_isBallMoving = false;
    /**
     * constructor
     * 
     * @param launcher
     * @param climber
     * @param intake
     * @param magazine
     * @param limelight
     * @param gamepad
     */
    public CopilotController(Launcher launcher, Climber climber, Intake intake, Magazine magazine,
            LimelightReader limelight, GenericHID gamepad) {
        //pass in variables and instantiate the instance variables
        m_launcher = launcher;
        m_climber = climber;
        m_intake = intake;
        m_magazine = magazine;
        m_limelight = limelight;
        m_gamepad = gamepad;

    }

    /**
     * This method will turn the launcher on and off / rev launcher wheel
     */
    public void controlLauncher() {
        //revs up the launcher with a distance retrieved from the limelight
        //the zero parameter is the angle the camera is fixed at relative to the ground. This could be passed in from the gyro, but -may- not be necesary
        //currently zero as no gyro is implemented, so we assume its parralel to the ground

        //the gamepad input is generic until an actual gamepad exists
        if(m_gamepad.getRawButton(1)) {
            m_launcher.revLauncher(m_limelight.getDistance(0));
        }
    }

    /**
     * This method will read input off of the copilot controller / dashboard to adjust speed based on wear
     * and tear of balls
     */
    public void adjustLauncherSpeed() {
        
    }

    /**
     * Control the climber based on copilot controller input
     */
    public void controlClimber() {
        //gamepad button needs to be replaced with an actual gamepad class
        if(m_gamepad.getRawButton(2)) {
            //extend the climber during a certain button press
            m_climber.extendClimber();
        }
        else if (m_gamepad.getRawButton(3)) {
            //retract the climber if its not extending and if a button is pressed
            m_climber.retractClimber();
        }
        else if (m_gamepad.getRawButton(4)) {
            //these values are currently arbitrary as we have no idea how this actually works yet
            m_climber.setLiftSpeed(0.3);
            m_climber.zeroExtensionMotor();
        }
        else {
            //zero all motors when there isn't an active input
            m_climber.zeroMotors();
        }

    }

    /**
     * Control the magazine
     */
    public void controlMagazine() {
        //if we are pressing the button that revs the launcher
        if(m_gamepad.getRawButton(1)) {

        }

        //if arbitrary button is released, start moving balls one spot
        if(m_gamepad.getRawButtonReleased(10)) {
            m_isBallMoving = true;
        } 

        //this construct is designed to move the balls exactly one spot and then stop the motors
        if (m_isBallMoving) {
            if(!m_isFinishedMovingBalls) {
                //set a value to false. That value will only change when the method returns true
                m_isFinishedMovingBalls = m_magazine.moveBallsOneSegment(0.1);
            }
            else {
                m_isBallMoving = false;
            }
        }

    }

    public void launchSequence() {
        //only does stuff if the magazine thinks it has balls
        if (m_magazine.getStoredBalls() > 0) {
            //rev the launcher constantly
            m_launcher.revLauncher(m_limelight.getDistance(0));

            //enter this if 
            if (m_isBallMoving) {
                if(!m_isFinishedMovingBalls) {
                    //set a value to false. That value will only change when the method returns true
                    m_isFinishedMovingBalls = m_magazine.moveBallsOneSegment(0.1);
                }
                else {
                    m_isBallMoving = false;
                }
            }
            else {
                m_magazine.spinPopUpWheel(2);
            }




        }

    }

    /**
     * Call all copilot controls
     */
    public void controlSystems() {
        m_magazine.manageStoredBalls();

    }


}