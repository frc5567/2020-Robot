package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

/**
 * A launcher that uses one or multiple motors to launch a projectile
 * <p>Utilizes a scrapped together proportionality controller to adjust speed for percent input,
 * and uses velocity PID control for velocity control
 * 
 * @version 2/17/2020
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
    private TalonSRX m_masterMotor;

    /**The slave on the same side as the master motor controller */
    private BaseMotorController m_closeSlaveMotor;

    /**The slaves on the further side from the master motor, and inverted */
    private BaseMotorController m_farSlaveMotor1, m_farSlaveMotor2;

    //the encoder plugged into the master Talon
    SensorCollection m_encoder;

    /**
     * Constructor for Launcher objects
     * 
     * <p> A four motor launcher with one master connected to an encoder and three slaves
     * 
     * @param adjustmentValue The proportionality constant used to control this launcher's speed
     * @param masterMotor The master speed controller used to launch the projectile
     * @param closeSlaveMotor The slave motor controller on the same side of the shooter as the master
     * @param farSlaveMotor1 One of the slave motors on the far side of the launcher (from the master)
     * @param farSlaveMotor2 The other slave motor on the far side of the launcher
     */
    public Launcher(double adjustmentValue, TalonSRX masterMotor, BaseMotorController closeSlaveMotor, BaseMotorController farSlaveMotor1, BaseMotorController farSlaveMotor2) {
        m_adjustmentValue = adjustmentValue;

        m_masterMotor = masterMotor;
        m_closeSlaveMotor = closeSlaveMotor;

        m_farSlaveMotor1 = farSlaveMotor1;
        m_farSlaveMotor2 = farSlaveMotor2;

        //Sets the far motors to be inverted so that they don't work against the close ones
        m_farSlaveMotor1.setInverted(RobotMap.LAUNCHER_FAR_SLAVE1_INVERTED);
        m_farSlaveMotor2.setInverted(RobotMap.LAUNCHER_FAR_SLAVE2_INVERTED);

        //Instantiates the encoder as the encoder plugged into the master
        m_encoder = new SensorCollection(m_masterMotor);

        //run the config methods to set up velocity control
        configVelocityControl();
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
     * Sets the velocity of the motors in revs/100ms
     * @param velocity target velocity in rev/100ms
     */
    public void setVelocity(double velocity) {
        //set the velocity of the motors
        m_masterMotor.set(ControlMode.Velocity, velocity);

        //set our slave motors to follow master
        m_closeSlaveMotor.follow(m_masterMotor);
        m_farSlaveMotor1.follow(m_masterMotor);
        m_farSlaveMotor2.follow(m_masterMotor);
    }

    /**
     * This revs our launcher to a target velocity as a function of distance
     * <p>This equation needs to be tuned based on testing
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
     * <p> This must be run before using any velocity control
     */
    public void configVelocityControl() {
        //config remote sensors
        //sets the sensor to be a quad encoder, sets our feedback device to be that sensor
        m_masterMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

        //sets whether our motor is inverted
        //this is currently false but can be switched based on testing
        m_masterMotor.setInverted(false);
        m_masterMotor.setSensorPhase(false);

        //this sets how often we pull data from our sensor
        //as it is currently set, the motor controller will read from the encoder every 10 miliseconds
        m_masterMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, RobotMap.LAUNCHER_FEEDBACK_PERIOD_MS, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //this configs the deadband for the PID output. Any output with an absolute value less than this will be treated as zero
        //curently, this is the factory default (0.04), but should be tuned on testing
        m_masterMotor.configNeutralDeadband(RobotMap.LAUNCHER_NEUTRAL_DEADBAND, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //this sets the peak output for our PID. The PID cannot output a value higher than this
        //this is currently set to 1.0, so the PID can output as much as it wants
        m_masterMotor.configPeakOutputForward(RobotMap.LAUNCHER_PID_PEAK_OUTPUT, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);
        //this does the same thing but for the reverse direction
        m_masterMotor.configPeakOutputReverse(-RobotMap.LAUNCHER_PID_PEAK_OUTPUT, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);
        
        //sets the period of the velocity sample
        //effectively this defines the amount of time used to calculate the velocity
        //this selection is currrently arbitrary
        m_masterMotor.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //Sets the number of samples used in the rolling average for calculating velocity
        m_masterMotor.configVelocityMeasurementWindow(RobotMap.LAUNCHER_VELOCITY_MEASUREMENT_WINDOW, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);
        
        //set p, i, d, f values
        //the zero is the PID slot, in this case it is zero for the primary PID
        //the launcher has no auxillary or turning PID control
        m_masterMotor.config_kP(0, RobotMap.LAUNCHER_P);
        m_masterMotor.config_kI(0, RobotMap.LAUNCHER_I);
        m_masterMotor.config_kD(0, RobotMap.LAUNCHER_D);
        m_masterMotor.config_kF(0, RobotMap.LAUNCHER_F);

        //this sets the acceptable amount of Integral error, where if the absolute accumulated error exceeds this ammount, it resets to zero
        //this is designed to prevent the PID from going crazy if we move too far from our target
        m_masterMotor.config_IntegralZone(0, RobotMap.LAUNCHER_I_ZONE, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //sets the max output of the motor specifically within closed loop control
        //this is likely redundant, but the values can be set to seperate if needed in testing
        m_masterMotor.configClosedLoopPeakOutput(0, RobotMap.LAUNCHER_PID_PEAK_OUTPUT, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //this configures an allowable error in closed loop control
        //any error less than this is treated as zero. We currently set this to zero, but we can increase it if need be
        m_masterMotor.configAllowableClosedloopError(0, RobotMap.LAUNCHER_ACCEPTABLE_ERROR, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //configures the period for closed loop calculations in MS 
        //Currently set to 10, but should be increased if the can bus is haveing issues
        m_masterMotor.configClosedLoopPeriod(0, RobotMap.LAUNCHER_CLOSED_LOOP_PERIOD_MS, RobotMap.LAUNCHER_CONFIG_TIMEOUT_MS);

        //sets our closed loop control to use our primary PID slot
        m_masterMotor.selectProfileSlot(0, 0);
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
     * @return The velocity of the encoder in units per 100ms
     */
    public int getEncoderVelocity() {
        return m_encoder.getQuadratureVelocity();
    }

    /**
     * @return the position of the encoder in encoder units
     */
    public int getEncoderPosition() {
        return m_encoder.getQuadraturePosition();
    }

    /**
     * @return the current difference between the current speed and the setpoint
     */
    public double getError() {
        return m_error;
    }

    /**
     * toString method containing motor, encoder, adjustmentValue, current speed and current error
     * 
     * @return the state of the Launcher object summarized in a string
     */
    public String toString() {
        return "Motor: " + m_masterMotor.toString() + " | Encoder: "+ m_encoder + " | Adjustment Value: " + m_adjustmentValue + " | Current Speed: " + m_currentSpeed + " | Current Error: " + m_error;
    }
}