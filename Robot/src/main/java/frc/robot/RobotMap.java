package frc.robot;

/**
 * A collection of constants for our robot
 * @version 1/25/2019
 */
public class RobotMap {
    //****************************************
    //*                                      *
    //*        DRIVETRAIN CONSTANTS          *
    //*                                      *
    //****************************************
    //the time it takes the drive train to ramp to full speed in open loop control in seconds
    public static final double DRIVE_RAMP_TIME = 0.5;

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


}