package frc.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first. wpilibj.GenericHID.Hand;
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

    SpeedControllerGroup m_leftSpeedControllers; 
    SpeedControllerGroup m_rightSpeedControllers; 

    DifferentialDrive m_driveTrain;

    XboxController m_driveController;

    Double m_motorPowerLeft;
    Double m_motorPowerRight;

    // Declare
    // ControlMode m_mode = new ControlMode.PercentOutput();

    public Drivetrain(){
        m_frontLeftMotor = new WPI_TalonSRX(1);
        m_frontRightMotor = new WPI_TalonSRX(2);
        m_backLeftMotor = new WPI_VictorSPX(3);
        m_backRightMotor = new WPI_VictorSPX(4);

        m_leftSpeedControllers = new SpeedControllerGroup(m_frontLeftMotor, m_backLeftMotor);
        m_rightSpeedControllers = new SpeedControllerGroup(m_frontRightMotor, m_backRightMotor);
       
        m_motorPowerLeft = - 0.0;
        m_motorPowerRight = 0.0;

        m_driveController = new XboxController(5);
        m_driveTrain = new DifferentialDrive(m_leftSpeedControllers, m_rightSpeedControllers);

       }


       
    // Makes the robot turn forwards
    public void forwardDrive(){
        if (m_driveController.getY(Hand.kLeft) < 0){
            m_motorPowerLeft = m_driveController.getY(Hand.kLeft);
            m_motorPowerRight = - m_driveController.getY(Hand.kLeft);
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight); 
        }

    }
    // Makes the robot turn backwards
    public void backwardDrive(){
      
        if (m_driveController.getY(Hand.kLeft) > 0 ){
            m_motorPowerLeft = m_driveController.getY(Hand.kLeft);
            m_motorPowerRight = - m_driveController.getY(Hand.kLeft);
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight);
        }
    }




    // Makes the Robot Turn Right
    public void rightTurn(){
        if (m_driveController.getX(Hand.kRight) > 0 ){
            m_motorPowerLeft = - m_driveController.getX(Hand.kRight);
            m_motorPowerRight = - m_driveController.getX(Hand.kRight);
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight);
        }
    }
    // Makes the Robot turn Left
    public void leftTurn(){
        if (m_driveController.getX(Hand.kRight) < 0){
            m_motorPowerLeft = m_driveController.getX(Hand.kRight);
            m_motorPowerRight = m_driveController.getX(Hand.kRight);
            m_leftSpeedControllers.set(m_motorPowerLeft);
            m_rightSpeedControllers.set(m_motorPowerRight); 
        }
    }


}