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

    /**TODO:Work out the modification to the target based on target yaw, until then this is unfinished
     * This method should adjust the x degrees to target based on target's yaw
     * <p>This is designed to hit the inner target when shooting at an angle.
     * <p>Note that we should only adjust within a certain window, past that we should just target the center of mass
     * @return The adjusted degrees to the inner target
     */
    public double getModifiedDegreesToTarget() {
        return m_limelightTable.getEntry("tx").getDouble(0);
    }

    /**
     * TODO: This value needs to be further researched/investigated
     * @return The skew of the target in degrees
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