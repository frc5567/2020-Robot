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
        double targetSkew;
        try{
            // Sets a variable equal to the targets skew
            targetSkew = getAdjustedSkew();
        }
        catch (Exception e) {
            //if the skew is invalid, print our that is is invalid
            System.out.println("Invalid Skew Reading");
            //return zero to prevent crazy oscillations
            //TODO: work out a better way to handle this
            return 0;
        }

        // Checks to make sure we have a target
        if (hasTargets()) {

            // Checks skew to see if we can hit the inner target
            if(targetSkew <= RobotMap.INNER_TARGET_DEGREES && targetSkew >= -RobotMap.INNER_TARGET_DEGREES){
                
                // Offset for inner target
                targetAngle *= RobotMap.OFFSET_TARGET_DEGREES;
            }
        }
        return targetAngle;
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
     * Adjusts our skew value so that values to the left range from 0 to -44 
     * and values to the right rangle from 0 to 45
     * @return skew adjusted to match our comprehension
     * @throws Exception get Skew should not be able to throw an exception greater than 0 or less
     * than -90. If it does, we throw this exception
     */
    public double getAdjustedSkew() throws Exception {
        //read our skew as we begin to prevent repeated calls
        double tempSkew = getSkew();

        //if our value is less than or equal to 0 and greater than or equal to -44
        //we do not modify the values as it properly measures offset to the left
        if ((0 >= tempSkew) && (tempSkew >= -44)) {
            return tempSkew;
        }
        //else if skew is greater than or equal to -90,
        //we add 90 to it to balance it at 0 and make positive values represent offset to the right
        else if (tempSkew >= -90) {
            return tempSkew + 90;
        }
        else {
            throw new Exception("Impossible skew value");
        }
    }

    /**
     * @return The vertical offset in degrees from the center of the camera to the target
     */
    public double getYDegreesToTarget() {
        return m_limelightTable.getEntry("ty").getDouble(0);
    }

    /**
     * @param cameraDegreesFromGround How far from horizontal the camera is mounted in degrees. 
     * This should be passed in from a gyro
     * @return The horizontal distance from the robot to the target
     */
    public double getDistance(double cameraDegreesFromGround) {
        //Math.tan() requires radians, hence the RobotMap constant
        double lengthToHeightRatio = Math.tan(RobotMap.DEG_TO_RAD_CONVERSION * (cameraDegreesFromGround + getYDegreesToTarget()));
        return (RobotMap.NET_HEIGHT_INCHES / lengthToHeightRatio);
    }

}