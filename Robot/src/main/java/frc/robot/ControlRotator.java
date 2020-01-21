package frc.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Encoder;


public class ControlRotator{

    /*
    1 speedcontroller
    1 encoder
    variable for the value of ticks and ratio of distance to ticks
    when called spin until encoder ticks are over __
    */

    //Declares Speedcontroller
    public SpeedController m_rotater;
    //Declares Encoder
    public Encoder m_encoder;
    //variable for color of wheel to rotate to
    public char m_fmsColor;
    //variable for number of ticks per 1 revolution of the big wheel. The value that it s currently set to is made up and needs to be changed
    public double TICKS_PER_REVOLUTION = 50;

    //constructer
    public ControlRotator(SpeedController rotater, Encoder rotateEncoder, char fmsColor){
        m_rotater = rotater;
        m_encoder = rotateEncoder;
        m_fmsColor = fmsColor;
    }

    //method for just spinning the wheel on its own
    public void manualSpin(double speed){
        m_rotater.set(speed);
    }

    //method for making the wheel spin about 3 times
    public boolean autoRotate(double speed, boolean doneSpinning){
        if(m_encoder.get() < 3 * TICKS_PER_REVOLUTION){
            m_rotater.set(speed);
            doneSpinning = false;
            return doneSpinning;
        }
        else{
            m_encoder.reset();
            doneSpinning = true;
            return doneSpinning;
        }
    }
    
    /*
    //method for spinning to a certain color
    public void autoColor(double speed){
        if(/*Color Sensor Output* != getRecievedColor(){
            m_rotater.set(speed);
        }
    }
    */

    //method for getting the color recieved from the fms
    public char getRecievedColor(){
        ColorReader color = new ColorReader();
        m_fmsColor = color.getColor();
        return m_fmsColor;
    }      
}
