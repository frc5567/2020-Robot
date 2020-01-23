package frc.robot;

// imports Motor Controllers, Controller group functions, Basic differenctial drive code, solenoid functions, and functions for getting the joystick values
import edu.wpi.first.wpilibj.SpeedControllerGroup;
//import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first. wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;



public class Drivetrain{

    // Declare variable for speed
    double m_speed = 0.0;
    // Declare front left talon
    WPI_TalonSRX m_frontRightMotor;
    // Declare back left talon
    WPI_TalonSRX m_frontLeftMotor;
    // Declare front right talon
    WPI_VictorSPX m_backLeftMotor;
    // Declare back right talon
    WPI_VictorSPX m_backRightMotor;

    //Declares speed controller objects (treats front left + back left as one)
    SpeedControllerGroup m_leftSpeedControllers; 
    SpeedControllerGroup m_rightSpeedControllers; 

    //Declares drivetrain object
    //DifferentialDrive m_driveTrain;

    //Declares Xbox controller object
    XboxController m_driveController;

    //Declares the solenoid as an object
    DoubleSolenoid m_twoSpeedSolenoid;

    //Declares object to represent motor power for the speed controller groups 
    double m_motorPowerLeft;
    double m_motorPowerRight;

    //Declares a value for the deadbands
    public static final double CONTROLLER_STICK_DEADBAND = 0.1; // double check to make sure it is supposed to be public

    //Declares a boolean for determining the position of the double solenoid. True = Forward  False = Reverse
    boolean SOLENOID_POSITION;


    public Drivetrain(){

        //Instatiates the motor controllers
        m_frontLeftMotor = new WPI_TalonSRX(1);
        m_frontRightMotor = new WPI_TalonSRX(2);
        m_backLeftMotor = new WPI_VictorSPX(3);
        m_backRightMotor = new WPI_VictorSPX(4);

        //Instantiates the speed controller groups as an object
        m_leftSpeedControllers = new SpeedControllerGroup(m_frontLeftMotor, m_backLeftMotor);
        m_rightSpeedControllers = new SpeedControllerGroup(m_frontRightMotor, m_backRightMotor);
       
        //Sets the base motor power (i.e when the robto starts)
        m_motorPowerLeft = 0.0;
        m_motorPowerRight = 0.0;

        // instantiates/ creates the Xbox controller as an object used
        m_driveController = new XboxController(5);

        //instantiates the double solenoid
        m_twoSpeedSolenoid = new DoubleSolenoid(0, 1); //<-We'll need to check the channels to make sure they're right.

        //m_driveTrain = new DifferentialDrive(m_leftSpeedControllers, m_rightSpeedControllers);
    }


       //Creates a function for setting the solenoid 1 (aka forward)
       public void solenoidForward(){
           m_twoSpeedSolenoid.set(DoubleSolenoid.Value.kForward);
           SOLENOID_POSITION = true;
       }
       //Creates a function for setting the solenoid into mode 2 (aka reverse)
       public void solenoidReverse(){
            m_twoSpeedSolenoid.set(DoubleSolenoid.Value.kReverse);
            SOLENOID_POSITION = false;
       }

       // Function for switching between the two solenoid positions
       public void switchSolenoidGear(){
           if(m_driveController.getXButton()){
               if(SOLENOID_POSITION == true){
                    solenoidReverse();
               } else {
                    solenoidForward();
               }
           }
       }

       // Function that gets the y value for the left joystick on the controller with the use of a deadband
       public double getY(){
           //Declares the double for the stickYValue
           double stickYValue;

           // If the position of the joystick is within the deadband the value is set to 0. If not it is set to the value of the joystick's position
           if ((m_driveController.getY(Hand.kLeft) < CONTROLLER_STICK_DEADBAND) && (m_driveController.getY(Hand.kLeft) > - CONTROLLER_STICK_DEADBAND)){
               stickYValue = 0.00;
           } else {
               stickYValue = m_driveController.getY(Hand.kLeft);
           }

           // Returns the Y stick value
           return stickYValue;
       }


       // Function to get the x value for the right joystick on the controller with the use of a deadband
       public double getX(){
           //Declares the double for the stickXValue
           double stickXValue;

        // If the position of the joystick is within the deadband the value is set to 0. If not it is set to the value of the joystick's position
           if((m_driveController.getX(Hand.kRight) < CONTROLLER_STICK_DEADBAND) && (m_driveController.getX(Hand.kRight) > - CONTROLLER_STICK_DEADBAND)){
               stickXValue = 0.00;
           } else {
               stickXValue = m_driveController.getX(Hand.kRight);
           }

           //Returns the X stick value
           return stickXValue;
       }
       


    // Makes the robot move forward and backward by setting both motors to the joystick's Y value
    public void linearDrive(){
            m_motorPowerLeft = getY();
            m_motorPowerRight = - getY();
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight); 
    }


    // Makes the Robot Turn Right by setting both motors to the joystick's X value.
    public void rightTurn(){
        if (m_driveController.getX(Hand.kRight) > CONTROLLER_STICK_DEADBAND){
            m_motorPowerLeft = - getX();
            m_motorPowerRight = - getX();
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight);
        } else {
            m_leftSpeedControllers.stopMotor(); //Might not need, see through testing
            m_rightSpeedControllers.stopMotor(); //Might not need, see through testing
        }
    }
    // Makes the Robot turn Left y setting both to the joystick's x value
    public void leftTurn(){
        if (m_driveController.getX(Hand.kRight) < CONTROLLER_STICK_DEADBAND){
            m_motorPowerLeft = getX();
            m_motorPowerRight = getX();
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight); 
        } else {
            m_leftSpeedControllers.stopMotor(); //Might not need, see through testing
            m_rightSpeedControllers.stopMotor(); //Might not need, see through testing
        }
    }
}