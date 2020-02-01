package frc.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Climber with one or more motors
 * <p>Utilizes a scrapped together proportionality controller to adjust speed
 * 
 * @version 1/25/2020
 * @author Owen Morrow
 */
public class Climber {

    // current speed of the motor
    private double m_currentSpeed = 0;
  
    // speed controller used to move climber
    private SpeedController m_motor;
  
    // encoder for the motor
    private Encoder m_encoder;
  
    //difference between current speed and target speed (set point)
    private double m_error;
  
    //error times this gives you the increase in speed 
    private double m_adjustmentValue;

    /**
     * Constructor for climber objects
     * 
     * @param motor The motor that runs the climber
     * @param encoder The encoder for our climber
     * @param adjustmentValue The value we use to adjust speed
     */
    public Climber(SpeedController motor, Encoder encoder, double adjustmentValue) {
        m_motor = motor;
        m_encoder = encoder;
        m_adjustmentValue = adjustmentValue;
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param motorSpeed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setSpeed(double motorSpeed) {
        m_motor.set(motorSpeed);
    }
    
    /**
     * Assigns a setpoint that the motor controller will then accelerate to
     * @param setpoint setpoint The target speed between -1.0 and 1.0
     */
    public void proportionalSpeedSetter(double setpoint) {
        //calculates error based on the difference between current and target speeds
        m_error = setpoint - m_currentSpeed;
        //adjusts the current speed proportionally to the error
        m_currentSpeed += (m_error * m_adjustmentValue);

        //sets the speed of the motors based on the adjusted current speed
        setSpeed(m_currentSpeed);
    }
  
    // resets encoder values
    public void encoderReset() {
        m_encoder.reset();
    }

    /**
     * @return The value given by the encoder
     */
    public int getEncoder() {
      return m_encoder.get();
    }

    // sets climber to encoder target position
    public void setClimberPos(int targetPos){
        if (m_encoder.get() < targetPos) {
            m_motor.set(RobotMap.CLIMBER_SPEED);

        }
        else if (m_encoder.get() > targetPos){
            m_motor.set(-RobotMap.CLIMBER_SPEED);

        }
        else {
            m_motor.set(0);
        }

    } 

}