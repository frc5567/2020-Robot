package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The magazine for our launcher that keeps track of balls in the system and moves them towards the launcher
 * @author Josh Overbeek
 * @version 2/11/2020
 */
public class Magazine {

    //declare our motor controllers for our belts and the wheel
    private VictorSPX m_motor;

    /**The photoelectric sensors for indexing, which are normally false */
    private DigitalInput m_intakeSensor;
    private DigitalInput m_launchSensor;

    //the number of balls currently in the magazine
    private int m_storedBalls;

    //storage booleans for storing the value of the sensors on the previous cycle
    private boolean m_lastIntakeInput = false;
    private boolean m_lastLaunchInput = false;

    /**
     * Constructor for magazine objects
     * @param motor The motor that drives the magazine
     * @param intakeSensor The sensor mounted near the input to index our balls
     * @param launchSensor The sensor mounted near the launchers to tick our count in the magazine down
     */
    public Magazine(VictorSPX motor, DigitalInput intakeSensor, DigitalInput launchSensor) {
        m_motor = motor;

        m_intakeSensor = intakeSensor;
        m_launchSensor = launchSensor;
    }

    /**
     * Vertical constructor for magazine that uses robot map constants for instantiation
     */
    public Magazine() {
        m_motor = new VictorSPX(RobotMap.MAGAZINE_MOTOR_PORT);

        m_intakeSensor = new DigitalInput(RobotMap.MAGAZINE_IN_SENSOR_PORT);
        m_launchSensor = new DigitalInput(RobotMap.MAGAZINE_OUT_SENSOR_PORT);
    }

    /**
     * Runs the belt at a inputted speed
     * @param speed The percent speed both belts should move at from -1.0 to 1.0
     */
    public void runBelt(double speed) {
        //sets percent output on both belts
        m_motor.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Runs the belt based off of sensor input
     * <p>Every time the intake sensor is tripped, 
     * the magazine runs until the ball is clear of the intake sensor.
     */
    public void sensorBeltControl() {
        if(!m_launchSensor.get()) {
            runBelt(0);
        }
        else if (m_intakeSensor.get()) {
            runBelt(0.71);
        }
        else {
            runBelt(0);
        }
    }

    /**
     * Ticks the number of stored balls up or down based on our sensors
     * <p>This is meant to be run in a periodic method so it can constantly be checking sensors and updating the count
     * <p>This is very likely completely wrong but I'll leave it here for now
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
        //then we have a falling edge on our launch, so we've seen a ball exit the system
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
     * @return the controller for the belt
     */
    public BaseMotorController getMotor() {
        return m_motor;
    }

    /**
     * @return the object summarized as a string
     */
    public String toString() {
        return "Motor: " + m_motor.toString();
    }
}