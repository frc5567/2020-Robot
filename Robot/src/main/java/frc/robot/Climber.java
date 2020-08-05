package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PWMSparkMax;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * Climber with two motors
 * <p>Currently is written expecting one talon and one Spark, where the talon is on extension 
 * <p>This climber uses one motor on a winch to extend the climber and one motor on a winch to retract a hook
 * 
 * @version 2/18/2020
 * @author Owen Morrow
 */
public class Climber {

    // current speed of the lift motor
    private double m_liftCurrentSpeed = 0;
  
    // speed controllers used to extend climber and lift robot
    private TalonSRX m_extensionMotor;
    private SpeedController m_liftMotor;

    // encoders for the extension and lift motors
    private SensorCollection m_extensionEncoder;
  
    //difference between current speed and target speed (set point)
    private double m_error;
  
    //error times this gives you the increase in speed 
    private double m_adjustmentValue;

    //the starting position of the encoder and the value the climber will attempt to return to
    private int m_startingPosition;

    /**
     * Constructor for climber objects
     * <p>The encoders are attached to the talon motor controllers that drive the climber
     * 
     * @param extensionMotor The motor that extends the climber
     * @param liftMotor The motor that pulls the robot up by turning the winch
     */
    public Climber(TalonSRX extensionMotor, SpeedController liftMotor) {
        //instantiate instance variables
        m_extensionMotor = extensionMotor;
        m_liftMotor = liftMotor;

        //sets our encoders to the encoders plugged into the talons
        m_extensionEncoder = new SensorCollection(m_extensionMotor);

        //sets starting position on object contruction
        //object construction should occur in RobotInit or in Robot contructor
        m_startingPosition = m_extensionEncoder.getQuadraturePosition();

        //config the extension talon to run via motion magic
        configExtensionMotionMagic();
    }

    /**
     * Vertical constructor with robot map constants
     */
    public Climber() {
        //instantiate instance variables
        m_extensionMotor = new TalonSRX(RobotMap.EXTENSION_MOTOR_ID);
        m_liftMotor = new PWMSparkMax(RobotMap.LIFT_MOTOR_PORT);

        //sets our encoders to the encoders plugged into the talons
        m_extensionEncoder = new SensorCollection(m_extensionMotor);

        //sets starting position on object contruction
        //object construction should occur in RobotInit or in Robot contructor
        m_startingPosition = m_extensionEncoder.getQuadraturePosition();

        //config the extension talon to run via motion magic
        configExtensionMotionMagic();
    }

    /**
     * Assigns a speed directly to the extension motor controller
     * @param motorSpeed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setExtensionSpeed(double motorSpeed) {
        m_extensionMotor.set(ControlMode.PercentOutput, motorSpeed);
    }
    
    /**
     * Assigns a speed directly to the lift motor controller
     * @param motorSpeed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    public void setLiftSpeed(double motorSpeed) {
        m_liftMotor.set(motorSpeed);
    }

    /**
     * Uses motion magic to drive the climber to a set position
     * @param targetPos The position in encoder ticks we are attempting to drive to
     */
    public void controlClimberExtension(int targetPos) {
        m_extensionMotor.set(ControlMode.MotionMagic, targetPos);
    }

    /**
     * Extends the climber to a predefined position set in the robot map
     * <p>We may need multiple targets/methods depending on our strategy for climbing.
     */
    public void extendClimber() {
        controlClimberExtension(RobotMap.CLIMBER_EXTENSION_ENCODER_TARGET);
    }

    /**
     * Retracts the climber to its starting position
     * <p>Currently set to the value read off of the encoder at the start of the robot
     */
    public void retractClimber() {
        controlClimberExtension(m_startingPosition);
    }

    /**
     * Retracts the climber to lift the robot
     * <p>This position should probably be defined by a RobotMap constant from testing
     * <p>Currently we assume the encoder direction and motor direction in the comparison, this should be tested before we attempt to actually climb
     * 
     * @param targetPos The target position for the encoder
     * @param extensionSpeed The cruise speed for the lift
     * 
     * @return whether we have lifted our robot far enough
     */
    public boolean liftRobot(int targetPos, double liftSpeed) {
        //if we have not yet reached our target, move to reach it
        if (m_extensionEncoder.getQuadraturePosition() < targetPos) {
            setLiftSpeed(liftSpeed);
            //return that we have not yet reached our target
            return false;
        }
        //if we have reached or passed our target, stop immediately
        else {
            setLiftSpeed(0);
            //return that we have reached our target
            return true;
        }
    }

    /**
     * Assigns a setpoint that the motor controller will then accelerate to
     * @param setpoint setpoint The target speed between -1.0 and 1.0
     */
    public void liftProportionalSpeedSetter(double setpoint) {
        //calculates error based on the difference between current and target speeds
        m_error = setpoint - m_liftCurrentSpeed;
        //adjusts the current speed proportionally to the error
        m_liftCurrentSpeed += (m_error * RobotMap.CLIMBER_ADJUSTMENT_VALUE);

        //sets the speed of the motors based on the adjusted current speed
        setLiftSpeed(m_liftCurrentSpeed);
    }

    /**
     * Configures the motion magic PID control for the extension motor
     */
    public void configExtensionMotionMagic() {
        //zero our motors and reset the encoders
        // zeroMotors();
        encoderReset();

        //set our feedback sensor to the encoder plugged into the motor controller
        m_extensionMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);

        //sets whether our motor is inverted
        //this is currently false but can be switched based on testing
        m_extensionMotor.setInverted(false);
        m_extensionMotor.setSensorPhase(false);

        //this sets how often we pull data from our sensor
        //as it is currently set, the motor controller will read from the encoder every 10 miliseconds
        m_extensionMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, RobotMap.CLIMBER_FEEDBACK_PERIOD_MS, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //this configs the deadband for the PID output. Any output with an absolute value less than this will be treated as zero
        //curently, this is the factory default (0.04), but should be tuned on testing
        m_extensionMotor.configNeutralDeadband(RobotMap.CLIMBER_NEUTRAL_DEADBAND, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //this sets the peak output for our PID. The PID cannot output a value higher than this
        //this is currently set to 1.0, so the PID can output as much as it wants
        m_extensionMotor.configPeakOutputForward(RobotMap.CLIMBER_PID_PEAK_OUTPUT, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);
        //this does the same thing but for the reverse direction
        m_extensionMotor.configPeakOutputReverse(-RobotMap.CLIMBER_PID_PEAK_OUTPUT, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //configs the acceleration of the extension in sensor units per hundered miliseconds per second
        //this is currently set to 1000u/100ms/s, which is half the value of last year's elevator
        m_extensionMotor.configMotionAcceleration(RobotMap.CLIMBER_MOTION_MAGIC_ACCEL, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);
        m_extensionMotor.configMotionSCurveStrength(1);
        //configs the cruise velocity of the extension in sensor units per hundred miliseconds
        //this is currently set to 1000u/100ms, which is half the value of last year's elevator
        m_extensionMotor.configMotionCruiseVelocity(RobotMap.CLIMBER_MOTION_MAGIC_CRUISE_VELOCITY, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //this sets the PIDF values for the motion magic PID controller
        //the zero is the PID slot, in this case it is zero for the primary PID. The climber has no aux PID (Which would be for turning)
        //that zero repeats in much of the PID config methods called here, and always does that
        m_extensionMotor.config_kP(0, RobotMap.CLIMBER_EXTENSION_P, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);
        m_extensionMotor.config_kI(0, RobotMap.CLIMBER_EXTENSION_I, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);
        m_extensionMotor.config_kD(0, RobotMap.CLIMBER_EXTENSION_D, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);
        m_extensionMotor.config_kF(0, RobotMap.CLIMBER_EXTENSION_F, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //this sets the acceptable amount of Integral error, where if the absolute accumulated error exceeds this ammount, it resets to zero
        //this is designed to prevent the PID from going crazy if we move too far from our target
        m_extensionMotor.config_IntegralZone(0, RobotMap.CLIMBER_EXTENSION_I_ZONE, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //sets the max output of the motor specifically within closed loop control
        //this is likely redundant, but the values can be set to seperate if needed in testing
        m_extensionMotor.configClosedLoopPeakOutput(0, RobotMap.CLIMBER_PID_PEAK_OUTPUT, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //this configures an allowable error in closed loop control
        //any error less than this is treated as zero. We currently set this to zero, but we can increase it if need be
        m_extensionMotor.configAllowableClosedloopError(0, RobotMap.CLIMBER_EXTENSION_ACCEPTABLE_ERROR, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //configures the period for closed loop calculations in MS 
        //Currently set to 10, but should be increased if the can bus is haveing issues
        m_extensionMotor.configClosedLoopPeriod(0, RobotMap.CLIMBER_EXTENSION_CLOSED_LOOP_PERIOD_MS, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);

        //sets our closed loop control to use our primary PID slot
        m_extensionMotor.selectProfileSlot(0, 0);
    }

    /**
     * Sets both motors' speeds to zero
     */
    public void zeroMotors() {
        setLiftSpeed(0);
        setExtensionSpeed(0);
    }

    /**
     * Sets the extension motor's speed to zero
     */
    public void zeroExtensionMotor() {
        setExtensionSpeed(0);
    }

    /**
     * Sets the lift motor's speed to zero
     */
    public void zeroLiftMotor() {
        setLiftSpeed(0);
    }

    /**
     * @return the extension motor for the climber
     */
    public BaseMotorController getExtensionMotor() {
        return m_extensionMotor;
    }

    /**
     * @return the lift motor for the climber
     */
    public SpeedController getLiftMotor() {
        return m_liftMotor;
    }

    /**
     * @return the encoder on the extension motor
     */
    public SensorCollection getExtensionEncoder() {
        return m_extensionEncoder;
    }

    /**
     * @return The position returned by the extension encoder
     */
    public int getExtensionEncoderPosition() {
        return m_extensionEncoder.getQuadraturePosition();
      }
  
    /**
     * @return The velocity returned by the extension encoder
     */
    public int getExtensionEncoderVelocity() {
        return m_extensionEncoder.getQuadratureVelocity();
    }
      
    /**
     * Resets the encoder
     * Sets the position of the encoders to zero maunally to zero it
     */
    public void encoderReset() {
        m_extensionEncoder.setQuadraturePosition(0, RobotMap.CLIMBER_CONFIG_TIMEOUT_MS);
    }

    /**
     * @return a summary of the member variables on the climber
     */
    public String toString() {
        return "Extension motor controller: " + m_extensionMotor.toString() + " | Lift motor controller: "
                + m_liftMotor.toString() + " | Extension encoder: " + m_extensionEncoder.toString() + " | Adjustment value: " + RobotMap.CLIMBER_ADJUSTMENT_VALUE;
    }
}