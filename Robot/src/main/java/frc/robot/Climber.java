package frc.robot;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Climber with one or more motors
 * <p>Utilizes a scrapped together proportionality controller to adjust speed
 * 
 */
public class Climber {

  // current speed of the motor
  private double currentSpd = 0;

  // speed controller used to move climber
  private SpeedController motor;

  // encoder for the motor
  private Encoder encoder;

  // encoder target variable
  private int targetPos;

  //difference between current speed and target speed (set point)
  private double error;

  //proportionality constant - error times this gives you the increase in speed 
  private double p;

  /**
   * Constructor for climber objects
   * 
   * @param motorSpeed
   * @param motor
   * @param encoder
   */
  public Climber(SpeedController motor, Encoder encoder) {
    this.motor = motor;
    this.encoder = encoder;
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param motorSpeed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
  public void setSpeed(double motorSpeed) {
    motor.set(motorSpeed);
    }
    
    /**
     * Assigns a setpoint that the motor controller will then accelerate to
     * @param setpoint setpoint The target speed between -1.0 and 1.0
     */
  public void proportionalSpeedSetter(double setpoint) {
    //calculates error based on the difference between current and target speeds
    error = setpoint - currentSpd;
    //adjusts the current speed proportionally to the error
    currentSpd += (error * p);

    //sets the speed of the motors based on the adjusted current speed
    setSpeed(currentSpd);
}
  
    // resets encoder values
  public void encoderReset() {
    encoder.reset();
    }

    /**
     * @return The value given by the encoder
     */
  public int getEncoder() {
      return encoder.get();
  }

    // sets climber to encoder target position
  public void setClimberPos(){
    if (encoder.get() < targetPos) {
        motor.set(.5);

    }

    if (encoder.get() > targetPos){
        motor.set(-.5);

    }

  }

}