package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ShiftDrive {
    private TalonSRX leftDrive;
    private TalonSRX rightDrive;
    private VictorSPX leftSlave;
    private VictorSPX rightSlave;

    private DoubleSolenoid leftPiston;
    private DoubleSolenoid rightPiston;

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
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
        this.leftSlave = leftSlave;
        this.rightSlave = rightSlave;

        this.leftPiston = leftPiston;
        this.rightPiston = rightPiston;

        //invert the right drive motor, this is arbitrary and should be based on tests
        rightDrive.setInverted(true);
        rightSlave.setInverted(InvertType.FollowMaster);

        leftDrive.setNeutralMode(NeutralMode.Brake);
        rightDrive.setNeutralMode(NeutralMode.Brake);

        leftDrive.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        rightDrive.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        leftSlave.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        rightSlave.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
    }

    /**
     * Sets the drive gear using our pistons
     * 
     * @param value The value to give to the pistons where kOff removes pressure, kForward is _ gear, and kReverse is _ gear
     */
    public void setGear(DoubleSolenoid.Value value) {
        leftPiston.set(value);
        rightPiston.set(value);
    }

    //TODO Re-comment
    /**
     * Drives the drive train as a tank, controlling the sides individually
     * <p>Speeds are double values between -1.0 and 1.0, where 1.0 is full speed forwards
     * @param leftSpeed The speed for the left half of the drive train
     * @param rightSpeed The speed for the right half of the drive train
     */
    public void tankDrive (double leftSpeed, double rightSpeed){
        leftDrive.set(ControlMode.PercentOutput, leftSpeed);
        rightDrive.set(ControlMode.PercentOutput, rightSpeed);
        leftSlave.follow(leftDrive);
        rightSlave.follow(rightDrive);
    }

    /**
     * Controls the drive train based on a speed and a turn input, with no acceleration (Racing Game Style)
     * @param speed The speed of the robot between -1.0 and 1.0 where 1.0 is max speed forwards
     * @param turn The turn of the robot between -1.0 and 1.0 where 1.0 is full rotational speed turning right
     */
    public void arcadeDrive(double speed, double turn){
        //this references last year's code, may need to be revised
        leftDrive.set(ControlMode.PercentOutput, turn, DemandType.ArbitraryFeedForward, speed);
        rightDrive.set(ControlMode.PercentOutput, turn, DemandType.ArbitraryFeedForward, speed);
        leftSlave.follow(leftDrive);
        rightSlave.follow(rightDrive);
    }
}