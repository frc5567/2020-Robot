package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FollowerType;
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

    //speed controllers used to launch 
    //the master controller drives the other motors in closed loop velocity control
    private BaseMotorController m_masterMotor;

    /**The slave on the same side as the master motor controller */
    private BaseMotorController m_closeSlaveMotor;

    /**The slaves on the further side from the master motor, and inverted */
    private BaseMotorController m_farSlaveMotor1, m_farSlaveMotor2;

    /**
     * Constructor for Launcher objects
     * 
     * <p> To be used for any system that launches a projectile
     * 
     * @param adjustmentValue The proportionality constant used to control this launcher's speed
     * @param leftMotor The speed controller used to launch the projectile
     */
    public Launcher(double adjustmentValue, BaseMotorController masterMotor, BaseMotorController closeSlaveMotor, BaseMotorController farSlaveMotor1, BaseMotorController farSlaveMotor2) {
        m_adjustmentValue = adjustmentValue;

        m_masterMotor = masterMotor;
        m_closeSlaveMotor = closeSlaveMotor;

        m_farSlaveMotor1 = farSlaveMotor1;
        m_farSlaveMotor2 = farSlaveMotor2;

        //Sets the far motors to be inverted so that they don't work against the close ones
        m_farSlaveMotor1.setInverted(true);
        m_farSlaveMotor2.setInverted(true);
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param speed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setMotor(double speed) {
        //set the master motor directly
        m_masterMotor.set(ControlMode.PercentOutput, speed);

        //set all other motors to follow
        m_closeSlaveMotor.follow(m_masterMotor, FollowerType.PercentOutput);
        m_farSlaveMotor1.follow(m_masterMotor, FollowerType.PercentOutput);
        m_farSlaveMotor2.follow(m_masterMotor, FollowerType.PercentOutput);
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
     * This revs our launcher to a target velocity as a function of distance
     * <p>Note that this velocity will not be a robot map constant, it will be a result of distance reported by the limelight
     * @param distance Horizontal distance to the target in inches, this should be a product of our limelight
     */
    public void revLauncher(double distance) {
        //calculate what percent of our max distance we are from our target
        double percentMaxDistance = (distance /  RobotMap.MAX_LAUNCHER_DISTANCE_IN);

        //set our target velocity to that same percent of our max speed
        double targetVelocity = (percentMaxDistance * RobotMap.LAUNCHER_FREESPIN_ANGULAR_VELOCITY);

        //set our motor to the target velocity calculated
        m_masterMotor.set(ControlMode.Velocity, targetVelocity);

        //set our slave motors to follow master
        m_closeSlaveMotor.follow(m_masterMotor);
        m_farSlaveMotor1.follow(m_masterMotor);
        m_farSlaveMotor2.follow(m_masterMotor);
    }

    /**
     * This should only be run once at the start of the match or in robot init
     */
    public void configVelocityControl() {
        //config remote sensors
        
        //set p, i, d, f values
        //the zero is the PID slot, in this case it is zero for the primary PID
        //the launcher has no auxillary or turning PID control
        m_masterMotor.config_kP(0, RobotMap.LAUNCHER_P);
        m_masterMotor.config_kI(0, RobotMap.LAUNCHER_I);
        m_masterMotor.config_kD(0, RobotMap.LAUNCHER_D);
        m_masterMotor.config_kF(0, RobotMap.LAUNCHER_F);

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
     * Returns the master motor
     * 
     * @return the motor used to drive the launcher
     */
    public BaseMotorController getMasterMotor() {
        return m_masterMotor;
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
        return "Motor: " + m_masterMotor.toString() + " | Adjustment Value: " + m_adjustmentValue + " | Current Speed: " + m_currentSpeed + " | Current Error: " + m_error;
    }
}