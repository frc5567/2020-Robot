package frc.robot;

import edu.wpi.first.wpilibj.Speedcontroller;
import edu.wpi.first.wpilibj.Encoder;

public class ControlRotater{

    //Declare Speedcontroller for Rotater
    private SpeedController m_rotater;
    //Declare Encoder for Rotater
    private Encoder m_encoder;
    //variable for color of wheel to rotate to from FMS
    private ColorReader m_fmsColor;

    public Spinner(SpeedController rotater, Encoder rotateEncoder){
        //instantiates 
        m_rotater = rotater;
        m_encoder = rotateEncoder;
        m_fmsColor = fmsColor;
        // Sets the speed for the rotater motor(temporary number)
        double m_speed = 1;
        // Number of ticks in a single rotation of the control panel(temporary number)
        double m_controlPanelTicks = 0;

    }
    // Function to spin the rotater motor manually
    public void manualSpin(){
        rotater.set(speed);
    }
    // Function to spin the rotater motor until it is equal to the ticks for 3 rotations
    public void autoRotate(){
        if(encoder.ticks < controlPanelTicks){
            rotater.set(speed);
        }
    }
    /*
    // Function to spin the rotater motor until matching the color needed
    public void autoColor(){
        if(Color Sensor Output != (fmsColor.getColor())){
            rotater.set(speed);
        }
    }

    */
    
}