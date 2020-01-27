package frc.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Small wheel used to spin the control panel
 * 
 * @version 1/27/2020
 * @author Caleb Overbeek
 */
public class ControlRotator{

    //Declares Speedcontroller
    private SpeedController m_rotator;
    //Declares Encoder
    private Encoder m_encoder;
    //Declares a variable for number of ticks per 1 revolution of the big wheel. The value that it s currently set to is made up and needs to be changed
    public double TICKS_PER_REVOLUTION = 50;
    //difference between current speed and target speed (set point)
    private double m_error;
    //current set speed of the motor
    private double m_currentSpeed = 0;
    //error times this gives you the increase in speed 
    private double m_adjustmentValue;

    //Constructer; make sure to pass in paramaters when created in the Robot class
    public ControlRotator(SpeedController rotator, Encoder rotatorEncoder){
        m_rotator = rotator;
        m_encoder = rotatorEncoder;
    }

    //method for just spinning the wheel manually
    public void manualRotate(double speed){
        m_rotator.set(speed);
    }

    //method for making the wheel spin about 3 times
    public boolean autoRotate(double speed){
        //variable to say if the autorotator is done or not yet
        boolean doneSpinning = false;
        if(m_encoder.get() < (3 * TICKS_PER_REVOLUTION)){
            m_rotator.set(speed);
        }
        else{
            m_rotator.set(0);
            m_encoder.reset();
            doneSpinning = true;
        }
        return doneSpinning;
    }
    
    /*
    //method for spinning to a certain color
    public void autoColor(double speed){
        if(/*Color Sensor Output* != getRecievedColor(){
            m_rotator.set(speed);
        }
    }
    */

    //method for getting the color recieved from the fms
    public char getRecievedColor(){
        ColorReader color = new ColorReader();
        return color.getColor();
    }

    //method to make the spinner speed up more and more over time
    public void proportionalSpeedSetter(double setpoint) {
        //calculates error based on the difference between current and target speeds
        m_error = setpoint - m_currentSpeed;
        //adjusts the current speed proportionally to the error
        m_currentSpeed += (m_error * m_adjustmentValue);

        //sets the speed of the motors based on the adjusted current speed
        manualRotate(m_currentSpeed);
    }
}
