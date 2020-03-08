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

/**
 * A class to control the Intake, Launcher, Magazine, and Climber using the copilot controller
 * @author Hannah Strimpel
 * @version 3/7/2020
 */
public class CopilotController{

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

    //Declares the controlRotator class to move the control panel to the right color/spins
    private ControlRotator m_controlRotator;

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
     * Creates the objects to allow the copilot controller/gamepad to control the intake, launcher, and shifting Gears
     * @param gamePad The buttons/controller that is used to control the intake, launcher, and shifting Gears
     * @param intake The robot intake used to enable and disable the intake by starting and disabling the motors and moving the arm position
     * @param position The enum that stores the position that the pneumatics/piston/arm is in
     * @param launcher The robot launcher used to start/enable and stop/disable the launcher motors
     * @param climber The robot climber used to extend and retract the climber as well as lift the robot off of the ground
     * @param magazine The robot magazine used to moves the balls to the launcher and back to the intake
     * @param controlRotator The robot controlRotator used to move the control panel
     * @param limelightReader The robot LimelightReader used to give the target to the LimelightTargeting
     */
    public CopilotController(GamePad gamePad, Intake intake, Position position, Launcher launcher, Climber climber, Magazine magazine, ControlRotator controlRotator, LimelightReader limelight){
        m_gamePad = gamePad;
        m_intake = intake;
        m_position = position;
        m_launcher = launcher;
        m_climber = climber;
        m_magazine = magazine;
        m_controlRotator = controlRotator;
        m_limelightReader = limelight;
        m_drivetrain = new Drivetrain(RobotMap.DRIVETRAIN_HAS_TWO_SOLENOIDS);
        m_limelightTargeting = new LimelightTargeting(m_drivetrain, m_limelightReader);
        m_launcherControl = new ShuffleboardLauncherControl(m_launcher);
        m_climbJoystick = new Joystick(13);
    }

    /**
     * Controls the climber using two buttons for moving the climber up and down,
     * a joystick to move the climber up and down,
     * and a button to lift the robot off the ground
     * @param targetPos the target position for the encoder when lifting the robot up
     */
    public void controlClimber(int targetPos){
        //Gets current position of the climber
        int extensionCurrent = m_climber.getExtensionMotor().getSelectedSensorPosition();
        //Extends the climber up at a speed of 0.4 when the getClimbUp button is pushed 
        //and the current position is less than the maximum position of 29700
        if((extensionCurrent < 29700) && m_gamePad.getClimbUp()) {
            m_climber.setExtensionSpeed(0.4);
            m_climber.extendClimber();
        } 
        //Retracts the climber down at the speed of -0.4 when the getClimb Down button is pushed
        //and the current position is greater than the minimum position of 0
        else if((extensionCurrent > 0) && m_gamePad.getClimbDown()) {
            m_climber.setExtensionSpeed(-0.4);
            m_climber.retractClimber();
        }
        //Stops the climber's motor by setting it to zero
        else {
            m_climber.zeroExtensionMotor();
        }

        //Zeros the climber's motor when the joystick y value is in a deadband of -0.05 to 0.05
        if((m_climbJoystick.getY() < 0.05) && (m_climbJoystick.getY() > -0.05) ){
            m_climber.zeroExtensionMotor();
        }
        //Extends the climber up by a speed of 0.4 if the joystick y value is greater than 0.05
        //and if the current position is less than the maximum position of 29700
        else if((extensionCurrent < 29700) && (m_climbJoystick.getY() > 0.05)) {
            m_climber.setExtensionSpeed(0.4);
            m_climber.extendClimber();
        }
        //Retracts the climber by a speed of -0.4 if the joystick y value less than -0.05
        //and the current position is greater than the minimum position of 0
        else if((extensionCurrent > 0) && (m_climbJoystick.getY() < -0.05)){
            m_climber.setExtensionSpeed(-0.4);
            m_climber.retractClimber();
        }

        //If the getWinch button is pressed, the robot is lifted up from the ground at a speed of 0.4
        //until it reaches the target position. If the button isn't pressed, the motors are zeroed
        if(m_gamePad.getWinch()) {
            m_climber.liftRobot(targetPos, 0.4);
        } else {
            m_climber.zeroLiftMotor();
        }
    }

    /**
     * Controls the magazine by using a button to move the balls toward the launcher
     * and to move the balls back toward the intake
     */
    public void controlMagazine(){
        //when the getMoveMagazine button is pushed, magazine moves the balls toward the launcher at a speed of 0.37
        if(m_gamePad.getMoveMagazine()){
            m_magazine.runBelt(0.37);
        } 
        //When the getMoveMagazineDown button is pushed, the magazine moves the balls back towars the intake at a speed of -0.37
        else if(m_gamePad.getMoveMagazineDown()){
            m_magazine.runBelt(-0.37);
        } 
        //Stops the magazine when none of the two buttons are pushed by setting the speed to 0
        else {
            m_magazine.runBelt(0);
        }
    }

    /**
     * Uses a button to get the launcher up to speed for launching the balls
     */
    //TODO: Figure out how to Rev the motors up to speed
    public void controlLauncher(){
        //Uses the getRevLauncher button to get the percent speed, lock onto and rotate to the target,
        //and increase the launcher speed up to the maximum speed. When the button isn't pushed, the motors are zeroed
        if(m_gamePad.getRevLauncher()) {
            m_launcherControl.setPercentSpeed();
            m_limelightTargeting.target();
            m_launcherControl.m_currentVel.setDouble(m_launcher.getMasterMotor().getSelectedSensorVelocity() / RobotMap.RPM_TO_UNITS_PER_100MS);
            //TODO: Rev Motor
        } else {
            m_launcherControl.zeroSpeed();
        }
    }

    /**
     * Controls the launcher and magazine at the same time using one button to set the magazine speed to 0.37 moving the balls toward the launcher,
     * and it gets the launcher up to speed
     */
    //TODO: check what to do with launcher
    public void controlMagazineAndLauncher(){
        //The magazine moves the balls toward the launcher at the speed of 0.37 and gets the launcher up to speed when the button is pushed
        if(m_gamePad.getLauncherAndMagazine()) {
            m_magazine.runBelt(0.37);
            m_launcherControl.setPercentSpeed();
            m_limelightTargeting.target();
            m_launcherControl.m_currentVel.setDouble(m_launcher.getMasterMotor().getSelectedSensorVelocity() / RobotMap.RPM_TO_UNITS_PER_100MS);
            //TODO: Rev Launcher
        }
        else {
            //Stops the magazine and zeros the launcher speed
            m_magazine.runBelt(0);
            m_launcherControl.zeroSpeed();
        }
    }

    /**
     * Turns on the Intake to take in the balls and turns the intake off to stop the intake of balls
     */
    public void controlIntake(){

        //Determine the target position that the arm is moving to by using the opposite position of where the arm is currently
        if(m_intake.getPosition() == Position.kLowered){
                m_position = Position.kRaised;
            } else {
                m_position = Position.kLowered;
            }

        //If the button is pressed on the gamePad, the intake is enabled for the drop bar to lower and the motors to run to take in the balls
        if(m_gamePad.getIntake()){
            //moves the arm to the target position (it would move the position to the target position of lowered)
            m_intake.setPosition(m_position);

            //Sets the motor speeds for the inner and outer motors to bring the balls in
            m_intake.setInnerIntakeMotor(RobotMap.COPILOT_CONTROLLER_INNER_INTAKE_SPEED);
            m_intake.setOuterIntakeMotor(RobotMap.COPILOT_CONTROLLER_OUTER_INTAKE_SPEED);

            //gives us the string name of the position that the arm is at currently
            m_intake.getPositionName();
        }

        //If the B button is pressed, the intake is disabled to raise the drop bar and stop the motors
        if(m_gamePad.getDisableIntake()){
            //sets the inner and outer motor speed to 0 to stop the intake of the balls
            m_intake.setInnerIntakeMotor(0);
            m_intake.setOuterIntakeMotor(0);

            //sets the position to the new target position of raised
            m_intake.setPosition(m_position);

            //Gices us the string name of the position that the arm is at currently
            m_intake.getPositionName();
        }
    }

    /**
     * Uses a button to run both the magazine and intake to push all of the balls out of the magazine
     */
    public void dumpBalls(){
        //When the getDumpAllBalls butoonn is pressed, the magazine moves backwards at a speed of -0.37 to push the ball back to the intake
        //the intake then pushes the balls out of the intake
        if(m_gamePad.getDumpAllBalls()){
            m_magazine.runBelt(-0.37);
            m_intake.setInnerIntakeMotor(-0.3);
            m_intake.setOuterIntakeMotor(-0.3);
        } else {
            //Stops the magazine and zeros the intake motors
            m_magazine.runBelt(0);
            m_intake.setInnerIntakeMotor(0);
            m_intake.setOuterIntakeMotor(0);
        }
    }

    public void controlColorWheel(){
        if(m_gamePad.getColorWheelColor()){
            m_controlRotator.getRecievedColor();
            m_controlRotator.manualRotate(0.3);
        } else {
            m_controlRotator.manualRotate(0);
        }

        if(m_gamePad.getColorWheelDistance()){
            m_controlRotator.autoRotate(0.3);
        }
    }

    /**
     * @return the targeting object that rotates based on limelight
     */
    public LimelightTargeting getTargeting() {
        return m_limelightTargeting;
    }
}