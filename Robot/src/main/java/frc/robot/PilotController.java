package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Drivetrain.Gear;

/**
 * A class to control the drivetrain with the pilot controller
 * @author Josh Overbeek
 * @version 1/25/2020
 */
public class PilotController {
    /**
     * Enum to indicate different control systems
     * <p>Possible values:
     * <li>{@link #kTank}</li>
     * <li>{@link #kArcade}</li>
     */
    public enum DriveType {
        /** 
         * A tank drive system where the user inputs drive speed to the left and right halves
         * of the drivetrain individually
         */
        kTank,

        /**
         * An arcade drive system where the user inputs a linear speed and a rotation speed
         * which controls the whole drivetrain as a unit
         */ 
        kArcade;
    }

    //declare our drivetrain and our controller
    private XboxController m_controller;
    private Drivetrain m_drivetrain;

    private LimelightTargeting m_limelightTargeting;
    private final DriveType m_driveType;

    //scalars and network table entries to scale our input on our drivetrain
    //this is to reduce our speed for driver testing and potentially for comp
    private double m_highGearVelocityScalar = RobotMap.DRIVE_DEFAULT_INPUT_SCALAR;
    private double m_highGearTurnScalar = RobotMap.DRIVE_DEFAULT_INPUT_SCALAR;
    private double m_lowGearVelocityScalar = RobotMap.DRIVE_DEFAULT_INPUT_SCALAR;
    private double m_lowGearTurnScalar = RobotMap.DRIVE_DEFAULT_INPUT_SCALAR;
    private NetworkTableEntry m_highGearVelocityScalarEntry;
    private NetworkTableEntry m_highGearTurnScalarEntry;
    private NetworkTableEntry m_lowGearVelocityScalarEntry;
    private NetworkTableEntry m_lowGearTurnScalarEntry;
    private ShuffleboardTab m_driverTab;

    //these variables allow us to reduce the amount of logic on every cycle
    //we set this value everytime we switch gears
    private double m_currentVelocityScalar = RobotMap.DRIVE_DEFAULT_INPUT_SCALAR;
    private double m_currentTurnScalar = RobotMap.DRIVE_DEFAULT_INPUT_SCALAR;

    /**
     * Creates an object to allow the pilot to control the drivetrain
     * 
     * @param controller The pilot controller to control the drive train with
     * @param drivetrain The robot drivetrain
     * @param driveType The type of drive control that the pilot wants (tank or arcade)
     * @param limelightTargting The targeting object used to lock on to our target
     */
    public PilotController(XboxController controller, Drivetrain drivetrain, DriveType driveType, LimelightTargeting limelightTargeting) {
        m_controller = controller;
        m_drivetrain = drivetrain;
        m_driveType = driveType;
        m_limelightTargeting = limelightTargeting;

        //puts input scalar widgets on the shuffleboard
        shuffleboardConfig();
    }

    /**
     * Creates an object to allow the pilot to control the drivetrain
     * <p>this constructor instantiates its own xbox controller based on the RobotMap port value
     * 
     * @param drivetrain The robot drivetrain
     * @param driveType The type of drive control that the pilot wants (tank or arcade)
     * @param limelightTargting The targeting object used to lock on to our target
     */
    public PilotController(Drivetrain drivetrain, DriveType driveType, LimelightTargeting limelightTargeting) {
        m_drivetrain = drivetrain;
        m_driveType = driveType;
        m_limelightTargeting = limelightTargeting;

        //instantiate xbox controller for controlling the drivetrain
        m_controller = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

        //puts input scalar widgets on the shuffleboard
        shuffleboardConfig();
    }

    /**
     * Refreshes input scalars based on input from shuffleboard
     * <p> The driver input is multiplied by input scalars in order to reduce the speed of the system.
     *     This should be called only while disabled to prevent constant changing of settings mid match
     */
    public void setInputScalar() {
        m_highGearVelocityScalar = m_highGearVelocityScalarEntry.getDouble(RobotMap.DRIVE_DEFAULT_INPUT_SCALAR);
        m_highGearTurnScalar = m_highGearTurnScalarEntry.getDouble(RobotMap.DRIVE_DEFAULT_INPUT_SCALAR);
        m_lowGearVelocityScalar = m_lowGearVelocityScalarEntry.getDouble(RobotMap.DRIVE_DEFAULT_INPUT_SCALAR);
        m_lowGearTurnScalar = m_lowGearTurnScalarEntry.getDouble(RobotMap.DRIVE_DEFAULT_INPUT_SCALAR);
    }

    /**
     * Controls our drivetrain with an arcade control system
     * Triggers are forward and back (left trigger is back, right is forward), left x stick is turn
     */
    private void arcadeDrive() {
        //read our current velocity and turn
        double velocityInput = (m_controller.getTriggerAxis(Hand.kRight) - m_controller.getTriggerAxis(Hand.kLeft));
        double turnInput =  m_controller.getX(Hand.kLeft);

        //adjust our input based on our deadband
        turnInput = adjustForDeadband(turnInput);

        //multiplies our input by our current scalar
        velocityInput *= m_currentVelocityScalar;
        turnInput *= m_currentTurnScalar;

        //run our drivetrain with the adjusted input
        m_drivetrain.arcadeDrive(velocityInput, turnInput);
    }

    /**
     * Controls our drivetrain with a tank control system
     * Left stick y is left side speed, right stick y is right side speed
     */
    private void tankDrive() {
        //read our current stick input
        //inputs are negative because forward on the stick is naturally negative
        //so we invert it to make controls match our worldview
        //we adjust here to make the rest of the method easier to read / debug
        double leftInput =  -m_controller.getY(Hand.kLeft);
        double rightInput =  -m_controller.getY(Hand.kRight);

        //adjust our stick input for the deadband to remove drift
        leftInput = adjustForDeadband(leftInput);
        rightInput = adjustForDeadband(rightInput);

        //scales our left and right sides based on velocity control
        leftInput *= m_currentVelocityScalar;
        rightInput *= m_currentVelocityScalar;

        //run our drivetrain with the adjusted inputs
        m_drivetrain.tankDrive(leftInput, rightInput);
    }

    /**
     * Sets us to high gear on x button input and low gear on y button input
     */
    private void controlGear() {
        if (m_controller.getXButtonPressed()) {
            //set the actual drive gear on the drivetrain
            m_drivetrain.shiftGear(Gear.kHighGear);

            //sets our current scalar to the one used in high gear
            m_currentVelocityScalar = m_highGearVelocityScalar;
            m_currentTurnScalar = m_highGearTurnScalar;
        }
        else if (m_controller.getYButtonPressed()) {
            //set the actual drive gear on the drivetrain
            m_drivetrain.shiftGear(Gear.kLowGear);

            //sets our current scalar to the one used in high gear
            m_currentVelocityScalar = m_lowGearVelocityScalar;
            m_currentTurnScalar = m_lowGearTurnScalar;
        }
    }
    
    /**
     * Controls all pilot controlled systems
     */
    public void controlDriveTrainPeriodic() {
        //if the b button is pressed, lock onto the high target
        if (m_controller.getBButton()) {
            m_limelightTargeting.target();
        }
        //when the b button isn't pressed, run the drive train as normal
        else {
            //runs our drivetrain based on control scheme passed in
            if (m_driveType == DriveType.kArcade) {
                arcadeDrive();
            }
            else if (m_driveType == DriveType.kTank) {
                tankDrive();
            }
        }

        //Controls shifting the gears off of the x and y buttons
        controlGear();
    }

    /**
     * @return The drivetrain that the pilot controller controls
     */
    public Drivetrain getDrivetrain() {
        return m_drivetrain;
    }

    /**
     * Take in input from a stick with drift, remove the drift and then scale the input to remove a jump
     * @param stickInput The direct input from the joystick
     * @return the adjusted value for the deadband
     */
    public double adjustForDeadband(double stickInput) {
        //grab the absolute value of the stick input to reduce comparisons
        double absoluteStickInput = Math.abs(stickInput);

        //if our absolute stick input is withing our deadband, we set it equal to zero and early exit
        if (absoluteStickInput < RobotMap.PILOT_CONTROLLER_STICK_DEADBAND) {
            return 0;
        }
        //otherwise, if we're outside of the deadband
        else {
            //reduce the input of the stick by the deadband to center the output on zero to prevent jumps
            absoluteStickInput -= RobotMap.PILOT_CONTROLLER_STICK_DEADBAND;

            //then we assign the original sign to the modified input
            stickInput = Math.copySign(absoluteStickInput, stickInput);

            //then we output the stick input scaled to cover the whole range of values from 0 to 1
            return stickInput / (1.0 - RobotMap.PILOT_CONTROLLER_STICK_DEADBAND);
        }
    }

    /**
     * instantiates all of our network table entries and displays them under the Driver tab
     * <p>the point of this method is to move the shuffleboard code out of init/constructor
     */
    public void shuffleboardConfig() {
        //Put drive control scalars onto the shuffleboard for editing mid drive
        m_driverTab = Shuffleboard.getTab("Driver Tab");
        m_highGearVelocityScalarEntry = m_driverTab.addPersistent("High Gear Speed Scalar", RobotMap.DRIVE_DEFAULT_INPUT_SCALAR)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry();

        m_highGearTurnScalarEntry = m_driverTab.addPersistent("High Gear Turn Scalar", RobotMap.DRIVE_DEFAULT_INPUT_SCALAR)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry();

        m_lowGearVelocityScalarEntry = m_driverTab.addPersistent("Low Gear Speed Scalar", RobotMap.DRIVE_DEFAULT_INPUT_SCALAR)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry();

        m_lowGearTurnScalarEntry = m_driverTab.addPersistent("Low Gear Turn Scalar", RobotMap.DRIVE_DEFAULT_INPUT_SCALAR)
                                        .withWidget(BuiltInWidgets.kTextView)
                                        .getEntry();
    }
}