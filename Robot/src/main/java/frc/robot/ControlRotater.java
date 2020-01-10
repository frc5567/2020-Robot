package frc.robot;

import edu.wpi.first.wpilibj.Speedcontroller;
import edu.wpi.first.wpilibj.Encoder;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


public class ControlRotater{

    /*
    1 speedcontroller
    1 encoder
    variable for the value of ticks and ratio of distance to ticks
    when called spin until encoder ticks are over __

    */

    //Declare Speedcontroller
    private SpeedController m_rotater;
    //Declare Encoder
    private Encoder m_encoder;
    //variable for color of wheel to rotate to
    private ColorReader m_fmsColor;




    public Spinner(SpeedController rotater, Encoder rotateEncoder){
        //instantiates 
        m_rotater = rotater;
        m_encoder = rotateEncoder;
        double m_speed = 1;
        double m_controlPanelTicks = 0;
        m_fmsColor = fmsColor;


    }
    public void manualSpin(){
        rotater.set(speed);
    }

    public void autoRotate(){
        if(encoder.ticks < controlPanelTicks){
            rotater.set(speed);
        }
    }
    /*
    public void autoColor(){
        if(/*Color Sensor Output* != (fmsColor.getColor())){
            rotater.set(speed);
        }
    }

    */















        
}