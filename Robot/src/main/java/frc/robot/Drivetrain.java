package frc.robot;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;


public class Drivetrain{

    //Declare variable for speed
    double m_speed = 0.0;
    // Declare left talon
    BaseMotorController m_left  = new TalonSRX(1);
    // Declare right talon
    BaseMotorController m_right = new TalonSRX(2);
    //Declare
    //ControlMode m_mode = new ControlMode.PercentOutput();

    public void Drive(TalonSRX left, TalonSRX right, double speed){
        m_left = left;
        m_right = right;
       m_speed = speed;
       }

    public void forwardDrive(){
        m_left.set(ControlMode.MotionMagic, -m_speed);
        m_right.set(ControlMode.MotionMagic, m_speed);
    }

    public void backwardDrive(){
        m_left.set(ControlMode.MotionMagic, m_speed);
        m_right.set(ControlMode.MotionMagic, -m_speed);
    }


}