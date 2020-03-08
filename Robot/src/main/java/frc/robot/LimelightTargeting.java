package frc.robot;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * This class should be used to center our robot on the high target.
 * <p>It controls the drivetrain using feedback from the limelight and calculated turn from a PID controller
 * <p>It also currently allows us to tune constants from the shuffleboard
 * @author Josh Overbeek
 * @version 2/1/2020
 */
public class LimelightTargeting {
    //Declare our drivetrain, limelight, and PID controller
    private Drivetrain m_drivetrain;
    private LimelightReader m_limelight;
    private PIDController m_targetController;

    //declare private variables for creating a tab, and setting widgets
    private ShuffleboardTab m_targetingTab;
    /**Network table entry for reading desired P constant off of the shuffleboard */
    private NetworkTableEntry m_pEntry;
    /**Network table entry for reading desired I constant off of the shuffleboard */
    private NetworkTableEntry m_iEntry;
    /**Network table entry for reading desired D constant off of the shuffleboard */
    private NetworkTableEntry m_dEntry;

    /**Network table entry to publish whether we are currently on target */
    private NetworkTableEntry m_onTargetEntry;

    /**
     * Contructor for LimelightTargeting objects
     * @param drivetrain The drivetrain of the robot
     * @param limelight The limelight reader that gives us our target
     */
    public LimelightTargeting(Drivetrain drivetrain, LimelightReader limelight) {
        m_drivetrain = drivetrain;
        m_limelight = limelight;

        //Instatiate a new PID controller with PID values passed in from the robot map
        m_targetController = new PIDController(RobotMap.TARGETING_P, RobotMap.TARGETING_I, RobotMap.TARGETING_D, RobotMap.TARGETING_PERIOD_S);

        //This caps the value of our accumulated error so as not to allow our robot to spiral into increasing oscilaations
        //the minimum is set to the negative maximum in order to give us the same cap on our negative error (overshoot)
        m_targetController.setIntegratorRange(-RobotMap.TARGETING_MAX_ACCUMULATED_ERROR, RobotMap.TARGETING_MAX_ACCUMULATED_ERROR);

        //Sets our tolearble error as defined in the robot map. This should be as minimal as possible
        m_targetController.setTolerance(RobotMap.TARGETING_ERROR_TOLERANCE);
        
        //Sets the values of -180 and 180 degrees to be identical, and makes our input continous so that we dont have to use modulo
        //This should not effect this class, however it is called for redundancy
        m_targetController.enableContinuousInput(-180, 180);

        //sets up the shuffleboard input for targeting
        shuffleboardConfig();
    }

    /**
     * Centers the robot on the target based on values from the limelight
     * <p>This should be called only when not driving the robot seperatly so as to avoid conflicting controls over the drivetrain
     * 
     * @return Whether we are currently within our acceptable error (on target)
     */
    public boolean target() {
        //Only allows the drivetrain to rotate if it currently has any targets
        if (m_limelight.hasTargets()) {
            //Passes in a speed of zero to keep us from moving, and sets the turn speed to the calculated output of the PID
            //The calculate methods passes in our measurement in degrees from the limelight as our offset and sets our setpoint to zero degrees
            //This way the PID controller should target dead center
            double degToTarget = -m_limelight.getRawDegreesToTarget();
            double turn = (m_targetController.calculate(degToTarget, 0)) /45;
            m_drivetrain.arcadeDrive(0, turn);
        }

        //returns whether the PID believes that we are on target
        return onTarget();
    }

    /**
     * Checks whether we are currently on target and sets the shuffleboard value
     * @return whether or not we are within our acceptable error
     */
    public boolean onTarget() {
        //read whether we are on target once
        boolean onTarget = m_targetController.atSetpoint();

        //sets the entry to whether we are on target
        m_onTargetEntry.setBoolean(onTarget);

        //return whether we are currently on target
        return onTarget;
    }

    /**
     * Sets the P, I, and D constants based on shuffleboard input
     * <p>-> THIS SHOULD ONLY BE CALLED IN TEST <-
     */
    public void setPID() {
        //passes in the values off of the shuffleboard Netwrok Table Entries
        m_targetController.setPID(m_pEntry.getDouble(RobotMap.TARGETING_P), m_iEntry.getDouble(RobotMap.TARGETING_I), m_dEntry.getDouble(RobotMap.TARGETING_D));
    }

    public void getPID_Values(){
        System.out.printf("P:%f\tI:%f\tD:%f\t\n",m_targetController.getP(),
        m_targetController.getI(),m_targetController.getD());
        return ;
    }

    /**
     * Reset the PID to RobotMap constants
     * <p>This should be called every time we disable
     */
    public void resetPID() {
        m_targetController.setPID(RobotMap.TARGETING_P, RobotMap.TARGETING_I, RobotMap.TARGETING_D);
    }

    public void resetError() {
        m_targetController.reset();
    }

    /**
     * instantiates all of our network table entries and displays them under the targeting tab
     */
    private void shuffleboardConfig() {
        //creates a tab on the shuffleboard for all our targeting needs
        m_targetingTab = Shuffleboard.getTab("Targeting");
        
        //creates a persistent widget as text for setting P constant
        m_pEntry = m_targetingTab.addPersistent("P", RobotMap.TARGETING_P)       //creates widget with the robotmap constant as a default
                                 .withWidget(BuiltInWidgets.kTextView)           //sets widget to a text view
                                 .withProperties(Map.of("min", -1000, "max", 100.0))   //sets min and max values
                                 .getEntry();   
        
        //creates a persistent widget as text for setting I constant
        m_iEntry = m_targetingTab.addPersistent("I", RobotMap.TARGETING_I)       //creates widget with the robotmap constant as a default
                                 .withWidget(BuiltInWidgets.kTextView)           //sets widget to a text view
                                 .withProperties(Map.of("min",-1000, "max", 100.0))   //sets min and max values
                                 .getEntry();   

        //creates a persistent widget as text for setting D constant
        m_dEntry = m_targetingTab.addPersistent("D", RobotMap.TARGETING_D)       //creates widget with the robotmap constant as a default
                                 .withWidget(BuiltInWidgets.kTextView)           //sets widget to a text view
                                 .withProperties(Map.of("min", -1000, "max", 100.0))   //sets min and max values
                                 .getEntry();  

        //create a widget to display whether we are currently on target
        m_onTargetEntry = m_targetingTab.add("On Target?", false)               //creates the widget that is false by default as by default we are not on target
                                        .withWidget(BuiltInWidgets.kBooleanBox) //set widget to a boolean box to easily display the value
                                        .getEntry();
    }
}