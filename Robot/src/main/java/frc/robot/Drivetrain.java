package frc.robot;

// imports Motor Controllers, Controller group functions, Basic differenctial drive code, solenoid functions, and functions for getting the joystick values
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.XboxController;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;




public class Drivetrain {
    //Declares the NavX for rotational control
    private NavX m_gyro;

    //Declares the Xbox controller used to toggle the double solenoid
    XboxController m_driveController;
    //Declares the turn control PID
    PIDController m_rotController;

    //Declare the flag for checking if this is the first time entering this method in a given run
    boolean m_firstCall = true;

    //This declares the double for the robot's speed when turning along the x axis
    //Used for feedback in speed setter
    double m_currentSpeed;
    //This declares the double for the robot's rotational rate along the y axis
    //Used for feeback in rotate setter
    double m_currentRotate;

    // Declares the boolean to determine if the speed is low enough to enable quick turn
    boolean m_quickTurnEnabled;

    // Declare variable for speed
    double m_speed = 0.0;
    // Declare master left talon
    WPI_TalonSRX m_masterRightMotor;
    // Declare slave left talon
    WPI_TalonSRX m_masterLeftMotor;
    // Declare master right talon
    WPI_VictorSPX m_slaveLeftMotor;
    // Declare slave right talon
    WPI_VictorSPX m_slaveRightMotor;

    //Declares the encoder used for the master left motor
    private SensorCollection m_leftDriveEncoder;
    //Declares the encoder used for the master right motor
    private SensorCollection m_rightDriveEncoder;

    //Declares drivetrain object
    private DifferentialDrive m_drivetrain;

    //Counter for buying time for the PID
    int m_counter;

    //Declares the solenoid/Piston as an object which is used to switch between the two gears
    DoubleSolenoid m_twoSpeedSolenoid;
    
   
    //Declares an enum for determining the position of the double solenoid. 
    public enum Gear{
        kFirstSpeed, kSecondSpeed;
    }
    //Declares a Gear object to store the gear that we are in
    private Gear m_gear;
    

    /**
     * Constructor for the drivetrain that uses double solenoids to shift speeds/gears
     * @param ahrs the NavX used to instantiate the gyro
     */
    public Drivetrain(NavX ahrs){

        //Instatiates the motor controllers and their ports
        m_masterLeftMotor = new WPI_TalonSRX(RobotMap.LEFT_TALON_ID);
        m_masterRightMotor = new WPI_TalonSRX(RobotMap.RIGHT_TALON_ID);
        m_slaveLeftMotor = new WPI_VictorSPX(RobotMap.LEFT_VICTOR_ID);
        m_slaveRightMotor = new WPI_VictorSPX(RobotMap.RIGHT_VICTOR_ID);

        // Initializes classes to call encoders connected to TalonSRXs
        m_leftDriveEncoder = new SensorCollection(m_masterLeftMotor);
        m_rightDriveEncoder = new SensorCollection(m_masterRightMotor);

        //Zeros the encoder positions on the drivetrain (connected to TalonSRX)
        m_leftDriveEncoder.setQuadraturePosition(0, 0);
        m_rightDriveEncoder.setQuadraturePosition(0, 0);

        //Sets VistorSPX to follow TalonSRXs output
        m_slaveLeftMotor.follow(m_masterLeftMotor);
        m_slaveRightMotor.follow(m_masterRightMotor);

        //Initializes the XboxConroller in order for the two speed transmission to switch
        m_driveController = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

        //Initializes the drivetrain with the TalonSRX as the Motors (VictorSPX follows TalonSRX output)
        m_drivetrain = new DifferentialDrive(m_masterLeftMotor, m_masterRightMotor);

        //Initializes feedback variables for speed setter and rotate setter
        //Setters use variables as feedback in order to "ramp" the output gradually
        m_currentSpeed = 0;
        m_currentRotate = 0;

        //Instatiates the NavX
        m_gyro = ahrs;


        //Initializes rotate PID controller with the PIDF constants ----------See if there is a way to add the m_gyro
        m_rotController = new PIDController(RobotMap.DRIVETRAIN_GAINS.kP, RobotMap.DRIVETRAIN_GAINS.kI, RobotMap.DRIVETRAIN_GAINS.kD, RobotMap.DRIVETRAIN_GAINS.kF);
        m_rotController.enableContinuousInput(-RobotMap.PID_INPUT_RANGE, RobotMap.PID_INPUT_RANGE);

        m_rotController.setIntegratorRange(-RobotMap.PID_OUTPUT_RANGE, RobotMap.PID_OUTPUT_RANGE);
        m_rotController.setTolerance(RobotMap.TOLERANCE_ROTATE_CONROLLER);
        m_rotController.disableContinuousInput();

        m_counter = 0;


        //instantiates the double solenoid used for switch gears
        m_twoSpeedSolenoid = new DoubleSolenoid(0, 1); //<-We'll need to check the channels to make sure they're right.
    }

    /**
     * || In place if access is needed to the DifferentialDrive methods ||
     * Gets the drivetrain ogject to use DifferentialDrive methods
     * 
     * @return The drivetrain object (DifferentialDrive)
     */
    public DifferentialDrive getDrivetrain(){
        return m_drivetrain;
    }

    /**
     * Sets the drivetrain motor to desired settings. Acceleration limiter is 
     * implemented to prevent current spikes from puting robot in brownout
     * condition
     * 
     * @param desiredSpeed The desired robot speed along the x-axis [-1.0.1.0] forward 
     * is positive
     * @param desiredRotate The desired robot turning speed along z-axis [-1.0..1.0]
     * clockwise is positive
     */
    public void curvatureDrive(double desiredSpeed, double desiredRotate){
        //If desired speed is higher than current speed by a margin larder than
        //kMaxDeltaSpeed,
        //Increase current speed by kMaxDeltaSpeed's amount
        if (desiredSpeed > (m_currentSpeed + RobotMap.DRIVE_MAX_DELTA_SPEED)) {
            m_currentSpeed += RobotMap.DRIVE_MAX_DELTA_SPEED;
        }
        //If desired speed is less than curren speed by a margin larger than
        //kMaxDeltaSpeed
        //Decrease current speed by kMaxDeltaSpeed's amount
        else if (desiredSpeed < (m_currentSpeed - RobotMap.DRIVE_MAX_DELTA_SPEED)) {
            m_currentSpeed -= RobotMap.DRIVE_MAX_DELTA_SPEED;
        }

        //If desired speed is within kMaxDeltaSpeed's margin to current speed,
        //set current speed to match desired speed
        else {
            m_currentSpeed = desiredSpeed;
        }

        //If desired rotate is higher than current rotate by a margin larger than
        //kMaxDeltaSpeed,
        // Increase current rotate by kMaxDeltaSpeed's amount
        if (desiredRotate > (m_currentRotate + RobotMap.DRIVE_MAX_DELTA_SPEED)) {
            m_currentRotate += RobotMap.DRIVE_MAX_DELTA_SPEED;
        }
        //If desired torate is less than current rotate by a margin larger than
        //kMaxDeltaspeed
        //Decrease current rotate by kMaxDeltaSpeed's amount
        else if (desiredRotate < (m_currentRotate - RobotMap.DRIVE_MAX_DELTA_SPEED)){
            m_currentRotate -= RobotMap.DRIVE_MAX_DELTA_SPEED;
        }
        //If desired rotate is within kMaxDeltaSpeed's margin to current rotate,
        //set current rotate to match desired speed
        else {
            m_currentRotate = desiredRotate;
        }

        //If the curent speed is at the kMaxQuickTurnSpeed quickTurnEnabled is true
        if ((m_currentSpeed < RobotMap.DRIVE_MAX_QUICK_TURN_SPEED) && (m_currentSpeed > RobotMap.DRIVE_MAX_QUICK_TURN_SPEED)) {
            m_quickTurnEnabled = true;
        } else {
            m_quickTurnEnabled = false;
        }

        //Pass current speed, current rotate, and the quick turn boolean into the
        //Differential Drive's curatureDrive method
        m_drivetrain.curvatureDrive(m_currentSpeed, m_currentRotate, m_quickTurnEnabled);
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

            //Enables the PID with the minimum and maximum input values
            m_rotController.enableContinuousInput(-RobotMap.PID_INPUT_RANGE, RobotMap.PID_INPUT_RANGE);

            //sets the target to our target angle
            m_rotController.setSetpoint(targetAngle);

            //prevents us from repeating the reset until we run the method again seperately
            m_firstCall = false;

            m_counter = 0;
        }
        //sets our rotate speed to the return of the PID
        double returnedRotate = m_rotController.calculate(m_gyro.getOffsetYaw());

        //Runs he drivetrain with 0 speed and the rotate speed set by the PID
        talonArcadeDrive(0, returnedRotate, false);

        // Checks to see if he PID is finished or close enough
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

        if (m_firstCall) {
            // Resets the error
            m_rotController.reset();

            //Enables the PID with the minimun and maximum input
            m_rotController.enableContinuousInput(-RobotMap.PID_INPUT_RANGE, RobotMap.PID_INPUT_RANGE);
            // Prevents repeating the reset until the method is run again seperately
            m_firstCall = false;
        }

        if (m_rotController.getSetpoint() != targetAngle) {
            //Resets the error
            m_rotController.reset();

            //Enables the PID with the minimum and maximum input values
            m_rotController.enableContinuousInput(-RobotMap.PID_INPUT_RANGE, RobotMap.PID_INPUT_RANGE);

            //Sets the target to our target angle
            m_rotController.setSetpoint(targetAngle);
        }

        //Sets our rotate speed to the reurn of the PID
        double returnedRotate = m_rotController.calculate(m_gyro.getOffsetYaw());

        System.out.println("Returned Rotate: \t" + returnedRotate);

        //Runs the drivetrain with an auto speed of 0.2, and a rotate speed set by the PID
        talonArcadeDrive(RobotMap.AUTO_SPEED, returnedRotate, false);

        /////////////////////////////////////////////////////
        double startingValue = 1440;
        double leftDiffValue;
        double rightDiffValue;
        if ((m_leftDriveEncoder.getQuadraturePosition() < startingValue) && (m_rightDriveEncoder.getQuadraturePosition() < startingValue)){
            double leftValue = m_leftDriveEncoder.getQuadratureVelocity();
            double rightValue = m_rightDriveEncoder.getQuadraturePosition();

            leftDiffValue = startingValue - leftValue;
            rightDiffValue = startingValue - rightValue;

            double leftInches = leftDiffValue / RobotMap.DRIVE_TICS_PER_INCH;
            double rightInches = rightDiffValue / RobotMap.DRIVE_TICS_PER_INCH;

            if ( leftInches > target && rightInches > target) {
                talonArcadeDrive(0.2, returnedRotate, false);
                isFinished = false;
            }
            else {
                talonArcadeDrive(0.2, 0, false);
             isFinished = true;
        }
        
        
        }
        ///////////////////////////////////////////////////////
        

        //isFinished acts as an exit flag once we have fulfilled the condiions desired
        return isFinished;
    }

    /**
     * Runs the teleop init section of the sample code. This method should be called there.
     * Sets up and configs everything on the talons for arcade drive via velcity PID.
     */

    public void talonDriveConfig(){
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
        m_masterLeftMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, RobotMap.PID_PRIMARY, RobotMap.TIMEOUT_MS);

        //Configure the Remote Talon's selected sensor as a remote for the right Talon
        m_masterRightMotor.configRemoteFeedbackFilter(m_masterLeftMotor.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 1, 30);

        // Setip Sum Signal to be used for Distance
        //Feedback Device of Remote Talon
        m_masterRightMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor1, 30);

        //Quadrature Encodere of current Talon
        m_masterRightMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.QuadEncoder, 30);

        // Setup Difference signal to be used for turn
        m_masterRightMotor.configSensorTerm(SensorTerm.Diff1, FeedbackDevice.RemoteSensor1, 30);
        m_masterRightMotor.configSensorTerm(SensorTerm.Diff0, FeedbackDevice.QuadEncoder, 30);

        //Configure sum [Sum of both QuadEncoders] to be used for Primary PID Index
        m_masterRightMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, 30);

        //Scale Feedback by 0.5 to half the sum of Distance
        m_masterRightMotor.configSelectedFeedbackCoefficient(0.5, 0, 30);

        //Configure Difference [Difference between both QuadEncoders] to be used for Aukiliary PID Index
        m_masterRightMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorDifference, 1, 30);

        //Don't scale the Feedback Sensor (use 1 for 1:1 ration)
        m_masterRightMotor.configSelectedFeedbackCoefficient(1, 1, 30);

        m_masterRightMotor.setSelectedSensorPosition(0, 0, 30);
        m_masterRightMotor.setSelectedSensorPosition(0, 1, 30);
        m_masterLeftMotor.setSelectedSensorPosition(0);

        //Configure output and sensor direction
        m_masterLeftMotor.setInverted(false);
        m_masterLeftMotor.setSensorPhase(true);
        m_masterRightMotor.setInverted(false);
        m_masterRightMotor.setSensorPhase(true);

        //Set status frame periods to ensure we don't have stale data. 20 and 5 are time in ms
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, 30);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, 30);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, 30);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20, 30);
        m_masterLeftMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, 30);

        //Configure neutral deadband
        m_masterRightMotor.configNeutralDeadband(0.001, 30);
        m_masterLeftMotor.configNeutralDeadband(0.001, 30);

        /**
         * Max out the peak output (for all modes).
         * However you can limit the output of a given PID object with
         * configClosedLoopPeakOutput().
         */
        m_masterLeftMotor.configPeakOutputForward(+1.0, 30);
        m_masterLeftMotor.configPeakOutputReverse(-1.0, 30);
        m_masterRightMotor.configPeakOutputForward(+1.0, 30);
        m_masterRightMotor.configPeakOutputReverse(-1.0, 30);

        //motion magic config
        m_masterRightMotor.configMotionAcceleration(2000, 30);
        m_masterRightMotor.configMotionCruiseVelocity(2000, 30);

        //FPID Gains for velocity servo
        m_masterRightMotor.config_kP(0, RobotMap.DRIVETRAIN_GAINS.kP, 30);
        m_masterRightMotor.config_kI(0, RobotMap.DRIVETRAIN_GAINS.kI, 30);
        m_masterRightMotor.config_kD(0, RobotMap.DRIVETRAIN_GAINS.kD, 30);
        m_masterRightMotor.config_kF(0, RobotMap.DRIVETRAIN_GAINS.kF, 30);
        m_masterRightMotor.config_IntegralZone(0, RobotMap.DRIVETRAIN_GAINS.kIzone, 30);
        m_masterRightMotor.configClosedLoopPeakOutput(0, RobotMap.DRIVETRAIN_GAINS.kPeakOutput, 30);
        m_masterRightMotor.configAllowableClosedloopError(0, 0,30);

        //FPID Gains for turn servo
        m_masterRightMotor.config_kP(1, RobotMap.GAINS_TURNING.kP, 30);
        m_masterRightMotor.config_kI(1, RobotMap.GAINS_TURNING.kI, 30);
        m_masterRightMotor.config_kD(1, RobotMap.GAINS_TURNING.kD, 30);
        m_masterRightMotor.config_kF(1, RobotMap.GAINS_TURNING.kF, 30);
        m_masterRightMotor.config_IntegralZone(1, RobotMap.GAINS_TURNING.kIzone, 30);
        m_masterRightMotor.configClosedLoopPeakOutput(1, RobotMap.GAINS_TURNING.kPeakOutput, 30);
        m_masterRightMotor.configAllowableClosedloopError(1, 0, 30);

        m_masterRightMotor.configClosedLoopPeriod(0, 10, 30);
        m_masterRightMotor.configClosedLoopPeriod(1, 10, 30);

        //Sets the status frame period to 10ms
        m_masterRightMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_Targets, 10);

        /**
         * false means talon's local output is PID0 + PID1, and other side Talon is PID0 - PID1
         * 
         * if it was true, the talon's local outpu is PID0 - PID1, and other side Talon is PID0 + PID1
         */
        m_masterRightMotor.configAuxPIDPolarity(false, 30);

        //sets profile slot for PID
        m_masterRightMotor.selectProfileSlot(0, 0);
        m_masterRightMotor.selectProfileSlot(1, 1);
    }

    /**
     * An arcade drive using the integrated velocity PID on the talons
     * @param forward -1.0 to 1.0, the speed at which you want the robot to move forward
     * @param turn turn -1.0 to 1.0, the rate of rotation
     * @param setter If this is true, use speed setters to adjust aspeed and conserve battery. If false, use raw input
     */
    public void talonArcadeDrive (double forward, double turn, boolean setter) {
        if (setter) {
            //If desired speed is higher than current speed by a margin larger than
            //kMaxdeltaSpeed,
            //Increase current speed by kMaxDeltaSpeed's amount
            if (forward > (m_currentSpeed + 0.1)) {
                m_currentSpeed += 0.1;
            }

            //If desired speed is less than current speed by a margin larger than
            //kMaxDeltaSpeed
            //Decrease current speed by kMaxDeltaSpeed's amount
            else if (forward < (m_currentSpeed - 0.1)) {
                m_currentSpeed -= 0.1;
            }

            //If desired speed is within kMaxDeltaSpeed's margin to current speed,
            //Set current Speed to match desired speed
            else {
                m_currentSpeed = forward;
            }


            //If desired rotate is higher than current rotate by a margin larger than
            //kMaxDeltaSpeed,
            //Increase current rotate by kMaxDeltaSpeed's amount
            if (turn > (m_currentRotate + 0.1)) {
                m_currentRotate += 0.1;
            }

            //If desired rotate is less than current rotate by a margin larger than
            //kMaxDeltaSpeed
            // Decrease current rotate by kMaxDeltaSpeed's amount
            else if (turn < (m_currentRotate - 0.1)) {
                m_currentRotate -= 0.1;
            }

            //If desired rotate is within kMaxDeltaSpeed's margin to current rotate,
            //Set current rotate to match desired speed
            else {
                m_currentRotate = turn;
            }
            m_masterLeftMotor.set(ControlMode.PercentOutput, m_currentRotate, DemandType.ArbitraryFeedForward, +m_currentSpeed);
            m_masterRightMotor.set(ControlMode.PercentOutput, m_currentRotate, DemandType.ArbitraryFeedForward, -m_currentSpeed);
        }
        else {
            m_masterLeftMotor.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, +turn);
            m_masterRightMotor.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, -turn);
        }
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



    //Creates a function for setting the solenoid 1 (aka forward)
    public void solenoidForward(){
        m_twoSpeedSolenoid.set(DoubleSolenoid.Value.kForward);
        m_gear = Gear.kFirstSpeed;
   }
    //Creates a function for setting the solenoid into mode 2 (aka reverse)
    public void solenoidReverse(){
        m_twoSpeedSolenoid.set(DoubleSolenoid.Value.kReverse);
        m_gear = Gear.kSecondSpeed;
    }

    // Function for switching between the two solenoid positions. The positions are forward and reverse.
    public void switchSolenoidGear(){
        //If X button it pressed, if the drivetrain is in first gear/speed the gear switches to second gear/speed
        //else the gear/speed switches to first gear/speed
        if(m_driveController.getXButton()){
            if(m_gear == Gear.kFirstSpeed){
                solenoidReverse();
            } else {
                solenoidForward();
            }
        }
    }
}