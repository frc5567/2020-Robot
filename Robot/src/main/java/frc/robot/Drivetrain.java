package frc.robot;

// imports Motor Controllers, Controller group functions, Basic differenctial drive code, solenoid functions, and functions for getting the joystick values
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;




public class Drivetrain {
    //Declares an enum for determining the position of the double solenoid. 
    public enum Gear{
        kLowGear("Low Gear"), 
        kHighGear("High Gear");

        private String gearName;

        /**
         * @param gearnName The name of the gear
         */
        Gear(String gearName) {
            this.gearName = gearName;
        }

        /**
         * Returns the position object represented as a string
         */
        public String toString() {
            return this.gearName;
        }
    }

    //Declares the NavX for rotational control
    NavX m_gyro;

    //Declares the turn control PID
    PIDController m_rotController;

    //Declare the flag for checking if this is the first time entering this method in a given run
    boolean m_firstCall = true;

    //Delcare variable to determine if two solenoids are being used
    private final boolean m_hasTwoSolenoids;

    // Declare variable for speed
    double m_speed = 0.0;
    // Declare master left talon
    TalonFX m_masterRightMotor;
    // Declare slave left talon
    TalonFX m_masterLeftMotor;
    // Declare master right talon
    TalonFX m_slaveLeftMotor;
    // Declare slave right talon
    TalonFX m_slaveRightMotor;

    //Declares the encoder used for the master left motor
    private SensorCollection m_leftDriveEncoder;
    //Declares the encoder used for the master right motor
    private SensorCollection m_rightDriveEncoder;

    TalonFXConfiguration m_leftConfig = new TalonFXConfiguration();
    TalonFXConfiguration m_rightConfig = new TalonFXConfiguration();

    //Counter for buying time for the PID
    int m_counter;

    //Declares the solenoids/Pistons as objects which is used to switch between the two gears
    DoubleSolenoid m_leftSolenoid;
    DoubleSolenoid m_rightSolenoid;
    
    //Declares a Gear object to store the gear that we are in
    private Gear m_gear;

   

    /**
     * Constructor for the drivetrain that uses double solenoids to shift speeds/gears
     * @param ahrs the NavX used to instantiate the gyro
     */
    public Drivetrain(TalonFX leftDrive, TalonFX rightDrive, TalonFX leftSlave, TalonFX rightSlave, DoubleSolenoid rightPiston, DoubleSolenoid leftPiston, boolean hasTwoSolenoids){

        //Instantiates the TalonFX motors
        m_masterLeftMotor = leftDrive;
        m_masterRightMotor = rightDrive;
        m_slaveLeftMotor = leftSlave;
        m_slaveRightMotor = rightSlave;

        //Inverts the right drive motors, this is arbitrary and should be based on tests
        m_masterRightMotor.setInverted(true);
        m_slaveRightMotor.setInverted(InvertType.FollowMaster);
        
        //Instantiates the left and right pistons
        m_leftSolenoid = leftPiston;
        m_rightSolenoid = rightPiston;

        //Instantiates the boolean to determine if there are two solenoids
        m_hasTwoSolenoids = hasTwoSolenoids;

        // Initializes classes to call encoders connected to TalonFXs
        m_leftDriveEncoder = new SensorCollection(m_masterLeftMotor);
        m_rightDriveEncoder = new SensorCollection(m_masterRightMotor);

        //Zeros the encoder positions on the drivetrain (connected to TalonFXs)
        m_leftDriveEncoder.setQuadraturePosition(0, 0);
        m_rightDriveEncoder.setQuadraturePosition(0, 0);

        //sets the motors to brake when not given an active command
        m_masterLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_masterRightMotor.setNeutralMode(NeutralMode.Brake);
        m_slaveLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_slaveRightMotor.setNeutralMode(NeutralMode.Brake);

        //configs the drive train to have an acceleration based on the RobotMap constant
        m_masterLeftMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_masterRightMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_slaveLeftMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_slaveRightMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);

        //Sets VistorSPX to follow TalonSRXs output
        m_slaveLeftMotor.follow(m_masterLeftMotor);
        m_slaveRightMotor.follow(m_masterRightMotor);

        //Instatiates the NavX----make sure this is the right port
        m_gyro = new NavX(SerialPort.Port.kMXP);


        //Initializes rotate PID controller with the PIDF constants ----------See if there is a way to add the m_gyro
        //TODO: Come back to this-We need to run characterization to get feedforward
        m_rotController = new PIDController(RobotMap.DRIVETRAIN_GAINS.kP, RobotMap.DRIVETRAIN_GAINS.kI, RobotMap.DRIVETRAIN_GAINS.kD, RobotMap.DRIVETRAIN_GAINS.kF);
        m_rotController.enableContinuousInput(-RobotMap.PID_INPUT_RANGE, RobotMap.PID_INPUT_RANGE);

        m_rotController.setIntegratorRange(-RobotMap.PID_OUTPUT_RANGE, RobotMap.PID_OUTPUT_RANGE);
        m_rotController.setTolerance(RobotMap.TOLERANCE_ROTATE_CONROLLER);

        m_counter = 0;

        m_gear = Gear.kLowGear; 


    }


    /**
     * This should be run in robo init in order to configure the falcons/talons. This method should be called there.
     * Sets up and configs everything on the talons for arcade drive via velcity PID.
     * The PID has to be tuned to make sure they work for this year
     */
    public void configDriveTrain(){
        //Sets all motor conrollers to zero to kill movement
        m_masterLeftMotor.set(ControlMode.PercentOutput, 0);
        m_masterRightMotor.set(ControlMode.PercentOutput, 0);

        //sets all motors to brake
        m_masterLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_masterRightMotor.setNeutralMode(NeutralMode.Brake);

        m_slaveLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_slaveRightMotor.setNeutralMode(NeutralMode.Brake);

        
        /** Feedback Sensor Configuration */

        //Configure the left Talon's selected sensor to a Quad encoder
        m_masterLeftMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, RobotMap.PID_PRIMARY, RobotMap.TIMEOUT_MS);

        // Setup Sum Signal to be used for Distance
        //Feedback Device of Remote Talon
        m_masterRightMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor1, RobotMap.TIMEOUT_MS);

        //Quadrature Encodere of current Talon
        m_masterRightMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.QuadEncoder, RobotMap.TIMEOUT_MS);

        // Setup Difference signal to be used for turn
        m_masterRightMotor.configSensorTerm(SensorTerm.Diff1, FeedbackDevice.RemoteSensor1, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configSensorTerm(SensorTerm.Diff0, FeedbackDevice.QuadEncoder, RobotMap.TIMEOUT_MS);

        //Configure sum [Sum of both QuadEncoders] to be used for Primary PID Index
        m_masterRightMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, RobotMap.TIMEOUT_MS);

        //Scale Feedback by 0.5 to half the sum of Distance
        m_masterRightMotor.configSelectedFeedbackCoefficient(RobotMap.SCALE_FEEDBACK_COEFFICIENT_VALUE, 0, RobotMap.TIMEOUT_MS);

        //Configure Difference [Difference between both QuadEncoders] to be used for Aukiliary PID Index
        m_masterRightMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorDifference, 1, RobotMap.TIMEOUT_MS);

        //Don't scale the Feedback Sensor (use 1 for 1:1 ration)
        m_masterRightMotor.configSelectedFeedbackCoefficient(RobotMap.UNSCALED_FEEDBACK_COEFFICIENT_VALUE, 1, RobotMap.TIMEOUT_MS);

        m_masterRightMotor.setSelectedSensorPosition(0, 0, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setSelectedSensorPosition(0, 1, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.setSelectedSensorPosition(0);

        //Configure output and sensor direction-needs to be tested
        m_masterLeftMotor.setInverted(false);
        m_masterLeftMotor.setSensorPhase(false);
        m_masterRightMotor.setInverted(true);
        m_masterRightMotor.setSensorPhase(true);

        //Set status frame periods to ensure we don't have stale data. 20 and 5 are time in ms
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_10_Targets, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, RobotMap.LEFT_PERIOD_MS, RobotMap.TIMEOUT_MS);

        //Configure neutral deadband
        m_masterRightMotor.configNeutralDeadband(RobotMap.PERCENT_DEADBAND, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.configNeutralDeadband(RobotMap.PERCENT_DEADBAND, RobotMap.TIMEOUT_MS);

        /**
         * Max out the peak output (for all modes).
         * However you can limit the output of a given PID object with
         * configClosedLoopPeakOutput().
         */
        m_masterLeftMotor.configPeakOutputForward(+RobotMap.PEAK_OUTPUT, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.configPeakOutputReverse(-RobotMap.PEAK_OUTPUT, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configPeakOutputForward(+RobotMap.PEAK_OUTPUT, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configPeakOutputReverse(-RobotMap.PEAK_OUTPUT, RobotMap.TIMEOUT_MS);

        //motion magic config
        m_masterRightMotor.configMotionAcceleration(RobotMap.SENSOR_UNIT_PER_100MS_PER_SEC, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configMotionCruiseVelocity(RobotMap.SENSOR_UNIT_PER_100MS_PER_SEC, RobotMap.TIMEOUT_MS);

        //FPID Gains for velocity servo
        m_masterRightMotor.config_kP(0, RobotMap.DRIVETRAIN_GAINS.kP, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kI(0, RobotMap.DRIVETRAIN_GAINS.kI, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kD(0, RobotMap.DRIVETRAIN_GAINS.kD, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kF(0, RobotMap.DRIVETRAIN_GAINS.kF, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_IntegralZone(0, RobotMap.DRIVETRAIN_GAINS.kIzone, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configClosedLoopPeakOutput(0, RobotMap.DRIVETRAIN_GAINS.kPeakOutput, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configAllowableClosedloopError(0, RobotMap.ALLOWABLE_CLOSED_LOOP_ERROR, RobotMap.TIMEOUT_MS);

        //FPID Gains for turn servo
        m_masterRightMotor.config_kP(1, RobotMap.GAINS_TURNING.kP, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kI(1, RobotMap.GAINS_TURNING.kI, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kD(1, RobotMap.GAINS_TURNING.kD, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kF(1, RobotMap.GAINS_TURNING.kF, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_IntegralZone(1, RobotMap.GAINS_TURNING.kIzone, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configClosedLoopPeakOutput(1, RobotMap.GAINS_TURNING.kPeakOutput, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configAllowableClosedloopError(1, RobotMap.ALLOWABLE_CLOSED_LOOP_ERROR, RobotMap.TIMEOUT_MS);

        m_masterRightMotor.configClosedLoopPeriod(0, RobotMap.LOOP_TIME_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configClosedLoopPeriod(1, RobotMap.LOOP_TIME_MS, RobotMap.TIMEOUT_MS);

        //Sets the status frame period to 10ms
        m_masterRightMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_Targets, RobotMap.PERIOD_MS);

        /**
         * false means talon's local output is PID0 + PID1, and other side Talon is PID0 - PID1
         * 
         * if it was true, the talon's local outpu is PID0 - PID1, and other side Talon is PID0 + PID1
         */
        m_masterRightMotor.configAuxPIDPolarity(false, RobotMap.TIMEOUT_MS);

        //sets profile slot for PID
        m_masterRightMotor.selectProfileSlot(0, 0);
        m_masterRightMotor.selectProfileSlot(1, 1);
    }

    /**
     * Sets the drive gear using our pistons. This is private so that it can never be called by an outside class to prevent confusion
     * @param value The value to give to the pistons where kOff removes pressure, kForward is _ gear, a nd kReverse is _ gear
     */
    private void setPistons(DoubleSolenoid.Value value) {
        //sets both solenoids if we have two
        if (m_hasTwoSolenoids) {
            m_leftSolenoid.set(value);
            m_rightSolenoid.set(value);
        }
        //only sets the left solenoid if we have one
        else {
            m_leftSolenoid.set(value);
        }
    }

    /**
     * Sets the drive gear using our pistons
     * <p> The solenoid value is currently arbitrary and needs to be confirmed
     * @param gear
     */
    public void shiftGear(Gear gear) {
        //sets our current gear to the inputted gear
        m_gear = gear;

        //sets our pistons based on what gear we request
        if (m_gear == Gear.kLowGear) {
            setPistons(Value.kForward);
        }
        else if (m_gear == Gear.kHighGear){
            setPistons(Value.kReverse);
        }
    }


    /**
     * @return The gear that we are currently in (kHighGear or kLowGear)
     */
    public Gear getGear(){
        return m_gear;
    }


    /**
     * Drives the drivetrain as a tank, controlling the sides individually
     * <p>Speeds are double values betweem -1.0 and 1.0, where 1.0 is full speed forwards
     * @param leftSpeed The speed for the left half of the drivetrain
     * @param rightSpeed The speed for the right half of the drivetrain
     */
    public void tankDrive (double leftSpeed, double rightSpeed) {
        //Sets power to the motors based on input
        m_masterLeftMotor.set(ControlMode.PercentOutput, leftSpeed);
        m_masterRightMotor.set(ControlMode.PercentOutput, rightSpeed);

        //Sets the slave motors to copy te masters
        m_slaveLeftMotor.follow(m_masterLeftMotor);
        m_slaveRightMotor.follow(m_masterRightMotor);
    
    
    }



    /**
     * Rotates to a set angle without moving forward utilizing the PID and AHRS
     * 
     * @param targetAngle The angle you want the robot to rotate to
     * @return Returns true is the PID returns a value low enough that the robot
     * doesn't move (thus finished)
     */
    public boolean rotateToAngle(double targetAngle) {
        //Flag to check if the method is finished
        boolean isFinished = false;

        //resets the PID only on first entry
        if (m_firstCall) {
            //resets the error
            m_rotController.reset();

            //sets the target to our target angle
            m_rotController.setSetpoint(targetAngle);

            //prevents us from repeating the reset until we run the method again seperately
            m_firstCall = false;

            m_counter = 0;
        }
        //sets our rotate speed to the return of the PID
        double returnedRotate = m_rotController.calculate(m_gyro.getOffsetYaw());

        //Runs he drivetrain with 0 speed and the rotate speed set by the PID
        arcadeDrive(0, returnedRotate);

        // Checks to see if the PID is finished or close enough
        //Needs to be tested and tuned
        if ( ((returnedRotate < RobotMap.FINISHED_PID_THRESHOLD) && (returnedRotate > -RobotMap.FINISHED_PID_THRESHOLD)) && (m_counter > 10)){
            isFinished = true;
            m_firstCall = true;
            System.out.println("FINISHED");
        }

        if (isFinished) {
            m_counter = 0;
        }
        else {
            m_counter++;
        }

        return isFinished;
    }

    
    
    public boolean rotateDriveAngle(double targetAngle, double target) {
        // flag for checking if the method is finished
        boolean isFinished = false;
        
        //The current encoder values
        double m_leftStartingEncoderValue = m_leftDriveEncoder.getQuadraturePosition();
        double m_rightStartingEncoderValue = m_rightDriveEncoder.getQuadraturePosition();

        if (m_rotController.getSetpoint() != targetAngle) {
            //Resets the error
            m_rotController.reset();

            //Sets the target to our target angle
            m_rotController.setSetpoint(targetAngle);
        }

        //Sets our rotate speed to the return of the PID
        double returnedRotate = m_rotController.calculate(m_gyro.getOffsetYaw());

        System.out.println("Returned Rotate: \t" + returnedRotate);

        //Runs the drivetrain with an auto speed of 0.2, and a rotate speed set by the PID
        arcadeDrive(RobotMap.AUTO_SPEED, returnedRotate);

        //TODO: Redue this section-Grab encoder value on first entry(target angle is changed)-get whether going forward or backward-drive forward or backward at set speed until if difference is equal to target distance then stop moving and feed in rotate to get setangle
        /////////////////////////////////////////////////////

        if((m_leftStartingEncoderValue - m_leftDriveEncoder.getQuadraturePosition() > target) && (m_rightStartingEncoderValue - m_rightDriveEncoder.getQuadraturePosition() > target) ){
            arcadeDrive(RobotMap.AUTO_SPEED, 0);
            isFinished = false;
        } else {
            arcadeDrive(0, returnedRotate);
            isFinished = true;
        }

        /**
         * If (target angle has changed){
         * Get forward or backward
         * If(starting encoder value - current encoder value > target distance){
         * drive forward/backward 
         * isFinished = false;
         * }
         * else {
         * stop moving and rotate to getSetAngle
         * isFinished = true;
         * }
         * 
         * }
         */
        ///////////////////////////////////////////////////////
        
        //isFinished acts as an exit flag once we have fulfilled the condiions desired
        return isFinished;
    }

    

    /**
     * An arcade drive using the integrated velocity PID on the talons
     * @param forward -1.0 to 1.0, the speed at which you want the robot to move forward
     * @param turn turn -1.0 to 1.0, the rate of rotation
     * @param setter If this is true, use speed setters to adjust a speed and conserve battery. If false, use raw input
     */
    public void arcadeDrive (double forward, double turn) {
        m_masterLeftMotor.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, +turn);
        m_masterRightMotor.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, -turn);
        m_slaveLeftMotor.follow(m_masterLeftMotor);
        m_slaveRightMotor.follow(m_masterRightMotor);
    }

    /**
     * Returns the encoder position of the drivetrain left side encoder
     * @return The position of the left side encoder
     */
    public int getLeftDriveEncoderPosition(){
        return m_leftDriveEncoder.getQuadraturePosition();
    }

    /**
     * Returns the encoder position of the drivetrain right side encoder
     * @return The position of the right side encoder
     */
    public int getRightDriveEncoderPosition() {
        return m_rightDriveEncoder.getQuadraturePosition();
    }

    /**
     * Returns the encoder velocity of the drivetrain left side encoder
     * @return The velocity of the left side encoder
     */
    public int getLeftDriveEncoderVelocity(){
        return m_leftDriveEncoder.getQuadratureVelocity();
    }

    /**
     * Returns the encoder velocity of the drivetrain right side encoder
     * @return The velocity of the right side encoder
     */
    public int getRightDriveEncoderVelocity(){
        return m_rightDriveEncoder.getQuadratureVelocity();
    }

}