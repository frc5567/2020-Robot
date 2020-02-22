package frc.robot;

/**
 * Class for storing PID constants and gains in the robot map
 */
public class Gains {
    //Declares the variables to be passed into the gains function
    public final double kP;
    public final double kI;
    public final double kD;
    public final double kF;
    public final int kIzone;
    public final double kPeakOutput;

    /**
     * Constructor for Gains objects.
     * These objects store PID constants for easier reading in the RobotMap
     * @param _kP The proportionality constant
     * @param _kI The integral constant
     * @param _kD The derivative constant
     * @param _kF The feed-forward constant
     * @param _kIzone The Integral zone -> if absolute closed loop error exceeds this value, error is reset
     * @param _kPeakOutput The peak output of the PID control
     */
    public Gains(double _kP, double _kI, double _kD, double _kF, int _kIzone, double _kPeakOutput) {
        kP = _kP;
        kI = _kI;
        kD = _kD;
        kF = _kF;
        kIzone = _kIzone;
        kPeakOutput = _kPeakOutput;
    }
}