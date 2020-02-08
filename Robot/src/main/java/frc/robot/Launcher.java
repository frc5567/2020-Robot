package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/**
 * A launcher that uses one or multiple motors to launch a projectile
 * <p>Utilizes a scrapped together proportionality controller to adjust speed
 * 
 * @version 1/7/2020
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
    private BaseMotorController leftMotor;
    private BaseMotorController rightMotor;
    private BaseMotorController leftSlaveMotor;
    private BaseMotorController rightSlaveMotor;

    /**
     * Constructor for Launcher objects
     * 
     * <p> To be used for any system that launches a projectile
     * 
     * @param p The proportionality constant used to control this launcher's speed
     * @param leftMotor The speed controller used to launch the projectile
     */
    public Launcher(double p, BaseMotorController leftMotor, BaseMotorController rightMotor, BaseMotorController leftSlaveMotor, BaseMotorController rightSlaveMotor) {
        this.p = p;
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.leftSlaveMotor = leftSlaveMotor;
        this.rightSlaveMotor = rightSlaveMotor;
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param speed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setMotor(double speed) {
        leftMotor.set(ControlMode.PercentOutput, speed);
        rightMotor.set(ControlMode.PercentOutput, -speed);
        leftSlaveMotor.follow(leftMotor);
        rightSlaveMotor.follow(rightMotor);
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
     * toString method containing motor, p, current speed and current error
     * 
     * @return the state of the Launcher object summarized in a string
     */
    public String toString() {
        return "Motor: " + leftMotor.toString() + " " + rightMotor.toString() + " | P Constant: " + p + " | Current Speed: " + currentSpeed + " | Current Error: " + error;
    }
}