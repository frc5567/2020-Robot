package frc.robot;

// imports Motor Controllers, Controller group functions, Basic differenctial drive code, solenoid functions, and functions for getting the joystick values
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.XboxController;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXSensorCollection;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;




public class Drivetrain {
    //Declares the NavX for rotational control
    NavX m_gyro;

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

    //Declares drivetrain object
    private DifferentialDrive m_drivetrain;

    //Counter for buying time for the PID
    int m_counter;

    //Declares the solenoids/Pistons as objects which is used to switch between the two gears
    DoubleSolenoid m_leftSolenoid;
    DoubleSolenoid m_rightSolenoid;
   
    //Declares an enum for determining the position of the double solenoid. 
    public enum Gear{
        kLowGear, 
            kHighGear;

        //see if someone put this here
		public static Object kFirstSpeedetEntry(String string) {
			return null;
		}
    }
    //Declares a Gear object to store the gear that we are in
    private Gear m_gear;

   

    /**
     * Constructor for the drivetrain that uses double solenoids to shift speeds/gears
     * @param ahrs the NavX used to instantiate the gyro
     */
    public Drivetrain(TalonFX leftDrive, TalonFX rightDrive, TalonFX leftSlave, TalonFX rightSlave, DoubleSolenoid rightPiston, DoubleSolenoid leftPiston, boolean hasTwoSolenoids, SerialPort.Port i2c_port_id){

        //Instatiates the motor controllers and their ports
        m_masterLeftMotor = new TalonFX(RobotMap.MASTER_LEFT_FALCON_ID);
        m_masterRightMotor = new TalonFX(RobotMap.MASTER_RIGHT_FALCON_ID);

        m_slaveLeftMotor = new TalonFX(RobotMap.SLAVE_LEFT_FALCON_ID);
        m_slaveRightMotor = new TalonFX(RobotMap.SLAVE_LEFT_FALCON_ID);

        
        m_masterLeftMotor = leftDrive;
        m_masterRightMotor = rightDrive;
        m_slaveLeftMotor = leftSlave;
        m_slaveRightMotor = rightSlave;

        //Inverts the right drive motors, this is arbitrary and should be based on tests
        m_masterRightMotor.setInverted(true);
        m_slaveRightMotor.setInverted(InvertType.FollowMaster);
        
        //instantiates the left double solenoid used for switch gears
        m_leftSolenoid = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.LEFT_SOLENOID_FORWARD_PORT, RobotMap.LEFT_SOLENOID_REVERSE_PORT); //<-We'll need to check the channels to make sure they're right.
        m_rightSolenoid = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.RIGHT_SOLENOID_FORWARD_PORT, RobotMap.RIGHT_SOLENOID_REVERSE_PORT);
        
        m_leftSolenoid = leftPiston;
        m_rightSolenoid = rightPiston;

        m_hasTwoSolenoids = hasTwoSolenoids;

        // Initializes classes to call encoders connected to TalonSRXs
        m_leftDriveEncoder = new SensorCollection(m_masterLeftMotor);
        m_rightDriveEncoder = new SensorCollection(m_masterRightMotor);

        //Zeros the encoder positions on the drivetrain (connected to TalonSRX)
        m_leftDriveEncoder.setQuadraturePosition(0, 0);
        m_rightDriveEncoder.setQuadraturePosition(0, 0);

        //sets the motors to brake when not given an active command
        m_masterLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_masterRightMotor.setNeutralMode(NeutralMode.Brake);

        //configs the drive train to have an acceleration based on the RobotMap constant
        m_masterLeftMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_masterRightMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_slaveLeftMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);
        m_slaveRightMotor.configOpenloopRamp(RobotMap.DRIVE_RAMP_TIME);

        //Sets VistorSPX to follow TalonSRXs output
        m_slaveLeftMotor.follow(m_masterLeftMotor);
        m_slaveRightMotor.follow(m_masterRightMotor);

        //Initializes the XboxConroller in order for the two speed transmission to switch
        m_driveController = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

        //Initializes feedback variables for speed setter and rotate setter
        //Setters use variables as feedback in order to "ramp" the output gradually
        m_currentSpeed = 0;
        m_currentRotate = 0;

        
        //Instatiates the NavX
        m_gyro = new NavX(i2c_port_id);


        //Initializes rotate PID controller with the PIDF constants ----------See if there is a way to add the m_gyro
        m_rotController = new PIDController(RobotMap.DRIVETRAIN_GAINS.kP, RobotMap.DRIVETRAIN_GAINS.kI, RobotMap.DRIVETRAIN_GAINS.kD, RobotMap.DRIVETRAIN_GAINS.kF);
        m_rotController.enableContinuousInput(-RobotMap.PID_INPUT_RANGE, RobotMap.PID_INPUT_RANGE);

        m_rotController.setIntegratorRange(-RobotMap.PID_OUTPUT_RANGE, RobotMap.PID_OUTPUT_RANGE);
        m_rotController.setTolerance(RobotMap.TOLERANCE_ROTATE_CONROLLER);
        m_rotController.disableContinuousInput();

        m_counter = 0;

        m_gear = Gear.kLowGear; 

    }

    /**
     * This should be run in robo init in order o configure the falcons/talons
     * This method will be filled in with our PID config methods
     * ???talonDriveConfig- is it the same thing -ask Josh
     */
    public void configDriveTrain(){

    }


    public void setPistons(DoubleSolenoid.Value value) {
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


    //Creates a function for setting the solenoid 1 (aka forward)
    public void solenoidForward(){
        m_leftSolenoid.set(DoubleSolenoid.Value.kForward);
        m_gear = Gear.kLowGear;
   }

    //Creates a function for setting the solenoid into mode 2 (aka reverse)
    public void solenoidReverse(){
        m_leftSolenoid.set(DoubleSolenoid.Value.kReverse);
        m_gear = Gear.kHighGear;
    }

    // Function for switching between the two solenoid positions. The positions are forward and reverse.
    public void switchSolenoidGear(){
        //If X button it pressed, if the drivetrain is in first gear/speed the gear switches to second gear/speed
        //else the gear/speed switches to first gear/speed
        if(m_driveController.getXButton()){
            if(m_gear == Gear.kLowGear){
                solenoidReverse();
            } else {
                solenoidForward();
            }
        }
    }


    /**
     * Sets the drivetrain motor to desired settings. Acceleration limiter is 
     * implemented to prevent current spikes from puting robot in brownout
     * condition
     * 
     * @param desiredSpeed The desired robot speed along the x-axis [-1.0..1.0] forward 
     * is positive
     * @param desiredRotate The desired robot turning speed along z-axis [-1.0..1.0]
     * clockwise is positive
     */
    public void curvatureDrive(double desiredSpeed, double desiredRotate){
        //If desired speed is higher than current speed by a margin larder than
        //DRIVE_MAX_DELTA_SPEED,
        //Increase current speed by DRIVE_MAX_DELTA_SPEED's amount
        if (desiredSpeed > (m_currentSpeed + RobotMap.DRIVE_MAX_DELTA_SPEED)) {
            m_currentSpeed += RobotMap.DRIVE_MAX_DELTA_SPEED;
        }
        //If desired speed is less than current speed by a margin larger than
        //DRIVE_MAX_DELTA_SPEED
        //Decrease current speed by DRIVE_MAX_DELTA_SPEED's amount
        else if (desiredSpeed < (m_currentSpeed - RobotMap.DRIVE_MAX_DELTA_SPEED)) {
            m_currentSpeed -= RobotMap.DRIVE_MAX_DELTA_SPEED;
        }

        //If desired speed is within DRIVE_MAX_DELTA_SPEED's margin to current speed,
        //set current speed to match desired speed
        else {
            m_currentSpeed = desiredSpeed;
        }

        //If desired rotate is higher than current rotate by a margin larger than
        //DRIVE_MAX_DELTA_SPEED,
        // Increase current rotate by DRIVE_MAX_DELTA_SPEED's amount
        if (desiredRotate > (m_currentRotate + RobotMap.DRIVE_MAX_DELTA_SPEED)) {
            m_currentRotate += RobotMap.DRIVE_MAX_DELTA_SPEED;
        }
        //If desired torate is less than current rotate by a margin larger than
        //DRIVE_MAX_DELTA_SPEED
        //Decrease current rotate by DRIVE_MAX_DELTA_SPEED's amount
        else if (desiredRotate < (m_currentRotate - RobotMap.DRIVE_MAX_DELTA_SPEED)){
            m_currentRotate -= RobotMap.DRIVE_MAX_DELTA_SPEED;
        }
        //If desired rotate is within DRIVE_MAX_DELTA_SPEED's margin to current rotate,
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
        double startingValue = RobotMap.STARTING_TICK_VALUE;
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
                talonArcadeDrive(RobotMap.AUTO_SPEED, returnedRotate, false);
                isFinished = false;
            }
            else {
                talonArcadeDrive(RobotMap.AUTO_SPEED, 0, false);
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
        m_masterRightMotor.configRemoteFeedbackFilter(m_masterLeftMotor.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 1, RobotMap.TIMEOUT_MS);

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

        //Configure output and sensor direction
        m_masterLeftMotor.setInverted(false);
        m_masterLeftMotor.setSensorPhase(true);
        m_masterRightMotor.setInverted(false);
        m_masterRightMotor.setSensorPhase(true);

        //Set status frame periods to ensure we don't have stale data. 20 and 5 are time in ms
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_10_Targets, RobotMap.RIGHT_PERIOD_MS, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, RobotMap.LEFT_PERIOD_MS, RobotMap.TIMEOUT_MS);

        //Configure neutral deadband
        m_masterRightMotor.configNeutralDeadband(0.001, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.configNeutralDeadband(0.001, RobotMap.TIMEOUT_MS);

        /**
         * Max out the peak output (for all modes).
         * However you can limit the output of a given PID object with
         * configClosedLoopPeakOutput().
         */
        m_masterLeftMotor.configPeakOutputForward(+1.0, RobotMap.TIMEOUT_MS);
        m_masterLeftMotor.configPeakOutputReverse(-1.0, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configPeakOutputForward(+1.0, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configPeakOutputReverse(-1.0, RobotMap.TIMEOUT_MS);

        //motion magic config
        m_masterRightMotor.configMotionAcceleration(2000, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configMotionCruiseVelocity(2000, RobotMap.TIMEOUT_MS);

        //FPID Gains for velocity servo
        m_masterRightMotor.config_kP(0, RobotMap.DRIVETRAIN_GAINS.kP, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kI(0, RobotMap.DRIVETRAIN_GAINS.kI, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kD(0, RobotMap.DRIVETRAIN_GAINS.kD, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kF(0, RobotMap.DRIVETRAIN_GAINS.kF, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_IntegralZone(0, RobotMap.DRIVETRAIN_GAINS.kIzone, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configClosedLoopPeakOutput(0, RobotMap.DRIVETRAIN_GAINS.kPeakOutput, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configAllowableClosedloopError(0, 0, RobotMap.TIMEOUT_MS);

        //FPID Gains for turn servo
        m_masterRightMotor.config_kP(1, RobotMap.GAINS_TURNING.kP, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kI(1, RobotMap.GAINS_TURNING.kI, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kD(1, RobotMap.GAINS_TURNING.kD, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_kF(1, RobotMap.GAINS_TURNING.kF, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.config_IntegralZone(1, RobotMap.GAINS_TURNING.kIzone, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configClosedLoopPeakOutput(1, RobotMap.GAINS_TURNING.kPeakOutput, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configAllowableClosedloopError(1, 0, RobotMap.TIMEOUT_MS);

        m_masterRightMotor.configClosedLoopPeriod(0, 10, RobotMap.TIMEOUT_MS);
        m_masterRightMotor.configClosedLoopPeriod(1, 10, RobotMap.TIMEOUT_MS);

        //Sets the status frame period to 10ms
        m_masterRightMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_Targets, 10);

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
     * An arcade drive using the integrated velocity PID on the talons
     * @param forward -1.0 to 1.0, the speed at which you want the robot to move forward
     * @param turn turn -1.0 to 1.0, the rate of rotation
     * @param setter If this is true, use speed setters to adjust aspeed and conserve battery. If false, use raw input
     */
    public void talonArcadeDrive (double forward, double turn, boolean setter) {
        if (setter) {
            //If desired speed is higher than current speed by a margin larger than
            //DRIVE_MAX_DELTA_SPEED,
            //Increase current speed by DRIVE_MAX_DELTA_SPEED's amount
            if (forward > (m_currentSpeed + RobotMap.DRIVE_MAX_DELTA_SPEED)) {
                m_currentSpeed += RobotMap.DRIVE_MAX_DELTA_SPEED;
            }

            //If desired speed is less than current speed by a margin larger than
            //DRIVE_MAX_DELTA_SPEED
            //Decrease current speed by DRIVE_MAX_DELTA_SPEED's amount
            else if (forward < (m_currentSpeed - RobotMap.DRIVE_MAX_DELTA_SPEED)) {
                m_currentSpeed -= RobotMap.DRIVE_MAX_DELTA_SPEED;
            }

            //If desired speed is within DRIVE_MAX_DELTA_SPEED's margin to current speed,
            //Set current Speed to match desired speed
            else {
                m_currentSpeed = forward;
            }


            //If desired rotate is higher than current rotate by a margin larger than
            //DRIVE_MAX_DELTA_SPEED,
            //Increase current rotate by DRIVE_MAX_DELTA_SPEED's amount
            if (turn > (m_currentRotate + RobotMap.DRIVE_MAX_DELTA_SPEED)) {
                m_currentRotate += RobotMap.DRIVE_MAX_DELTA_SPEED;
            }

            //If desired rotate is less than current rotate by a margin larger than
            //DRIVE_MAX_DELTA_SPEED
            // Decrease current rotate by DRIVE_MAX_DELTA_SPEED's amount
            else if (turn < (m_currentRotate - RobotMap.DRIVE_MAX_DELTA_SPEED)) {
                m_currentRotate -= RobotMap.DRIVE_MAX_DELTA_SPEED;
            }

            //If desired rotate is within DRIVE_MAX_DELTA_SPEED's margin to current rotate,
            //Set current rotate to match desired speed
            else {
                m_currentRotate = turn;
            }
            m_masterLeftMotor.set(ControlMode.PercentOutput, m_currentRotate, DemandType.ArbitraryFeedForward, m_currentSpeed);
            m_masterRightMotor.set(ControlMode.PercentOutput, m_currentRotate, DemandType.ArbitraryFeedForward, m_currentSpeed);
        }
        else {
            m_masterLeftMotor.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, turn);
            m_masterRightMotor.set(ControlMode.PercentOutput, forward, DemandType.ArbitraryFeedForward, turn);
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
}