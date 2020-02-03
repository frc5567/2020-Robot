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