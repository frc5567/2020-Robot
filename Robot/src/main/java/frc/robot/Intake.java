package frc.robot;

//import encoder
import com.ctre.phoenix.motorcontrol.SensorCollection;
// import the Xboc controller
import edu.wpi.first.wpilibj.XboxController;
// import speed controller/motor
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
// import solenoid
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.DoubleSolenoid.Value;   -not currently used but might need



public class Intake {
    //Declares and Instantiates the double Soleniod. The 0 and 1 are the forward and reverse channels.
    private DoubleSolenoid dSolDropBar = new DoubleSolenoid(0, 1);
    
    // declare intake motors controllers
    private SpeedController leftIntakeMotor;
    private SpeedController rightIntakeMotor;

    //declares and Instantiates motor controller group
    private SpeedControllerGroup intakeMotors= new SpeedControllerGroup(leftIntakeMotor, rightIntakeMotor);

    //declare encoder
    private SensorCollection encoder;

    //declare the number of encoder ticks -change number to correct number
    double maxEncoderTick = 22;

    //declare the number of ticks left untill max Encoder tick value
    double ticksLeft = maxEncoderTick - encoder.get();

    //declare DropBarMotor
    private SpeedController dropBarMotor;
    
    //declare variable dropBarMotorSpeed
    double dropBarMotorSpeedDown = 0.5;
    double dropBarMotorSpeedUp = -0.5;

    //declare the Xbox Controller
    private XboxController testController = new XboxController(0);

    double i;

    //Constructor for the Intake objects
    public Intake(DoubleSolenoid dSolDropBar, SpeedControllerGroup intakeMotors, SpeedController leftIntakeMotor, SpeedController rightIntakeMotor, SensorCollection encoder, XboxController testController){
        this.dSolDropBar = dSolDropBar;
        this.intakeMotors = intakeMotors;
        this.leftIntakeMotor = leftIntakeMotor;
        this.rightIntakeMotor = rightIntakeMotor;
        this.encoder = encoder;
        this.testController = testController;

    }

    public void kUp(){
        dSolDropBar.set(DoubleSolenoid.Value.kForward);
    }

    public void kDown(){
        dSolDropBar.set(DoubleSolenoid.Value.kReverse);
    }


    //class that moves the drop bar
    public void moveDropBarPiston(){
        //when button A is pressed, drop bar drops
        if(testController.getAButton()){
           kDown(); 
        } 
        //when button B is pressed, drop bar rises
        if(testController.getBButton()){
            kUp();
        }
        
    }



    // class in which the intake motor will be told to move
    public void setIntakeMotor(double intakeMotorSpeed){
        
        //turns on the intake motor
        if (testController.getXButton()){
            intakeMotors.set(0.5);
        }
        else{
            //leaves the motor still
            
            intakeMotors.set(0.0);

        }

    }
 
    //lowers and raises the drop bar for intake
    public void dropBarButton(){ //the button can change
        if (testController.getYButton()){
            if (encoder.get() < maxEncoderTick){
                //double check for loop
                for (i = ticksLeft; i < ticksLeft; i++){
                    dropBarMotor.set(dropBarMotorSpeedDown);
                } 
            } else {
                if(encoder.get() = maxEncoderTick){
                    // Double check for loop
                    for(i = encoder.get(); i > 0; i--){
                       dropBarMotor.set(dropBarMotorSpeedUp); 
                    } 
                }
            }
            
        } else{
            dropBarMotor.set(0.0);
        }
    }
    
/**             //all the things used here
     *              
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