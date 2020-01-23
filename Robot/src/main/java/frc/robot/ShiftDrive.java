package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ShiftDrive {

    /**
     * An Enum for storing what gear the drivetrain is currently in
     */
    public enum Gear {
        kLow,
        kHigh;
    }

    //declare our drive motors
    private TalonSRX m_leftDrive;
    private TalonSRX m_rightDrive;

    //declare our slave motors
    private VictorSPX m_leftSlave;
    private VictorSPX m_rightSlave;

    //declare the solenoids used to shift gears
    //note that this may be one solenoid in the future
    private DoubleSolenoid m_leftPiston;
    private DoubleSolenoid m_rightPiston;

    //declare a Gear object to store what gear we are in
    private Gear m_gear;

    //constructor
    //pass in 2 drive, 2 slave
    /**
     * Contructor for drive trains that can shift speed based on double solenoids
     * @param leftDrive The left master motor controller
     * @param rightDrive The right master motor controller
     * @param leftSlave The left slave motor contoller
     * @param rightSlave The right slave motor controller
     * @param leftPiston The solenoid for shifting gears on the left gearbox
     * @param rightPiston THe solenoid for shifting gears on the right gearbox
     */
    public ShiftDrive(TalonSRX leftDrive, TalonSRX rightDrive, VictorSPX leftSlave, VictorSPX rightSlave, DoubleSolenoid leftPiston, DoubleSolenoid rightPiston) {
        m_leftDrive = leftDrive;
        m_rightDrive = rightDrive;
        m_leftSlave = leftSlave;
        m_rightSlave = rightSlave;

        m_leftPiston = leftPiston;
        m_rightPiston = rightPiston;

        //invert the right drive motor, this is arbitrary and should be based on tests
        m_rightDrive.setInverted(true);
        m_rightSlave.setInverted(InvertType.FollowMaster);

        //set the motors to brake when not given an active command
        m_leftDrive.setNeutralMode(NeutralMode.Brake);
        m_rightDrive.setNeutralMode(NeutralMode.Brake);

        //configs the drive train to have an acceleration based on the RobotMap constant
        m_leftDrive.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_rightDrive.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_leftSlave.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_rightSlave.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);

        m_gear = Gear.kLow;
    }

    /**
     * Sets the drive gear using our pistons. This is private so that it can never be called by an outside class to prevent confusion
     * 
     * @param value The value to give to the pistons where kOff removes pressure, kForward is _ gear, and kReverse is _ gear
     */
    private void setPistons(DoubleSolenoid.Value value) {
        m_leftPiston.set(value);
        m_rightPiston.set(value);
    }

    /**
     * Sets the drive gear using our pistons
     * <p> The solenoid value is currently arbitrary and needs to be confirmed
     * 
     * @param gear The gear we want the drivetrain to shift to
     */
    public void shiftGear(Gear gear) {
        //sets our current gear to the inputted gear
        m_gear = gear;

        //sets our pistons based on what gear we request
        if (m_gear == Gear.kLow) {
            setPistons(Value.kForward);
        }
        else if (m_gear == Gear.kHigh) {
            setPistons(Value.kReverse);
        }
    }

    /**
     * Toggles our gear to whatever it is not currently
     */
    public void switchGear() {
        if (m_gear == Gear.kLow) {
            m_gear = Gear.kHigh;
        }
        else if (m_gear == Gear.kHigh) {
            m_gear = Gear.kLow;
        }

        //shifts gears to whatever our new gear is
        shiftGear(m_gear);
    }

    /**
     * @return The gear that we are currently in (kHigh or kLow)
     */
    public Gear getGear() {
        return m_gear;
    }

    /**
     * Drives the drive train as a tank, controlling the sides individually
     * <p>Speeds are double values between -1.0 and 1.0, where 1.0 is full speed forwards
     * @param leftSpeed The speed for the left half of the drive train
     * @param rightSpeed The speed for the right half of the drive train
     */
    public void tankDrive (double leftSpeed, double rightSpeed){
        //sets power to the motors based on input
        m_leftDrive.set(ControlMode.PercentOutput, leftSpeed);
        m_rightDrive.set(ControlMode.PercentOutput, rightSpeed);

        //sets the slave motors to copy the masters
        m_leftSlave.follow(m_leftDrive);
        m_rightSlave.follow(m_rightDrive);
    }

    /**
     * Controls the drive train based on a speed and a turn input, with no acceleration (Racing Game Style)
     * @param speed The speed of the robot between -1.0 and 1.0 where 1.0 is max speed forwards
     * @param turn The turn of the robot between -1.0 and 1.0 where 1.0 is full rotational speed turning right
     */
    public void arcadeDrive(double speed, double turn){
        //this references last year's code, may need to be revised
        m_leftDrive.set(ControlMode.PercentOutput, turn, DemandType.ArbitraryFeedForward, speed);
        m_rightDrive.set(ControlMode.PercentOutput, turn, DemandType.ArbitraryFeedForward, speed);
        m_leftSlave.follow(m_leftDrive);
        m_rightSlave.follow(m_rightDrive);
    }
}