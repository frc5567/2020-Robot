package frc.robot;

import edu.wpi.first.networktables.NetworkTable;

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
     * @param limelightTable The network table that stores limelight data
     */
    public LimelightReader(NetworkTable limelightTable) {
        m_limelightTable = limelightTable;
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
       // return m_limelightTable.getEntry("tx").getDouble(0);
        double targetAngle;
        // Checks to make sure we have a target
        if (hasTargets() == true) {
            // Gets skew and checks if target is turned to the right
            if (getSkew() <= -70) {
                System.out.println("right");
                // Checks skew to see if we can hit the inner target
                if(getSkew() <= -85.5){
                    System.out.println("right inner");
                    // offset left for inner target
                    targetAngle = getRawDegreesToTarget()*RobotMap.OFFSET_TARGET_DEGREES;
                }
                else targetAngle = getRawDegreesToTarget(); //target center of target
            }
            // Gets skew and checks if target is turned to the left
            else if (getSkew() >= -20) {
                System.out.println("left");
                // Checks skew to see if we can hit the inner target
                if(getSkew() >= -4.5){
                    System.out.println("left inner");
                    // offset right for inner target
                    targetAngle = getRawDegreesToTarget()*RobotMap.OFFSET_TARGET_DEGREES;
                }
                else targetAngle = getRawDegreesToTarget(); //target center of target
            }
        }else System.out.println("None");
        return targetAngle;
    }

    

    /**
     * TODO: This value needs to be further researched/investigated
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
        //calculates and reports the distance from the robot to the base of the target
        double netHeight = (RobotMap.TARGET_HEIGHT_INCHES - RobotMap.CAMERA_HEIGHT_INCHES);

        //The Pi/180 calc is a conversion from degrees to radians so that the Math.tan() method returns the correct value
        double lengthToHeightRatio = Math.tan((Math.PI / 180) * (cameraDegreesFromGround + getYDegreesToTarget()));
        return (netHeight / lengthToHeightRatio);
    }

}