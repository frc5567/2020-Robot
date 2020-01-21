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
    //variable for how much we should spin the spinner
    public double m_controlPanelTicks;

    //constructer
    public Spinner(SpeedController rotater, Encoder rotateEncoder, char fmsColor, double controlPanelTicks){
        m_rotater = rotater;
        m_encoder = rotateEncoder;
        m_controlPanelTicks = controlPanelTicks;
        m_fmsColor = fmsColor;
    }

    //method for just spinning the wheel on its own
    public void manualSpin(double speed){
        m_rotater.set(speed);
    }

    //method for making the wheel spin about 3 times
    public void autoRotate(double speed){
        if(m_encoder.get() < m_controlPanelTicks){
            m_rotater.set(speed);
        }
        m_encoder.reset();
    }
    
    /*
    //method for spinning to a certain color
    public void autoColor(double speed){
        if(/*Color Sensor Output* != getRecievedColor(){
            m_rotater.set(speed);
        }
    }
    */

    //getter method for recievedColor
    public char getRecievedColor(){
        ColorReader color = new ColorReader();
        m_fmsColor = color.recievedColor;
        return m_fmsColor;
    }












        
}
