package frc.robot;

/**
 * An auton interface that any auton class should probably implement
 * 
 * @author Josh Overbeek
 * @version 6/22/2020
 */
public interface AutonBase {
    /**
     * The init method to be called in autonomousInit() in the main robot
     */
    public void init();

    /**
     * The periodic method to be called in autonomousPeriodic() in the main robot
     */
    public void periodic();
}