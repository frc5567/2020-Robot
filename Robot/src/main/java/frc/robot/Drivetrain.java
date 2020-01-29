package frc.robot;

// imports Motor Controllers, Controller group functions, Basic differenctial drive code, solenoid functions, and functions for getting the joystick values
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first. wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
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
import com.ctre.phoenix.motorcontrol.FollowerType;

import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Ultrasonic.Unit;


public class Drivetrain  {
    private NavX m_gyro;

  XboxController m_driveController;

    boolean m_firstCall = true;

    double m_currentSpeed;
    double m_currentRotate;

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

    private SensorCollection m_leftDriveEncoder;
    private SensorCollection m_rightDriveEncoder;

    //Declares drivetrain object
    private DifferentialDrive m_drivetrain;

    private Ultrasonic ultraLeft;
    private Ultrasonic ultraRight;

    int m_counter;

    //Declares the solenoid as an object
    DoubleSolenoid m_twoSpeedSolenoid;
    
    //Declares a boolean for determining the position of the double solenoid. True = Forward  False = Reverse
    boolean SOLENOID_POSITION;
    
    public final double kP;
    public final double kI;
    public final double kD;
    public final double kF;
    public final int kIzone;
    public final double kPeakOutput;
    
    

    public Drivetrain(NavX ahrs){

        //Instatiates the motor controllers
        m_masterLeftMotor = new WPI_TalonSRX(1);
        m_masterRightMotor = new WPI_TalonSRX(2);
        m_slaveLeftMotor = new WPI_VictorSPX(3);
        m_slaveRightMotor = new WPI_VictorSPX(4);

        m_leftDriveEncoder = new SensorCollection(m_masterLeftMotor);
        m_rightDriveEncoder = new SensorCollection(m_masterRightMotor);

        m_leftDriveEncoder.setQuadraturePosition(0, 0);
        m_rightDriveEncoder.setQuadraturePosition(0, 0);

        m_slaveLeftMotor.follow(m_masterLeftMotor);
        m_slaveRightMotor.follow(m_masterRightMotor);

        m_drivetrain = new DifferentialDrive(m_masterLeftMotor, m_masterRightMotor);

        m_currentSpeed = 0;
        m_currentRotate = 0;

        m_gyro = ahrs;

        ultraLeft = new Ultrasonic(2, 1);
        ultraRight = new Ultrasonic(4, 3);
        ultraLeft.setEnabled(true);
        ultraLeft.setAutomaticMode(true);
        ultraLeft.setDistanceUnits(Unit.kInches);
        ultraRight.setEnabled(true);
        ultraRight.setAutomaticMode(true);
        ultraRight.setDistanceUnits(Unit.kInches);

        //Declares turn control PID----------Chech to see if correct
    PIDController m_rotController = new PIDController(kP, kI, kD,kIzone, kPeakOutput);
        m_rotController.setInputRange(-180.00, 180.00);

        m_rotController.setOutputRange(-0.5, 0.5);
        m_rotController.setAbsoluteTolerance(2);
        m_rotController.setContinuous();
        m_rotController.disable();

        m_counter = 0;


        //instantiates the double solenoid
        m_twoSpeedSolenoid = new DoubleSolenoid(0, 1); //<-We'll need to check the channels to make sure they're right.

    }

    public DifferentialDrive getDrivetrain(){
        return m_drivetrain;
    }

    public void curvatureDrive(double desiredSpeed, double desiredRotate){
        if (desiredSpeed > (m_currentSpeed + 0.1)) {
            m_currentSpeed += 0.1;
        }

        else if (desiredSpeed < (m_currentSpeed - 0.1)) {
            m_currentSpeed -= 0.1;
        }

        else {
            m_currentSpeed = desiredSpeed;
        }



        if (desiredRotate > (m_currentRotate + 0.1)) {
            m_currentRotate += 0.1;
        }

        else if (desiredRotate < (m_currentRotate - 0.1)){
            m_currentRotate -= 0.1;
        }

        else {
            m_currentRotate = desiredRotate;
        }


        if ((m_currentSpeed < 0.1) && (m_currentSpeed > 0.1)) {
            m_quickTurnEnabled = true;
        } else {
            m_quickTurnEnabled = false;
        }

        m_drivetrain.curvatureDrive(m_currentSpeed, m_currentRotate, m_quickTurnEnabled);
    }

    public boolean rotateToAngle(double targetAngle) {
        boolean isFinished = false;

        if (m_firstCall) {
            m_rotController.reset();

            m_rotController.enable();

            m_rotController.setSetpoint(targetAngle);

            m_firstCall = false;

            m_counter = 0;
        }

        double returnedRotate = m_rotController.get();

        talonArcadeDrive(0, returnedRotate, false);


        if ( ((returnedRotate < 0.15) && (returnedRotate > -0.15)) && (m_counter > 10)){
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
        boolean isFinished = false;

        if (m_firstCall) {
            m_rotController.reset();

            m_rotController.enable();

            m_firstCall = false;
        }

        if (m_rotController.getSetpoint() != targetAngle) {
            m_rotController.reset();

            m_rotController.enable();

            m_rotController.setSetpoint(targetAngle);
        }

        double returnedRotate = m_rotController.get();

        System.out.println("Returned Rotate: \t" + returnedRotate);

        talonArcadeDrive(0.2, returnedRotate, false);

        if (ultraLeft.getRangeInches() > target && ultraRight.getRangeInches() > target) {
            talonArcadeDrive(0.2, returnedRotate, false);
            isFinished = false;
        }
        else {
            talonArcadeDrive(0.2, 0, false);
            isFinished = true;
        }

        return isFinished;
    }


    public void talonDriveConfig(){

        m_masterLeftMotor.set(ControlMode.PercentOutput, 0);
        m_masterRightMotor.set(ControlMode.PercentOutput, 0);

        m_masterLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_masterRightMotor.setNeutralMode(NeutralMode.Brake);

        m_slaveLeftMotor.setNeutralMode(NeutralMode.Brake);
        m_slaveRightMotor.setNeutralMode(NeutralMode.Brake);


        m_masterLeftMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 30);

        m_masterRightMotor.configRemoteFeedbackFilter(m_masterLeftMotor.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 1, 30);


        m_masterRightMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor1, 30);

        m_masterRightMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.QuadEncoder, 30);

        m_masterRightMotor.configSensorTerm(SensorTerm.Diff1, FeedbackDevice.RemoteSensor1, 30);
        m_masterRightMotor.configSensorTerm(SensorTerm.Diff0, FeedbackDevice.QuadEncoder, 30);

        m_masterRightMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, 30);

        m_masterRightMotor.configSelectedFeedbackCoefficient(0.5, 0, 30);

        m_masterRightMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorDifference, 1, 30);

        m_masterRightMotor.configSelectedFeedbackCoefficient(1, 1, 30);

        m_masterRightMotor.setSelectedSensorPosition(0, 0, 30);
        m_masterRightMotor.setSelectedSensorPosition(0, 1, 30);
        m_masterLeftMotor.setSelectedSensorPosition(0);

        m_masterLeftMotor.setInverted(false);
        m_masterLeftMotor.setSensorPhase(true);
        m_masterRightMotor.setInverted(false);
        m_masterRightMotor.setSensorPhase(true);

        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, 30);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, 30);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, 30);
        m_masterRightMotor.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20, 30);
        m_masterLeftMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, 30);

        m_masterRightMotor.configNeutralDeadband(0.001, 30);
        m_masterLeftMotor.configNeutralDeadband(0.001, 30);


        m_masterLeftMotor.configPeakOutputForward(+1.0, 30);
        m_masterLeftMotor.configPeakOutputReverse(-1.0, 30);
        m_masterRightMotor.configPeakOutputForward(+1.0, 30);
        m_masterRightMotor.configPeakOutputReverse(-1.0, 30);

        m_masterRightMotor.configMotionAcceleration(2000, 30);
        m_masterRightMotor.configMotionCruiseVelocity(2000, 30);


        m_masterRightMotor.config_kP(0, RobotMap.DRIVETRAIN_GAINS.kP, 30);
        m_masterRightMotor.config_kI(0, RobotMap.DRIVETRAIN_GAINS.kI, 30);
        m_masterRightMotor.config_kD(0, RobotMap.DRIVETRAIN_GAINS.kD, 30);
        m_masterRightMotor.config_kf(0, RobotMap.DRIVETRAIN_GAINS.kF, 30);
        m_masterRightMotor.config_IntegralZone(0, RobotMap.DRIVETRAIN_GAINS.kPeakOutput, 30);
        m_masterRightMotor.configAllowableClosedloopError(0, 0,30);

        m_masterRightMotor.config_kP(1, RobotMap.GAINS_TURNING.kP, 30);
        m_masterRightMotor.config_kI(1, RobotMap.GAINS_TURNING.kI, 30);
        m_masterRightMotor.config_kD(1, RobotMap.GAINS_TURNING.kD, 30);
        m_masterRightMotor.config_kF(1, RobotMap.GAINS_TURNING.kF, 30);
        m_masterRightMotor.config_IntegralZone(1, RobotMap.GAINS_TURNING.kIzone, 30);
        m_masterRightMotor.configClosedLoopPeakOutput(1, RobotMap.GAINS_TURNING.kPeakOutput, 30);
        m_masterRightMotor.configAllowableClosedloopError(1, 0, 30);

        m_masterRightMotor.configClosedLoopPeriod(0, 10, 30);
        m_masterRightMotor.configClosedLoopPeriod(1, 10, 30);

        m_masterRightMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_Targets, 10);


        m_masterRightMotor.configAuxPIDPolarity(false, 30);

        m_masterRightMotor.selectProfileSlot(0, 0);
        m_masterRightMotor.selectProfileSlot(1, 1);
    }

    public void talonArcadeDrive (double forward, double turn, boolean setter) {
        if (setter) {
            if (forward > (m_currentSpeed + 0.1)) {
                m_currentSpeed += 0.1;
            }

            else if (forward < (m_currentSpeed - 0.1)) {
                m_currentSpeed -= 0.1;
            }

            else {
                m_currentSpeed = forward;
            }


            if (turn > (m_currentRotate + 0.1)) {
                m_currentRotate += 0.1;
            }

            else if (turn < (m_currentRotate - 0.1)) {
                m_currentRotate -= 0.1;
            }

            else {
                m_currentRotate = turn;
            }
            m_masterLeftMotor.set(ControlMode.PercentOutput, m_currentRotate, DemandType.ArbitraryFeedForward, +m_currentSpeed);
            m_masterRightMotor.set(ControlMode.PercentOutput, m_currentRotate, DemandType.ArbitraryFeedForward, -m_currentSpeed);
        }
        else {
            m_masterLeftMotor.set(ControlMode.PercentOutput, turn, DemandType.ArbitraryFeedForward, +forward);
            m_masterRightMotor.set(ControlMode.PercentOutput, turn, DemandType.ArbitraryFeedForward, -forward);
        }
    }

    private double inToTics(double inches){
        return inches*(4096 / (6*3.14159265359));
    }

    //do we need?
    public Ultrasonic getLeftUltra() {
        return ultraLeft;
    }

    //do we need?
    public Ultrasonic getRightUltra() {
        return ultraRight;
    }

    public int getRightDriveEncoderPosition() {
        return m_rightDriveEncoder.getQuadraturePosition();
    }

    public int getLeftDriveEncoderVelocity(){
        return m_leftDriveEncoder.getQuadratureVelocity();
    }

    public int getRightDriveEncoderVelocity(){
        return m_rightDriveEncoder.getQuadratureVelocity();
    }



       //Creates a function for setting the solenoid 1 (aka forward)
       public void solenoidForward(){
           m_twoSpeedSolenoid.set(DoubleSolenoid.Value.kForward);
           SOLENOID_POSITION = true;
       }
       //Creates a function for setting the solenoid into mode 2 (aka reverse)
       public void solenoidReverse(){
            m_twoSpeedSolenoid.set(DoubleSolenoid.Value.kReverse);
            SOLENOID_POSITION = false;
       }

       // Function for switching between the two solenoid positions. The positions are forward and reverse.
       public void switchSolenoidGear(){
           if(m_driveController.getXButton()){
               if(SOLENOID_POSITION == true){
                    solenoidReverse();
               } else {
                    solenoidForward();
               }
           }
        }

}