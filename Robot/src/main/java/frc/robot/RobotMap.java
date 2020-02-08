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
    //Drivetrain, turning, and velocity gains
    public static final Gains  DRIVETRAIN_GAINS = new Gains(0.3, 0.0, 0.0, 0.0, 100, 1.0);
    public static final Gains GAINS_TURNING = new Gains(0.1, 0.0, 0.0, 0.0, 200, 1.0);
    public static final Gains GAINS_VELOCIT = new Gains(0.1, 0.0, 0.0, 0.0, 300, 1.0);
    
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

    public static final double SCALE_FEEDBACK_COEFFICIENT_VALUE = 0.5;

    public static final double UNSCALED_FEEDBACK_COEFFICIENT_VALUE = 1;

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

    //launcher target speed in encoder ticks per 100 ms
    //temporary value - needs to be tuned based on encoder
    public static final double LAUNCHER_SPEED = 0;

    //****************************************
    //*                                      *
    //*           CLIMBER CONSTANTS          *
    //*                                      *
    //****************************************
    public static final double CLIMBER_SPEED = 0.5;

    //****************************************
    //*                                      *
    //*           INTAKE CONSTANTS           *
    //*                                      *
    //****************************************
    public static final int INTAKE_VICTOR_ID = 15;

    //The maximum number of ticks that the encoder has------The number of ticks might not be correct:double check
    public static final double MAX_ENCODER_TICKS = 22;

    //Speed of the motor on the drop bar when going down
    public static final double DROP_BAR_SPEED_DOWN = 0.5;
    //Speed of the motor on the drop bar when going up
    public static final double DROP_BAR_SPEED_UP = -0.5;

    

    //****************************************
    //*                                      *
    //*          LIMELIGHT CONSTANTS         *
    //*                                      *
    //****************************************
    //This is the height of the
    public static final double TARGET_HEIGHT_INCHES = 98.25;
    //This is temporary and will need to be assigned on our final bot
    public static final double CAMERA_HEIGHT_INCHES = 45d;

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