package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Drivetrain;
import frc.robot.LimelightTargeting;
import frc.robot.ControlRotator;
import frc.robot.Magazine;
import frc.robot.Climber;
import frc.robot.GamePad;
import frc.robot.Intake;
import frc.robot.Intake.Position;
import frc.robot.LimelightReader.Pipeline;

/**
 * A class to control the Intake, Launcher, Magazine, and Climber using the copilot controller
 * @author Hannah Strimpel
 * @version 3/7/2020
 */
public class CopilotController{
    public enum TargetingStage {
        kRevAndTarget, kRevToVelocity, kRunMagazine
    }

    //declare Targeting enum to keep track of current targeting state
    private TargetingStage m_targetingStage = TargetingStage.kRevAndTarget;

    //Declares the gamePad for the use of the buttons from the GamePad class
    private GamePad m_gamePad;

    //Declares the Intake to use the positioning and setting motor speeds that are created in that class
    private Intake m_intake;
    //Declares the enum position for storing the positions our intake arm can move to
    private Position m_position;

    //Declares the launcher to set the speed of the motors on the launcher
    private Launcher m_launcher;

    //Declares the climber to extend and retract the climber and to use the winch to lift the robot.
    private Climber m_climber;
    
    // Declares the magazine to index the balls into the launcher, backwards to unclog the balls, and back to the intake to dump the balls
    private Magazine m_magazine;

    //Declares the drivetrain to line up with the vision target (used with the limelightTargeting)
    private Drivetrain m_drivetrain;

    //Declares the ShuffleboardLauncherControl class in order to set the percent speed and velocity of the launcher
    private ShuffleboardLauncherControl m_launcherControl;

    //Declares the LimelightReader used for locking onto the target
    private LimelightReader m_limelightReader;

    //Declare the targeting object used to lock-on to the vision target and then lining up with the target
    private LimelightTargeting m_limelightTargeting;

    //Declares the joystick used to move the climber up and down
    private Joystick m_climbJoystick;

    /**
     * Creates the objects to allow the copilot controller/gamepad to control the intake, launcher,
     *  and shifting Gears
     * <p> We pass in the drivetrain to keep the same object as used in the PilotController.
     * All other objects are instantiated here for encapsulation
     * @param limelightReader The robot LimelightReader used to give the target to the LimelightTargeting
     */
    public CopilotController(LimelightReader limelight, Drivetrain drivetrain){
        m_gamePad = new GamePad(RobotMap.GAMEPAD_PORT);
        m_intake = new Intake();
        m_launcher = new Launcher();
        m_climber = new Climber();
        m_magazine = new Magazine();

        m_limelightReader = limelight;
        m_drivetrain = drivetrain;

        m_limelightTargeting = new LimelightTargeting(m_drivetrain, m_limelightReader);
        m_launcherControl = new ShuffleboardLauncherControl(m_launcher);
        m_climbJoystick = new Joystick(13);
    }

    /**
     * This method should be called periodically in Teleop in order to control all systems
     */
    public void periodicCopilotControl() {
        controlClimber();
        controlIntake();
        controlMagazineAndLauncher();
    }

    /**
     * Controls the climber using two buttons for moving the climber up and down,
     * a joystick to move the climber up and down,
     * and a button to lift the robot off the ground
     */
    public void controlClimber(){
        //Gets current position of the climber
        int extensionCurrent = m_climber.getExtensionMotor().getSelectedSensorPosition();
        //Extends the climber up at a speed of 0.4 when the getClimbUp button is pushed 
        //and the current position is less than the maximum position of 29700
        if(m_gamePad.getClimbUp()) {
            m_climber.extendClimber();
        } 
        //Retracts the climber down at the speed of -0.4 when the getClimb Down button is pushed
        //and the current position is greater than the minimum position of 0
        else if(m_gamePad.getClimbDown()) {
            m_climber.retractClimber();
        }
        //Zeros the climber's motor when the joystick y value is in a deadband of -0.05 to 0.05
        else if((m_climbJoystick.getY() < 0.05) && (m_climbJoystick.getY() > -0.05) ){
            m_climber.zeroExtensionMotor();
        }
        //Extends the climber up by a speed of 0.4 if the joystick y value is greater than 0.05
        //and if the current position is less than the maximum position
        else if((extensionCurrent < RobotMap.CLIMBER_EXTENSION_HARD_LIMIT) && (m_climbJoystick.getY() > 0.05)) {
            m_climber.setExtensionSpeed(RobotMap.CLIMBER_EXTENSION_MANUAL_SPEED);
        }
        //Retracts the climber by a speed of -0.4 if the joystick y value less than -0.05
        //and the current position is greater than the minimum position of 0
        else if((extensionCurrent > 0) && (m_climbJoystick.getY() < -0.05)){
            m_climber.setExtensionSpeed(-RobotMap.CLIMBER_EXTENSION_MANUAL_SPEED);
        }

        //If the getWinch button is pressed, the robot is lifted up from the ground at a speed of 0.4
        //until it reaches the target position. If the button isn't pressed, the motors are zeroed
        if(m_gamePad.getWinch()) {
            m_climber.setLiftSpeed(RobotMap.CLIMBER_WINCH_SPEED);
        } 
        else {
            m_climber.zeroLiftMotor();
        }
    }

    /**
     * Controls the launcher and magazine at the same time using one button to set the magazine speed to 0.37 moving the balls toward the launcher,
     * and it gets the launcher up to speed
     */
    public void controlMagazineAndLauncher(){
        //When we first start targeting, grab control of drivetrain and reset target
        if (m_gamePad.getLauncherAndMagazinePressed()) {
            //lock the drivetrain only on rising edge
            PilotController.is_currently_targeting = true;

            //set our stage to the first one
            m_targetingStage = TargetingStage.kRevAndTarget;

            //set our vision pipeline to targeting
            m_limelightReader.setPipeline(Pipeline.kStandard);
        }
        //while the button is held, run the targeting and launching sequence
        else if(m_gamePad.getLauncherAndMagazine()) {
            //if we are in the first stage
            if (m_targetingStage.equals(TargetingStage.kRevAndTarget)) {
                //sets the launcher to the holding speed
                m_launcher.setMotor(RobotMap.LAUNCHER_HOLDING_SPEED);

                //if we are on target and our launcher is up to speed, progress the state
                if (m_limelightTargeting.target() && (m_launcher.getMasterMotor().getMotorOutputPercent() > RobotMap.LAUNCHER_HOLDING_SPEED)) {
                    m_targetingStage = TargetingStage.kRevToVelocity;
                }
                else {
                    return;
                }
            }
            //revs the launcher up to launch speed
            else if (m_targetingStage.equals(TargetingStage.kRevToVelocity)) {
                //continue running the targeting method
                m_limelightTargeting.target();

                //set the velocity to a launch speed TODO: Grab launch speed as a function of distance
                m_launcher.setVelocity(4800);

                //if we are at speed. exit out TODO: Make 4800 a variable
                if (m_launcher.getMasterMotor().getSelectedSensorVelocity() > 4800 - RobotMap.LAUNCHER_ACCEPTABLE_ERROR) {
                    m_targetingStage = TargetingStage.kRunMagazine;
                }
            }
            //drives the magazine for launching
            else if (m_targetingStage.equals(TargetingStage.kRunMagazine)) {
                //zero drivetrain
                m_drivetrain.arcadeDrive(0, 0);

                //run our magazine to launch balls
                m_magazine.runBelt(RobotMap.MAGAZINE_LAUNCH_SPEED);
            }
        }
        //zero all motors on release and return drive control
        else if (m_gamePad.getLauncherAndMagazineReleased()) {
            m_launcher.setMotor(0);
            m_magazine.runBelt(0);
            m_drivetrain.arcadeDrive(0, 0);
            m_limelightReader.setPipeline(Pipeline.kDriver);
            PilotController.is_currently_targeting = false;
        }
        else if(m_gamePad.getMoveMagazine()){
            m_magazine.runBelt(RobotMap.MAGAZINE_LAUNCH_SPEED);
        } 
        //When the getMoveMagazineDown button is pushed, the magazine moves the balls back towars the intake at a speed of -0.37
        else if(m_gamePad.getMoveMagazineDown()){
            m_magazine.runBelt(-RobotMap.MAGAZINE_LAUNCH_SPEED);
        } 
        //when the getMoveMagazine button is pushed, magazine moves the balls toward the launcher at a speed of 0.37
        else if (m_gamePad.getIntake()) {
            m_magazine.sensorBeltControl();
        }
        //When the getDumpAllBalls buton is pressed, the magazine moves backwards and 
        //the intake then pushes the balls out of the intake TODO: This doesn't work with chinese finger trap
        else if(m_gamePad.getDumpAllBalls()){
            m_magazine.runBelt(-RobotMap.MAGAZINE_LAUNCH_SPEED);
            m_intake.setInnerIntakeMotor(-RobotMap.INNER_INTAKE_SPEED);
            m_intake.setOuterIntakeMotor(-RobotMap.OUTER_INTAKE_SPEED);
        } 
        else {
            //Stops the magazine and zeros the launcher speed
            m_magazine.runBelt(0);
            m_launcher.setMotor(0);
        }
    }

    /**
     * Turns on the Intake to take in the balls and turns the intake off to stop the intake of balls
     */
    public void controlIntake(){
        //If the button is pressed on the gamePad, the intake is enabled for the drop bar to lower and the motors to run to take in the balls
        if(m_gamePad.getIntakePressed()){
            //moves the arm to the target position (it would move the position to the target position of lowered)
            m_intake.setPosition(Position.kLowered);

            //Sets the motor speeds for the inner and outer motors to bring the balls in
            m_intake.setInnerIntakeMotor(RobotMap.INNER_INTAKE_SPEED);
            m_intake.setOuterIntakeMotor(RobotMap.OUTER_INTAKE_SPEED);
        }

        //If the B button is pressed, the intake is disabled to raise the drop bar and stop the motors
        if(m_gamePad.getDisableIntake()){
            //sets the inner and outer motor speed to 0 to stop the intake of the balls
            m_intake.setInnerIntakeMotor(0);
            m_intake.setOuterIntakeMotor(0);

            //sets the position to the new target position of raised
            m_intake.setPosition(Position.kRaised);
        }
    }

    /**
     * @return the targeting object that rotates based on limelight
     */
    public LimelightTargeting getTargeting() {
        return m_limelightTargeting;
    }
}