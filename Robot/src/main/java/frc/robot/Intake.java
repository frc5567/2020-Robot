package frc.robot;

//import m_encoder
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
    private DoubleSolenoid m_doubleSolenoidDropBar;
    
    // declare intake motors controllers
    private SpeedController m_leftIntakeMotor;
    private SpeedController m_rightIntakeMotor;

    //declares and Instantiates motor controller group
    private SpeedControllerGroup m_intakeMotors = new SpeedControllerGroup(m_leftIntakeMotor, m_rightIntakeMotor);

    //declare m_encoder
    private SensorCollection m_encoder;

    //declare the number of m_encoder ticks -change number to correct number
    double m_maxEncoderTicks = RobotMap.MAX_ENCODER_TICKS;

    // 
    //declare the number of ticks left untill max m_encoder tick value
    double m_ticksLeft = m_maxEncoderTicks - m_encoder.getQuadraturePosition();
    //declare m_dropBarMotor
    private SpeedController m_dropBarMotor;

    //declare variable speed of the motor for the drop down bar
    double m_dropBarMotorSpeedDown;
    double m_dropBarMotorSpeedUp;
    

    //declare the Xbox Controller
    private XboxController m_testController;

    double i;

    //Constructor for the Intake objects
    public Intake(DoubleSolenoid m_doubleSolenoidDropBar, SpeedControllerGroup m_intakeMotors, SpeedController m_leftIntakeMotor, SpeedController m_rightIntakeMotor, SensorCollection m_encoder, XboxController m_testController){
        
        
        this.m_doubleSolenoidDropBar = m_doubleSolenoidDropBar;
        this.m_intakeMotors = m_intakeMotors;
        this.m_leftIntakeMotor = m_leftIntakeMotor;
        this.m_rightIntakeMotor = m_rightIntakeMotor;
        this.m_encoder = m_encoder;
        this.m_testController = m_testController;

        m_testController = new XboxController(0);
        m_doubleSolenoidDropBar = new DoubleSolenoid(0, 1);

        
        m_dropBarMotorSpeedDown = RobotMap.DROP_BAR_SPEED_DOWN;
        m_dropBarMotorSpeedUp = RobotMap.DROP_BAR_SPEED_UP;
        
    }

    public void kUp(){
        m_doubleSolenoidDropBar.set(DoubleSolenoid.Value.kForward);
    }

    public void kDown(){
        m_doubleSolenoidDropBar.set(DoubleSolenoid.Value.kReverse);
    }


    //class that moves the drop bar
    public void moveDropBarPiston(){
        //when button A is pressed, drop bar drops
        if(m_testController.getAButton()){
           kDown(); 
        } 
        //when button B is pressed, drop bar rises
        if(m_testController.getBButton()){
            kUp();
        }
        
    }



    // class in which the intake motor will be told to move
    public void setIntakeMotor(){
        
        //turns on the intake motor
        if (m_testController.getXButton()){
            m_intakeMotors.set(0.5);
        }
        else{
            //leaves the motor still
            
            m_intakeMotors.set(0.0);

        }

    }
 
    //lowers and raises the drop bar for intake
    public void dropBarButton(){ //the button can change
        if (m_testController.getYButton()){
            if (m_encoder.getQuadraturePosition() < m_maxEncoderTicks){
                //double check for loop
                for (i = m_ticksLeft; i < m_ticksLeft; i++){
                    m_dropBarMotor.set(m_dropBarMotorSpeedDown);
                } 
            } else {
                if(m_encoder.getQuadraturePosition() == m_maxEncoderTicks){
                    // Double check for loop
                    for(i = m_encoder.getQuadraturePosition(); i > 0; i--){
                       m_dropBarMotor.set(m_dropBarMotorSpeedUp); 
                    } 
                }
            }
            
        } else{
            m_dropBarMotor.set(0.0);
        }
    }
    

}