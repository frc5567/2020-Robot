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
    public static final double PILOT_CONTROLLER_STICK_DEADBAND = 0.05;

    //****************************************
    //*                                      *
    //*        DRIVETRAIN CONSTANTS          *
    //*                                      *
    //****************************************
    //the time it takes the drive train to ramp to full speed in open loop control in seconds
    public static final double DRIVE_RAMP_TIME = 0.5;

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
    public static final int SLAVE_RIGHT_FALCON_ID = 4;

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


}