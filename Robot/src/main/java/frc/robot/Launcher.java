package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/**
 * A launcher that uses one or multiple motors to launch a projectile
 * <p>Utilizes a scrapped together proportionality controller to adjust speed
 * 
 * @version 1/25/2020
 * @author Josh Overbeek
 */
public class Launcher {
    //error times this gives you the increase in speed 
    private double m_adjustmentValue;

    //current set speed of the motor
    private double m_currentSpeed = 0;

    //difference between current speed and target speed (set point)
    private double m_error;

    //speed controller used to launch 
    private BaseMotorController m_leftMotor;
    private BaseMotorController m_rightMotor;

    /**
     * Constructor for Launcher objects
     * 
     * <p> To be used for any system that launches a projectile
     * 
     * @param adjustmentValue The proportionality constant used to control this launcher's speed
     * @param leftMotor The speed controller used to launch the projectile
     */
    public Launcher(double adjustmentValue, BaseMotorController leftMotor, BaseMotorController rightMotor) {
        m_adjustmentValue = adjustmentValue;
        m_leftMotor = leftMotor;
        m_rightMotor = rightMotor;
        m_rightMotor.setInverted(true);
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param speed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setMotor(double speed) {
        m_leftMotor.set(ControlMode.PercentOutput, speed);
        m_rightMotor.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Assigns a setpoint that the motor controller will then accelerate to
     * @param setpoint The target speed between -1.0 and 1.0
     */
    public void proportionalSpeedSetter(double setpoint) {
        //calculates error based on the difference between current and target speeds
        m_error = setpoint - m_currentSpeed;
        //adjusts the current speed proportionally to the error
        m_currentSpeed += (m_error * m_adjustmentValue);

        //sets the speed of the motors based on the adjusted current speed
        setMotor(m_currentSpeed);
    }

    /**
     * This revs our launcher to our preset velocity
     * <p>Note that this velocity will not be a robot map constant, it will be a result of distance reported by the limelight
     */
    public void revLauncher() {
        m_leftMotor.set(ControlMode.Velocity, RobotMap.LAUNCHER_SPEED);
        m_rightMotor.set(ControlMode.Velocity, RobotMap.LAUNCHER_SPEED);
    }

    /**
     * This should only be run once at the start of the match or in robot init
     */
    public void configVelocityControl() {
        //config remote sensors
        
        //set p, i, d, f values
        //the zero is the PID slot, in this case it is zero for the primary PID
        //the launcher has no auxillary or turning PID control
        m_leftMotor.config_kP(0, RobotMap.LAUNCHER_P);
        m_leftMotor.config_kI(0, RobotMap.LAUNCHER_I);
        m_leftMotor.config_kD(0, RobotMap.LAUNCHER_D);
        m_leftMotor.config_kF(0, RobotMap.LAUNCHER_F);
        
        m_rightMotor.config_kP(0, RobotMap.LAUNCHER_P);
        m_rightMotor.config_kI(0, RobotMap.LAUNCHER_I);
        m_rightMotor.config_kD(0, RobotMap.LAUNCHER_D);
        m_rightMotor.config_kF(0, RobotMap.LAUNCHER_F);

        //set standard setpoint -> Should be done in robot map?

        //config max integral accum and i zone -> should be done

        //config peak output and allowable error
    }

    /**
     * Sets the proportionality constant
     * @param adjustmentValue The desired value for said constant
     */
    public void setAdjustmentValue(double adjustmentValue) {
        m_adjustmentValue = adjustmentValue;
    }

    /**
     * @return The proportionality constant used to adjust speed
     */
    public double getAdjustmentValue() {
        return m_adjustmentValue;
    }
    
    /**
     * Returns a motor based on a string passed in
     * <p>Will default to left motor
     * @param motorName The name of the motor to get. Pass in "right" to get the right motor,
     * otherwise it will return the left motor
     * @return the motor used to drive the launcher
     */
    public BaseMotorController getMotor(String motorName) {
        if (motorName == "right") {
            return m_rightMotor;
        }
        else {
            return m_leftMotor;
        }
    }

    /**
     * @return the current speed the motor controller is set to
     */
    public double getCurrentSpeed() {
        return m_currentSpeed;
    }

    /**
     * @return the current difference between the current speed and the setpoint
     */
    public double getError() {
        return m_error;
    }

    /**
     * toString method containing motor, adjustmentValue, current speed and current error
     * 
     * @return the state of the Launcher object summarized in a string
     */
    public String toString() {
        return "Motor: " + m_leftMotor.toString() + " " + m_rightMotor.toString() + " | Adjustment Value: " + m_adjustmentValue + " | Current Speed: " + m_currentSpeed + " | Current Error: " + m_error;
    }
}