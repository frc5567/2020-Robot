package frc.robot;

import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;

/**
 * A collection of constants for our robot
 * @version 1/25/2019
 */
public class RobotMap {
    //****************************************
    //*                                      *
    //*        CONTROLLER CONSTANTS          *
    //*                                      *
    //****************************************
    /**The USB port for the testing controller */
    public static final int TEST_CONTROLLER_PORT = 0;
    /**The USB port for the drive controller */
    public static final int DRIVE_CONTROLLER_PORT = 1;
    /**The USB port for the GamePad */
    public static final int GAMEPAD_PORT = 2;

    /**the deadband on our controller sticks, used to prevent drift*/
    public static final double PILOT_CONTROLLER_STICK_DEADBAND = 0.08;

    /** A storage class to put all of the gamepad button IDs in the same spot */
    static final class GAMEPAD_BUTTON_ID
    {
        public static final int CLIMB_UP = 2;
		public static final int CLIMB_DOWN = 3;
		public static final int WINCH = 1;
		public static final int MOVE_MAGAZINE_DOWN = 4;
		public static final int MOVE_MAGAZINE_LAUNCH = 5;
		public static final int LAUNCHER_AND_MAGAZINE = 7;
		public static final int REV_LAUNCHER = 7;
		public static final int ENABLE_INTAKE = 8;
		public static final int DISABLE_INTAKE = 9;
		public static final int DUMP_BALLS = 12;
		public static final int COLOR_WHEEL_COLOR = 11;
		public static final int COLOR_WHEEL_DISTANCE = 10;
    }

    //****************************************
    //*                                      *
    //*        DRIVETRAIN CONSTANTS          *
    //*                                      *
    //****************************************
    //Whether the drivetrain has two solenoids
    public static final boolean DRIVETRAIN_HAS_TWO_SOLENOIDS = true;

    //Drivetrain, turning, and velocity gains
    /**The gains for the drivetrain PID control */
    public static final Gains DRIVETRAIN_GAINS = new Gains(0.3, 0.0, 0.0, 0.0, 100, 1.0);
    /**The gains for the CTRE drivetrain turning aux PID*/
    public static final Gains GAINS_TURNING = new Gains(0.1, 0.0, 0.0, 0.0, 200, 1.0);
    /**The gains for the CTRE drivetrain velocity/distance primary PID*/
    public static final Gains GAINS_VELOCITY = new Gains(0.1, 0.0, 0.0, 0.0, 300, 1.0);

    /**The default scalar for drive inputs - what we multiply input from drive controller by*/
    public static final double DRIVE_DEFAULT_INPUT_SCALAR = 0.6;

    /**We divide rotational PID output by this number to scale it to match our percent values */
    public static final double DRIVE_PID_OUTPUT_SCALAR = 180;
    
    /**
     * The possible input range for our rotational PID based on gyro input. 
     * We use this for enableContinous mode to save us from moduloe (-180 and 180 are the same value)
     */
    public static final double PID_INPUT_RANGE = 180.00;

    /**The cap on the I output, which helps to prevent crazy oscillation */
    public static final double ROTATE_PID_INTEGRATOR_RANGE = 0.10;

    /**
     * The tolerance in degrees of the rotation controller. 
     * All error lower than this is treated as zero 
     */
    public static final double TOLERANCE_ROTATE_CONROLLER = 2.00;

    /**The timeout for the CTRE config methods in miliseconds */
    public static final int TIMEOUT_MS = 30;

    /**The timeout for the status frame config methods particular to the right side */
    public static final int RIGHT_PERIOD_MS = 20;
    /**The timeout for the status frame config methods particular to the left side */
    public static final int LEFT_PERIOD_MS = 5;

    /**The deadband in percent output for the motor. Any value less than this is treated as zero */
    public static final double PERCENT_DEADBAND = 0.001;

    public static final double SCALE_FEEDBACK_COEFFICIENT_VALUE = 0.5;

    public static final double UNSCALED_FEEDBACK_COEFFICIENT_VALUE = 1;

    /**The peak output for the motors */
    public static final double PEAK_OUTPUT = 1.0;
    
    /**The motion magic acceleration in sensor units per 100ms */
    public static final int DRIVE_CRUISE_SPEED = 2000;
    
    /**The motion magic acceleration in sensor units per 100ms per second */
    public static final int DRIVE_ACCEL = 2000;

    public static final int ALLOWABLE_CLOSED_LOOP_ERROR = 0;
    public static final int LOOP_TIME_MS = 10;
    public static final int PERIOD_MS = 10;

    //the time it takes the drive train to ramp to full speed in open loop control in seconds
    public static final double DRIVE_RAMP_TIME = 1.0;

    //TODO:Does not account for gearing
    public static final double DRIVE_TICS_PER_INCH = (2048 / (6.25*Math.PI));

    //the total number of encoder ticks in a rotate----TODO: Check to see if this is the correct number
    public static final double STARTING_TICK_VALUE = 1440;

    //can IDs for Falcon drive motors
    public static final int MASTER_LEFT_FALCON_ID = 3;
    public static final int MASTER_RIGHT_FALCON_ID = 4;
    public static final int SLAVE_LEFT_FALCON_ID = 13;
    public static final int SLAVE_RIGHT_FALCON_ID = 14;

    //solenoid ports TODO: Make one solenoid based on RIGHT
    public static final int LEFT_SOLENOID_FORWARD_PORT = 4;
    public static final int LEFT_SOLENOID_REVERSE_PORT = 5;
    public static final int RIGHT_SOLENOID_FORWARD_PORT = 2;
    public static final int RIGHT_SOLENOID_REVERSE_PORT = 1;


    //****************************************
    //*                                      *
    //*          LAUNCHER CONSTANTS          *
    //*                                      *
    //****************************************
    //launcher PID constants for velocity control
    public static final double LAUNCHER_P = 1.0;
    public static final double LAUNCHER_I = 0;
    public static final double LAUNCHER_D = 0.1;
    public static final double LAUNCHER_F = .27;

    //600 is minutes to 100ms, 2048 is revs to sensor ticks
    public static final double RPM_TO_UNITS_PER_100MS = 2048.0 / 600;

    //Calculated free spin angular velocity of our shooter based on specs (-10%) divided by three for gear reduction
    //measured in rev/100ms, specs found at https://www.vexrobotics.com/775pro.html#Other_Info
    public static final double LAUNCHER_FREESPIN_ANGULAR_VELOCITY = 9.356;
    
    //The maximum distance that the launcher can be shot from and still make the target (given max power)
    //this number is currently arbitrary (25 feet)
    public static final double MAX_LAUNCHER_DISTANCE_IN = 300;

    //CAN IDs for the launcher motors
    public static final int MASTER_LAUNCHER_ID = 21;
    public static final int CLOSE_LAUNCHER_SLAVE_ID = 22;
    public static final int FAR_LAUNCHER_SLAVE1_ID = 23;
	public static final int FAR_LAUNCHER_SLAVE2_ID = 24;
	
	//inversion for master motor
	public static final boolean LAUNCHER_MASTER_INVERTED = false;

    //inversion for far slave motors
    //The far slave motor 1 was inverted at manufacturing, so it needs to not be inverted
    public static final boolean LAUNCHER_FAR_SLAVE1_INVERTED = false;
    public static final boolean LAUNCHER_FAR_SLAVE2_INVERTED = true;

    //adjustment value for the launcher percent control
    //0.5 is pretty arbitrary, it is the value that was used for initial launcher testing
    public static final double LAUNCHER_ADJUSTMENT_VALUE = 0.5;
    
    public static final double LAUNCHER_HOLDING_SPEED = 0.5;
    
    //the measurement period for calculating velocity off of the encoder
	public static final VelocityMeasPeriod VELOCITY_MEASUREMENT_PERIOD = VelocityMeasPeriod.Period_10Ms;

    //the launcher timeout for running confing methods
    public static final int LAUNCHER_CONFIG_TIMEOUT_MS = 30;

    //the period for reading data from the encoders attached to the motor controllers
    public static final int LAUNCHER_FEEDBACK_PERIOD_MS = 10;

    //the neutral deadband for our launcher PID
    public static final double LAUNCHER_NEUTRAL_DEADBAND = 0.04;

    //the peak output on our launcher PID
    public static final double LAUNCHER_PID_PEAK_OUTPUT = 0.9;

    //the number of samples use in rolling average. Valid values are 1,2,4,8,16,32. If another value is specified, it will truncate to nearest support value.
    //this number is currently arbitrary
    public static final int LAUNCHER_VELOCITY_MEASUREMENT_WINDOW = 8;

    //the acceptable integral zone for the launch master motor
    //100 is the value used last year, this should be adjusted in testing if need be
    public static final int LAUNCHER_I_ZONE = 1000;

    //the acceptable error for the launcher PID. Any error less than this will be treated as zero
    public static final int LAUNCHER_ACCEPTABLE_ERROR = 50;

    //the closed loop period for the launcher PID
    public static final int LAUNCHER_CLOSED_LOOP_PERIOD_MS = 10; 
    public static final int LAUNCHER_OPEN_LOOP_RAMP_TIME_S = 3;

    //****************************************
    //*                                      *
    //*           CLIMBER CONSTANTS          *
    //*                                      *
    //****************************************
    /**The CAN ID for the extension motor */
    public static final int EXTENSION_MOTOR_ID = 27;
    /**The PWM port for the winch or lift motor */
    public static final int LIFT_MOTOR_PORT = 3;

    /**The adjustment value for the proportionality constant for the lift motor */
    public static final double CLIMBER_ADJUSTMENT_VALUE = 0.5;

    //the encoder target of for the extension motor.
    public static final int CLIMBER_EXTENSION_ENCODER_TARGET = 29600;
    public static final int CLIMBER_EXTENSION_HARD_LIMIT = 29200;

    public static final int CLIMBER_MIN_EXTENSION = 500;

    //the climber timeout for running confing methods
    public static final int CLIMBER_CONFIG_TIMEOUT_MS = 30;

    //the period for reading data from the encoders attached to the motor controllers
    public static final int CLIMBER_FEEDBACK_PERIOD_MS = 10;

    //the neutral deadband for our climber PIDs
    public static final double CLIMBER_NEUTRAL_DEADBAND = 0.02;

    //the peak output on our climber PIDs
    public static final double CLIMBER_PID_PEAK_OUTPUT = 1.0;

    //the acceleration for the climber in units per 100ms per second
    //1000 is half the value for the elevator last year, this needs to be tuned via testing
    public static final int CLIMBER_MOTION_MAGIC_ACCEL = 2000;

    //the cruise velocity for the climber in units per second
    //1000 is half the value for the elevator last year, this needs to be tuned via testing
    public static final int CLIMBER_MOTION_MAGIC_CRUISE_VELOCITY = 2000;

    //the PIDF values for the extension motor on the climber
    //these values are temporary and should be tuned through testing
    public static final double CLIMBER_EXTENSION_P = 0.4;
    public static final double CLIMBER_EXTENSION_I = 0.01;
    public static final double CLIMBER_EXTENSION_D = 0;
    public static final double CLIMBER_EXTENSION_F = 0.05;

    public static final double CLIMBER_EXTENSION_MANUAL_SPEED = 0.4;
    public static final double CLIMBER_WINCH_SPEED = 0.5;
    public static final double CLIMBER_REDUCED_SPEED = 0.125;

    //the acceptable integral zone for the extension motor
    //100 is the value used last year, this should be adjusted in testing if need be
    public static final int CLIMBER_EXTENSION_I_ZONE = 600;

    //the acceptable error for the extension PID. Any error less than this will be treated as zero
    public static final int CLIMBER_EXTENSION_ACCEPTABLE_ERROR = 0;

    //the closed loop period for the extension PID
    public static final int CLIMBER_EXTENSION_CLOSED_LOOP_PERIOD_MS = 10;

    //****************************************
    //*                                      *
    //*           INTAKE CONSTANTS           *
    //*                                      *
    //****************************************
    //the PWM port for the outer motor - this is on PWM to reduce CAN traffic
    public static final int INTAKE_PWM_SPARK_PORT = 2;
    public static final int INTAKE_INNER_MOTOR_PORT = 1;

    //inversion for our intake motors
    public static final boolean OUTER_INTAKE_INVERTED = true;
    public static final boolean INNER_INTAKE_INVERTED = true;

    public static final double OUTER_INTAKE_SPEED = 0.8;
    public static final double INNER_INTAKE_SPEED = 0.3;

    //the ports for the intake position piston
    public static final int INTAKE_POSITION_PISTON_FORWARD_PORT = 0;
    public static final int INTAKE_POSITION_PISTON_REVERSE_PORT = 3;

    //the ramp time in seconds from zero to full speed for the intake motor
    public static final double INTAKE_OPEN_LOOP_RAMP_TIME_S = 0.75;

    //****************************************
    //*                                      *
    //*          CONTROL ROTATOR CONSTANTS   *
    //*                                      *
    //****************************************
    //TODO: Evaluate if we are keeping control rotator in master
    public static final double TICKS_PER_REVOLUTION = 50;

    //****************************************
    //*                                      *
    //*          LIMELIGHT CONSTANTS         *
    //*                                      *
    //****************************************
    /**Height of the vision target in inches */
    public static final double TARGET_HEIGHT_INCHES = 98.25;
    /**Height of the limelight lense in inches */
    public static final double CAMERA_HEIGHT_INCHES = 43.4;
    /**The difference between the camera height and the target height */
    public static final double NET_HEIGHT_INCHES = RobotMap.TARGET_HEIGHT_INCHES - RobotMap.CAMERA_HEIGHT_INCHES;

    public static final double CAMERA_DEGREES_FROM_GROUND = 14.4;
    /**
     * This is the offset we make for inner target - 
     * we multiply this by our actual offset to change our targeting 
     * <p> TODO:Needs to be tested and tuned
     */
    public static final double OFFSET_TARGET_DEGREES = 5.5;

    //This is the range on either side in degrees where we can still hit the inner target
    public static final double INNER_TARGET_DEGREES = 4.5;

    //PID values for targeting the vision target
    public static final double TARGETING_P = 0.55;
    public static final double TARGETING_I = 0.002;
    public static final double TARGETING_D = 0.75;

    //the period between controller updates in seconds
    //this is used for the calculation of the velocity error and the total error - i and d
    public static final double TARGETING_PERIOD_S = 2;

    //the maximum accumulated error for the Integral portion of our targeting PID
    public static final double TARGETING_MAX_ACCUMULATED_ERROR = 2;

    //the acceptable error for our vision targeting in degrees
    public static final double TARGETING_ERROR_TOLERANCE = 1.0;

    //****************************************
    //*                                      *
    //*          MAGAZINE CONSTANTS          *
    //*                                      *
    //****************************************
    /**The CAN ID for the motor driving the magazine */
    public static final int MAGAZINE_MOTOR_PORT = 30;

    /**The DIO port for the photoelectric sensor mounted near the intake */
    public static final int MAGAZINE_IN_SENSOR_PORT = 7;

    /**The DIO port for the photoelectric sensor mounted near the launcher */
    public static final int MAGAZINE_OUT_SENSOR_PORT = 8;

    public static final double MAGAZINE_INTAKE_SPEED = 0.65;
    public static final double MAGAZINE_LAUNCH_SPEED = 0.65;
    public static final double MAGAZINE_DUMP_SPEED = -0.45;

    //****************************************
    //*                                      *
    //*           GENERAL CONSTANTS          *
    //*                                      *
    //****************************************
    public static final int PCM_CAN_ID = 20;
    
    /**
     * The primary slot for a CTRE PID controller. This slot controlls the main motion of the system, 
     * as oppose to an auxillary controller which controls turning
     */
    public static final int PID_PRIMARY_SLOT = 0;

    /**
     * The auxillary slot for a CTRE PID controller. This slot controlls the turning motion of the system, 
     * as oppose to primary controller which controls direct motion
     */
    public static final int PID_AUXILLARY_SLOT = 1;
    
    
    /**The conversion rate when switching from degrees to radians */
    public static final double DEG_TO_RAD_CONVERSION = (Math.PI / 180);


}