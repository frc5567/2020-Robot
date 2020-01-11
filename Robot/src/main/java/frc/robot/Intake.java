package frc.robot;

//import encoder
import edu.wpi.first.wpilibj.Encoder;
// import speed controller/motor
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
// import solenoid
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;




public class Intake {

    //Declare the double Soleniod
    DoubleSolenoid dSolDropBar;
    
    // Instantiate the Double Solenoid
    dSolDropBar = new DoubleSolenoid(1); // the 1 is the port number needed for the double solenoid

    // declare intake motors controllers
    private SpeedController leftIntakeMotor;
    private SpeedController rightIntakeMotor;

    //declare encoder
    private Encoder m_encoder;
    //stores values of the drop bar position
    public enum DropBarPosition {
        kUp(0),
        kDown(1);
        //variable for up/down
        private int armValue;

        
        DropBarPosition(int armValue){
            //this specific location of arm value
            this.armValue = armValue;
        }


    }
    //class that tells the position of the drop bar
    public void setDropBarPosition(DropBarPosition armValue) {
        //switches in between the cases
        switch (armValue){
            //the case where the drop bar is up
            case kUp:
                dSolDropBar.set(Value.kForward);
                break;
            //the case where the drop bar is down    
            case kDown:
                dSolDropBar.set(Value.kReverse);
                break;
        }
    }
    //class that moves the drop bar
    public void moveDropBarPiston(boolean buttonA, boolean buttonB){
        //when button A is pressed, drop bar drops
        if(buttonA = true){
            this.setDropBarPosition(DropBarPosition.kDown);
        } 
        //when button B is pressed, drop bar rises
        if(buttonB = true){
            this.setDropBarPosition(DropBarPosition.kUp);
        }
        
    }




    //declare motor controller group
    SpeedControllerGroup m_intakeMotors;
    //created the speed controller group
    m_intakeMotors = new SpeedControllerGroup(leftIntakeMotor, rightIntakeMotor);

    // class in which the intake motor will be told to move
    public void setIntakeMotor(double intakeMotorSpeed, boolean button){
        
        //turns on the intake motor
        if (button = true){
            m_intakeMotors.set(0.5);
        }
        else{
            //leaves the motor still
            m_intakeMotors.set(0.0);

        }

    }

    
    
/**             //all the things used here
     *              declare DropBarMotor
     *              declare variable dropBarMotorSpeed
     *              declare DropBarButtonDown
     *              declare DropBarButtonUp
     *              declare boolean dropBarButtonDownPressed
     *              declare boolean dropBarButtonUpPressed
     *              
     *              // encoder on DropBarMotor
     *              declare DropBarEncoder
     * 
     * 
     * // resets the encoder value
     * public void encoderReset(){
     * DropBarEncoder.reset
     * }
     *               
     * // general class in which the motor will be told to move
     * public void setDropBarMotor(double DropBarMotorSpeed){
     * 
     *      initiate variable DropBarMotorSpeed = 0
     * 
     *      // buttons on the controller controlling the drop bar
     *      initiate boolean dropBarButtonDownPressed = 0
     *      initiate boolean dropBarButtonUpPressed = 0
     * 
     *      // to move down, conditions in this will have to be met
     *      while DropBarButtonDownPressed = activated {
     * 
     *      // checks bar's position
     *      get() encoder count
     * 
     *            // if the bar is already down, then the bar won't move. else, it will move down when pressed
     *            if encoder value = down encoder value{
     *         
     *            set dropBarMotorSpeed = 0
     * 
     *            } else {
     * 
     *            set dropBarMotorSpeed = (positive or negative depending on how the motor is mounted)
     *         }
     *      } 
     *       // to move up, conditions in this will have to be met
     *       while DropBarButtonUpPressed = activated{
     * 
     *       // checks bar's position
     *       get() encoder count
     * 
     *            // if the bar is already up, then the bar won't move. else, it will move up when pressed
     *            if encoder value = up encoder value (0){
     *         
     *            set dropBarMotor speed = 0
     * 
     *            }else{
     * 
     *      set motorSpeed = (positive or negative depending on how the motor is mounted)
     * 
     *              }
     * 
     *      }
     * } 
    **/

}