package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * This class has methods to make reading information from the network table easier and more readable
 * @version 1/27/2020
 * @author Josh Overbeek
 */
public class LimelightReader {
    //declares the network table for limelight info so that we can access it
    private NetworkTable m_limelightTable;

    /**
     * Constructor for our limelight reader object
     */
    public LimelightReader() {
        //pull the network table that the limelight publishes data to to a specific variable
        m_limelightTable = NetworkTableInstance.getDefault().getTable("limelight");
    }

    /**
     * Forces the LEDs to turn off on the limelight
     */
    public void disableLEDs() {
        m_limelightTable.getEntry("ledMode").setDouble(1d);
    }

    /**
     * Restores the LED to pipeline control
     */
    public void enableLEDs() {
        m_limelightTable.getEntry("ledMode").setDouble(0d);
    }

    /**
     * @return Whether or not the limelight sees any valid targets
     */
    public boolean hasTargets() {
        return (m_limelightTable.getEntry("tv").getDouble(0) == 1d);
    }

    /**
     * @return The unmodified degrees to target in the X direction
     */
    public double getRawDegreesToTarget() {
        return m_limelightTable.getEntry("tx").getDouble(0);
    }

    /**TODO:Correct the inner target offset and the degrees to hit inner target
     * This method should adjust the x degrees to target based on target's yaw
     * <p>This is designed to hit the inner target when shooting at an angle.
     * <p>Note that we should only adjust within a certain window, past that we should just target the center of mass
     * @return The adjusted degrees to the inner target
     */
    public double getModifiedDegreesToTarget() {
        // Sets targetAngle to tx, the degrees off from center in the x direction
        //this is inverted to play nice with our PID
        //Since our PID treats the returned angle as our "current" and zero as our setpoint,
        //we have to invert the angle to make the PID turn the robot in the right direction
        double targetAngle = -getRawDegreesToTarget();
        
        // Sets a variable equal to the targets skew
        double targetSkew = getSkew();

        // Checks to make sure we have a target
        if (hasTargets() == true) {

                // Checks skew to see if we can hit the inner target
                if(targetSkew <= -90 + RobotMap.INNER_TARGET_DEGREES && targetSkew >= 0 - RobotMap.INNER_TARGET_DEGREES){
                    
                    // Offset for inner target
                    targetAngle *= RobotMap.OFFSET_TARGET_DEGREES;
                    
                    // Test print outs
                    // System.out.println("Inner Target");
                }
                else {
                    // System.out.println("Outer Target");
                }
        }
        return targetAngle;
    }

    public double offset() {
        double targetSkew = getSkew();
        // Checks to make sure we have a target
        if (hasTargets() == true) {

            // Checks skew to see if we can hit the inner target
            if(targetSkew <= -90 + RobotMap.INNER_TARGET_DEGREES){
                
                // Offset for inner target
                return RobotMap.OFFSET_TARGET_DEGREES;
                
            }
            else if (targetSkew >= 0 - RobotMap.INNER_TARGET_DEGREES) {
                return -RobotMap.OFFSET_TARGET_DEGREES;
            }
        }
        return 0;
    }

    /**
     * @return The skew of the target in degrees, only returns negative values
     * 0 to -20 degrees is the range the target is to the left of us
     *  -90 to -70 degrees is range the target is to the right of us
     */
    public double getSkew() {
        return m_limelightTable.getEntry("ts").getDouble(0);
    }

    /**
     * @return The vertical offset in degrees from the center of the camera to the target
     */
    public double getYDegreesToTarget() {
        return m_limelightTable.getEntry("ty").getDouble(0);
    }

    /**
     * @param cameraDegreesFromGround How far from horizontal the camera is mounted in degrees. This should be passed in from a gyro
     * @return The horizontal distance from the robot to the target
     */
    public double getDistance(double cameraDegreesFromGround) {
        //The Pi/180 calc is a conversion from degrees to radians so that the Math.tan() method returns the correct value
        double lengthToHeightRatio = Math.tan(RobotMap.DEG_TO_RAD_CONVERSION * (cameraDegreesFromGround + getYDegreesToTarget()));
        return (RobotMap.NET_HEIGHT_INCHES / lengthToHeightRatio);
    }
    // creates a enum for our pipeline modes
    // creates a enum for our pipeline modes
    public enum Pipeline{
        kStandard,
        kZoomX2,
        kZoomX3,
        kDriver;
    }
    // declare object to store pipeline
    public Pipeline m_pipeline;
    public void setPipeline(Pipeline pipeline){

        m_pipeline = pipeline;

        // changes pipeline mode depending on what we set it to
        if(m_pipeline == Pipeline.kStandard){
            m_limelightTable.getEntry("pipeline").setNumber(0);
        }
        else if(m_pipeline == Pipeline.kZoomX2){
            m_limelightTable.getEntry("pipeline").setNumber(1);
        }
        else if(m_pipeline == Pipeline.kZoomX3){
            m_limelightTable.getEntry("pipeline").setNumber(2);
        }
        else if(m_pipeline == Pipeline.kDriver){
            m_limelightTable.getEntry("pipeline").setNumber(3);
        }
    }
    
}