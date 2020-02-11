package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The magazine for our launcher that keeps track of balls in the system and moves them towards the launcher
 * @author Josh Overbeek
 * @version 2/11/2020
 */
public class Magazine {

    //declare our motor controllers for our belts and the wheel
    private BaseMotorController m_bottomBelt;
    private BaseMotorController m_topBelt;
    private BaseMotorController m_popUpWheel; //TODO: Think of a better name

    //declare digital input for the photoelectric sensors
    private DigitalInput m_intakeSensor;
    private DigitalInput m_launchSensor;

    //the number of balls currently in the magazine
    private int m_storedBalls;

    //storage booleans for storing the value of the sensors on the previous cycle
    private boolean m_lastIntakeInput = false;
    private boolean m_lastLaunchInput = false;

    /**
     * Constructor for magazine objects
     * @param bottomBelt The motor controller that controls the bottom belt
     * @param topBelt The motor controller that controls the top belt
     * @param popUpWheel The motor controller that drives the wheel which pops the cells into the launcher
     * @param intakeSensor The sensor mounted near the input to index our balls
     * @param launchSensor The sensor mounted near the launchers to tick our count in the magazine down
     */
    public Magazine(BaseMotorController bottomBelt, BaseMotorController topBelt, BaseMotorController popUpWheel, DigitalInput intakeSensor, DigitalInput launchSensor) {
        m_bottomBelt = bottomBelt;
        m_topBelt = topBelt;
        m_popUpWheel = popUpWheel;

        m_intakeSensor = intakeSensor;
        m_launchSensor = launchSensor;
    }

    /**
     * Runs both top and bottom belts at a fixed speed, said speed should be tested then defined in robot map
     * <p>This method should be called whenever we intake a ball to move all units up and when we are shooting
     * 
     * @param speed The percent speed both belts should move at from -1.0 to 1.0
     */
    public void runBelts(double speed) {
        //sets percent output on both belts
        m_bottomBelt.set(ControlMode.PercentOutput, speed);
        m_topBelt.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Spins the top wheel to pop a ball into the shooter
     */
    public void spinPopUpWheel(double speed) {
        m_popUpWheel.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Ticks the number of stored balls up or down based on our sensors
     * <p>This is meant to be run in a periodic method so it can constantly be checking sensors and updating the count
     */
    public void manageStoredBalls() {
        boolean intakeInput = m_intakeSensor.get();
        boolean launchInput = m_launchSensor.get();

        //if we don't see the same value that we had last cycle and our new input is true
        //then we have a rising edge on our intake, so we've seen a ball enter the system
        if ( (intakeInput != m_lastIntakeInput) && (intakeInput == true)) {
            m_storedBalls++;
        }

        //if we don't see the same value that we had last cycle and our new input is false
        //then we have a falling edge on our laucnh, so we've seen a ball exit the system
        if ( (launchInput != m_lastLaunchInput) && (launchInput == false)) {
            m_storedBalls--;
        }

        //update our stored values to match our current cycle
        m_lastIntakeInput = intakeInput;
        m_lastLaunchInput = launchInput;
    }

    /**
     * @return the sensor mounted near the intake
     */
    public DigitalInput getIntakeSensor() {
        return m_intakeSensor;
    }

    /**
     * @return the sensor mounted near the launcher
     */
    public DigitalInput getLaunchSensor() {
        return m_launchSensor;
    }

    /**
     * @return the number of balls currently in the magazine
     */
    public int getStoredBalls() {
        return m_storedBalls;
    }

    /**
     * Manually sets the number of balls in the magazine
     * @param storedBalls the desired number of stored balls
     */
    public void setStoredBalls(int storedBalls) {
        m_storedBalls = storedBalls;
    }

    /**
     * @return the controller for the top belt
     */
    public BaseMotorController getTopBeltController() {
        return m_topBelt;
    }

    /**
     * @return the controller for the bottom belt
     */
    public BaseMotorController getBottomBeltController() {
        return m_bottomBelt;
    }

    /**
     * @return the controller for the pop-up wheel
     */
    public BaseMotorController getPopUpWheelController() {
        return m_popUpWheel;
    }

    /**
     * @return the object summarized as a string
     */
    public String toString() {
        return "Belts (top | bottom): " + m_topBelt.toString() + " | " + m_bottomBelt.toString() + " | Pop-Up Wheel: " + m_popUpWheel.toString();
    }
}