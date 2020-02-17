package frc.robot;

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
    public static final int TEST_CONTROLLER_PORT = 0;
    public static final int DRIVE_CONTROLLER_PORT = 1;

    //the deadband on our controller sticks to prevent drift
    public static final double PILOT_CONTROLLER_STICK_DEADBAND = 0.08;

    //****************************************
    //*                                      *
    //*        DRIVETRAIN CONSTANTS          *
    //*                                      *
    //****************************************
    //Drivetrain, turning, and velocity gains
    public static final Gains  DRIVETRAIN_GAINS = new Gains(0.3, 0.0, 0.0, 0.0, 100, 1.0);
    public static final Gains GAINS_TURNING = new Gains(0.1, 0.0, 0.0, 0.0, 200, 1.0);
    public static final Gains GAINS_VELOCIT = new Gains(0.1, 0.0, 0.0, 0.0, 300, 1.0);

    //The default scalar for drive inputs - what we multiply input from drive controller by
    public static final double DRIVE_DEFAULT_INPUT_SCALAR = 0.6;
    
    //PIDF constants----Could change
    public static final double PID_INPUT_RANGE = 180.00;
    public static final double PID_OUTPUT_RANGE = 180.00;
    public static final double TOLERANCE_ROTATE_CONROLLER = 2.00;
    public static final double FINISHED_PID_THRESHOLD = 0.15;
    public static final int PID_PRIMARY = 0;

    public static final int TIMEOUT_MS = 30;
    public static final int RIGHT_PERIOD_MS = 20;
    public static final int LEFT_PERIOD_MS = 5;

    public static final double DRIVE_MAX_DELTA_SPEED = 0.1;
    public static final double DRIVE_MAX_QUICK_TURN_SPEED = 0.1;
    public static final double AUTO_SPEED = 0.2;

  public static final double PERCENT_DEADBAND = 0.001;

    public static final double SCALE_FEEDBACK_COEFFICIENT_VALUE = 0.5;

    public static final double UNSCALED_FEEDBACK_COEFFICIENT_VALUE = 1;

    public static final double PEAK_OUTPUT = 1.0;
    
    public static final int SENSOR_UNIT_PER_100MS_PER_SEC = 2000;
    public static final int ALLOWABLE_CLOSED_LOOP_ERROR = 0;
    public static final int LOOP_TIME_MS = 10;
    public static final int PERIOD_MS = 10;

    //the time it takes the drive train to ramp to full speed in open loop control in seconds
    public static final double DRIVE_RAMP_TIME = 0.5;

    public static final double PI = 3.14159265359;
    // the 6 has to be changed to the diameter of our wheels
    public static final double DRIVE_TICS_PER_INCH = (4096 / (6*RobotMap.PI));

    //the total number of encoder ticks in a rotate----Check to see if this is the correct number
    public static final double STARTING_TICK_VALUE = 1440;

    //can IDs for drive motors
    //This is for the original can setup
    //this is still here as documentation for why the TalonFXs have these can ID's +1
    public static final int LEFT_TALON_ID = 1;
    public static final int RIGHT_TALON_ID = 2;
    public static final int LEFT_VICTOR_ID = 11;
    public static final int RIGHT_VICTOR_ID = 12;

    //can IDs for Falcon drive motors
    //this is current
    public static final int MASTER_LEFT_FALCON_ID = 3;
    public static final int MASTER_RIGHT_FALCON_ID = 4;
    public static final int SLAVE_LEFT_FALCON_ID = 13;
    public static final int SLAVE_RIGHT_FALCON_ID = 14;

    //solenoid ports
    public static final int LEFT_SOLENOID_FORWARD_PORT = 0;
    public static final int LEFT_SOLENOID_REVERSE_PORT = 1;
    public static final int RIGHT_SOLENOID_FORWARD_PORT = 2;
    public static final int RIGHT_SOLENOID_REVERSE_PORT = 3;


    //****************************************
    //*                                      *
    //*          LAUNCHER CONSTANTS          *
    //*                                      *
    //****************************************
    //launcher PID constants for velocity control
    //to be set later
    public static final double LAUNCHER_P = 0;
    public static final double LAUNCHER_I = 0;
    public static final double LAUNCHER_D = 0;
    public static final double LAUNCHER_F = 0;

    //Calculated free spin angular velocity of our shooter based on specs (-10%) divided by three for gear reduction
    //measured in rev/100ms, specs found at https://www.vexrobotics.com/775pro.html#Other_Info
    public static final double LAUNCHER_FREESPIN_ANGULAR_VELOCITY = 9.356;
    
    //The maximum distance that the launcher can be shot from and still make the target (given max power)
    //this number is currently arbitrary (25 feet)
    public static final double MAX_LAUNCHER_DISTANCE_IN = 300;

    //the ports for the launcher motors
    public static final int MASTER_LAUNCHER_ID = 21;
    public static final int CLOSE_LAUNCHER_SLAVE_ID = 22;
    public static final int FAR_LAUNCHER_SLAVE1_ID = 23;
    public static final int FAR_LAUNCHER_SLAVE2_ID = 24;

    //adjustment value for the launcher percent control
    //0.5 is pretty arbitrary, it is the value that was used for initial launcher testing
    public static final double LAUNCHER_ADJUSTMENT_VALUE = 0.5;

    //the launcher timeout for running confing methods
    public static final int LAUNCHER_CONFIG_TIMEOUT_MS = 30;

    //the period for reading data from the encoders attached to the motor controllers
    public static final int LAUNCHER_FEEDBACK_PERIOD_MS = 10;

    //the neutral deadband for our launcher PID
    public static final double LAUNCHER_NEUTRAL_DEADBAND = 0.04;

    //the peak output on our launcher PID
    public static final double LAUNCHER_PID_PEAK_OUTPUT = 1.0;

    //the number of samples use in rolling average. Valid values are 1,2,4,8,16,32. If another value is specified, it will truncate to nearest support value.
    //this number is currently arbitrary
    public static final int LAUNCHER_VELOCITY_MEASUREMENT_WINDOW = 8;

    //the acceptable integral zone for the launch master motor
    //100 is the value used last year, this should be adjusted in testing if need be
    public static final int LAUNCHER_I_ZONE = 100;

    //the acceptable error for the launcher PID. Any error less than this will be treated as zero
    public static final int LAUNCHER_ACCEPTABLE_ERROR = 0;

    //the closed loop period for the launcher PID
    public static final int LAUNCHER_CLOSED_LOOP_PERIOD_MS = 10; 

    //****************************************
    //*                                      *
    //*           CLIMBER CONSTANTS          *
    //*                                      *
    //****************************************
    //the cruise speed for the climber
    public static final double CLIMBER_SPEED = 0.5;

    //the encoder targets for our lift and extension motor
    public static final int CLIMBER_EXTENSION_ENCODER_TARGET = 0;
    public static final int CLIMBER_LIFT_ENCODER_TARGET = 0;

    //the climber timeout for running confing methods
    public static final int CLIMBER_CONFIG_TIMEOUT_MS = 30;

    //the period for reading data from the encoders attached to the motor controllers
    public static final int CLIMBER_FEEDBACK_PERIOD_MS = 10;

    //the neutral deadband for our climber PIDs
    public static final double CLIMBER_NEUTRAL_DEADBAND = 0.04;

    //the peak output on our climber PIDs
    public static final double CLIMBER_PID_PEAK_OUTPUT = 1.0;

    //the acceleration for the climber in units per 100ms per second
    //1000 is half the value for the elevator last year, this needs to be tuned via testing
    public static final int CLIMBER_MOTION_MAGIC_ACCEL = 1000;

    //the cruise velocity for the climber in units per second
    //1000 is half the value for the elevator last year, this needs to be tuned via testing
    public static final int CLIMBER_MOTION_MAGIC_CRUISE_VELOCITY = 1000;

    //the PIDF values for the extension motor on the climber
    //these values are temporary and should be tuned through testing
    public static final double CLIMBER_EXTENSION_P = 0;
    public static final double CLIMBER_EXTENSION_I = 0;
    public static final double CLIMBER_EXTENSION_D = 0;
    public static final double CLIMBER_EXTENSION_F = 0;

    //the acceptable integral zone for the extension motor
    //100 is the value used last year, this should be adjusted in testing if need be
    public static final int CLIMBER_EXTENSION_I_ZONE = 100;

    //the acceptable error for the extension PID. Any error less than this will be treated as zero
    public static final int CLIMBER_EXTENSION_ACCEPTABLE_ERROR = 0;

    //the closed loop period for the extension PID
    public static final int CLIMBER_EXTENSION_CLOSED_LOOP_PERIOD_MS = 10;

    //****************************************
    //*                                      *
    //*           INTAKE CONSTANTS           *
    //*                                      *
    //****************************************
    public static final int INTAKE_SPARK_ID = 15;

    //the ramp time in seconds from zero to full speed for the intake motor
    public static final double INTAKE_OPEN_LOOP_RAMP_TIME_S = 0.75;

    //The maximum number of ticks that the encoder has------The number of ticks might not be correct:double check
    public static final double MAX_ENCODER_TICKS = 22;

    //Speed of the motor on the drop bar when going down
    public static final double DROP_BAR_SPEED_DOWN = 0.5;
    //Speed of the motor on the drop bar when going up
    public static final double DROP_BAR_SPEED_UP = -0.5;

    

    //****************************************
    //*                                      *
    //*          CONTROL ROTATOR CONSTANTS   *
    //*                                      *
    //****************************************
    public static final double TICKS_PER_REVOLUTION = 50;

    //****************************************
    //*                                      *
    //*          LIMELIGHT CONSTANTS         *
    //*                                      *
    //****************************************
    //This is the height of the
    public static final double TARGET_HEIGHT_INCHES = 98.25;
    //This is temporary and will need to be assigned on our final bot
    public static final double CAMERA_HEIGHT_INCHES = 45d;
    //This is the offset we make for inner target TODO:Needs to be tested and tuned
    public static final double OFFSET_TARGET_DEGREES = 0.8;
    //This is the range on either side in degrees where we can still hit the inner target
    public static final double INNER_TARGET_DEGREES = 4.5;

    //PID values for targeting the vision target
    //TODO: These values are temporary and need to be updated in testing
    public static final double TARGETING_P = 0;
    public static final double TARGETING_I = 0;
    public static final double TARGETING_D = 0;

    //the period between controller updates in seconds
    public static final double TARGETING_PERIOD_S = 20;

    //the maximum accumulated error for the Integral portion of our targeting PID
    public static final double TARGETING_MAX_ACCUMULATED_ERROR = 0;

    //the acceptable error for our vision targeting in degrees
    public static final double TARGETING_ERROR_TOLERANCE = 0;

    //****************************************
    //*                                      *
    //*           GENERAL CONSTANTS          *
    //*                                      *
    //****************************************
    public static final int PCM_CAN_ID = 20;
	public static final int CYCLES_TO_MOVE_BALL_ONE_POSITION = 30;


}