package frc.robot;

import frc.robot.ControlRotator;
import frc.robot.Magazine;
import frc.robot.Climber;
import frc.robot.GamePad;
import frc.robot.Intake;
import frc.robot.Intake.Position;

/**
 * A class to control the Intake, Launcher, and Gears using the copilot controller
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

    private Climber m_climber;
    
    private Magazine m_magazine;

    private ControlRotator m_controlRotator;

    /**
     * Creates the objects to allow the copilot controller/gamepad to control the intake, launcher, and shifting Gears
     * @param gamePad The buttons/controller that is used to control the intake, launcher, and shifting Gears
     * @param drivetrain The robot drivetrain used to get the shiftGear command
     * @param intake The robot intake used to enable and disable the intake by starting and disabling the motors and moving the arm position
     * @param position The enum that stores the position that the pneumatics/piston/arm is in
     * @param Launcher The robot launcher used to start/enable and stop/disable the launcher motors
     */
    public CopilotController(GamePad gamePad, Intake intake, Position position, Launcher launcher, Climber climber, Magazine magazine, ControlRotator controlRotator){
        m_gamePad = gamePad;
        m_intake = intake;
        m_position = position;
        m_launcher = launcher;
        m_climber = climber;
        m_magazine = magazine;
        m_controlRotator = controlRotator;
    }


    public void controlClimber(int targetPos, double liftSpeed){

        if(m_gamePad.getClimbUp()) {
            m_climber.extendClimber();
        }

        if(m_gamePad.getClimbDown()) {
            m_climber.retractClimber();
        }

        if(m_gamePad.getWinch()) {
            m_climber.liftRobot(targetPos, liftSpeed);
        }
    }

    public void controlMagazine(){
        if(m_gamePad.getMoveMagazine()){
            m_magazine.runBelt(0.37);
        } 
        else if(m_gamePad.getMoveMagazineDown()){
            m_magazine.runBelt(-0.37);
        } 
        else {
            m_magazine.runBelt(0);
        }
    }

    //TODO: Do last
    public void controlLauncher(){
        if(m_gamePad.getRevLauncher()) {

        }
    }

    //TODO: check what to do with launcher
    public void controlMagazineAndLauncher(){
        if(m_gamePad.getLauncherAndMagazine()) {

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

    public void dumpBalls(){
        if(m_gamePad.getDumpAllBalls()){
            m_magazine.runBelt(-0.37);
            m_intake.setInnerIntakeMotor(-0.5);
            m_intake.setOuterIntakeMotor(-0.5);
        } else {
            m_magazine.runBelt(0);
            m_intake.setInnerIntakeMotor(0);
            m_intake.setOuterIntakeMotor(0);
        }
    }

    public void controColorWheel(){
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
}