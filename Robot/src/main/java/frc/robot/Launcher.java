package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/**
 * A launcher that uses one or multiple motors to launch a projectile
 * <p>Utilizes a scrapped together proportionality controller to adjust speed
 * 
 * @version 1/13/2020
 * @author Josh Overbeek
 */
public class Launcher {
    //proportionality constant - error times this gives you the increase in speed 
    private double p;

    //current set speed of the motor
    private double currentSpeed = 0;

    //difference between current speed and target speed (set point)
    private double error;

    //speed controller used to launch 
    private BaseMotorController motor;

    /**
     * Constructor for Launcher objects
     * 
     * <p> To be used for any system that launches a projectile
     * 
     * @param p The proportionality constant used to control this launcher's speed
     * @param motor The speed controller used to launch the projectile
     */
    public Launcher(double p, BaseMotorController motor) {
        this.p = p;
        this.motor = motor;
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param speed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setMotor(double speed) {
        motor.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Assigns a setpoint that the motor controller will then accelerate to
     * @param setpoint The target speed between -1.0 and 1.0
     */
    public void proportionalSpeedSetter(double setpoint) {
        //calculates error based on the difference between current and target speeds
        error = setpoint - currentSpeed;
        //adjusts the current speed proportionally to the error
        currentSpeed += (error * p);

        //sets the speed of the motors based on the adjusted current speed
        setMotor(currentSpeed);
    }

    /**
     * Sets the proportionality constant
     * @param p The desired value for said constant
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * @return The proportionality constant used to adjust speed
     */
    public double getP() {
        return p;
    }
    
    /**
     * @return the motor used to drive the launcher
     */
    public BaseMotorController getMotor() {
        return motor;
    }

    /**
     * @return the current speed the motor controller is set to
     */
    public double getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * @return the current difference between the current speed and the setpoint
     */
    public double getError() {
        return error;
    }

    /**
     * toString method containing motor, p, current speed and current error
     * 
     * @return the state of the Launcher object summarized in a string
     */
    public String toString() {
        return "Motor: " + motor.toString() + " | P Constant: " + p + " | Current Speed: " + currentSpeed + " | Current Error: " + error;
    }
}