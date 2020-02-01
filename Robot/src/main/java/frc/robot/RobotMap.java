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

    

    //****************************************
    //*                                      *
    //*        DRIVETRAIN CONSTANTS          *
    //*                                      *
    //****************************************
    //Drivetrain, turning, and velocity gains
    public static final Gains  DRIVETRAIN_GAINS = new Gains(0.3, 0.0, 0.0, 0.0, 100, 1.0);
    public static final Gains GAINS_TURNING = new Gains(0.1, 0.0, 0.0, 0.0, 200, 1.0);
    public static final Gains GAINS_VELOCIT = new Gains(0.1, 0.0, 0.0, 0.0, 300, 1.0);
    
    //the time it takes the drive train to ramp to full speed in open loop control in seconds
    public static final double DRIVE_RAMP_TIME = 0.5;

    public static final double PI = 3.14159265359;
    // the 6 has to be changed to the diameter of our wheels
    public static final double DRIVE_TICS_PER_INCH = (4096 / (6*RobotMap.PI));

    //can IDs for drive motors
    public static final int LEFT_TALON_ID = 1;
    public static final int RIGHT_TALON_ID = 2;
    public static final int LEFT_VICTOR_ID = 11;
    public static final int RIGHT_VICTOR_ID = 12;

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

    //****************************************
    //*                                      *
    //*           GENERAL CONSTANTS          *
    //*                                      *
    //****************************************
    public static final int PCM_CAN_ID = 20;


}