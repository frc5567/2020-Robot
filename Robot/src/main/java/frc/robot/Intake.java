package frc.robot;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * A class for controlling the intake system
 * <p>This expects a SparkMAX controlling a Neo as its main motor
 * @author Josh Overbeek
 * @version 2/8/2020
 */
public class Intake {

    /**
     * An enum for storing our positions
     */
    public enum Position {
        kLowered("Lowered"), kRaised("Raised");

        private String positionName;

        /**
         * @param positionName The name of the position
         */
        Position(String positionName) {
            this.positionName = positionName;
        }

        /**
         * Returns the position object represented as a string
         */
        public String toString() {
            return this.positionName;
        }
    }

    //declare our intake motor controller
    SpeedController m_motor;

    //delcare our position control solenoid
    DoubleSolenoid m_positionPiston;

    //declare our position tracker
    Position m_position;
    
    /**
     * Constructor for intake objects
     * @param intakeMotor A default motor controller for running the intake
     * @param positionPiston the double solenoid used to control the piston controlling position
     */
    public Intake(SpeedController intakeMotor, DoubleSolenoid positionPiston) {
        //instantiate instance variables
        m_motor = intakeMotor;
        m_positionPiston = positionPiston;

        //set our default position to raised
        m_position = Position.kRaised;
    }

    /**
     * Constructor for intake objects
     * This constructor requires a spark pro motor controller
     * @param intakeMotor A spark pro motor controller for running the intake
     * @param positionPiston the double solenoid used to control the piston controlling position
     */
    public Intake(CANSparkMax intakeMotor, DoubleSolenoid positionPiston) {  
        //sets the time in seconds from zero to full for the intake motor
        //acts as a speed setter to control acceleration
        //this is configured on the passed in variable rather than the member variable because it must be run on a object declared as a CANSparkMax
        //It should make no difference in the end result
        intakeMotor.setOpenLoopRampRate(RobotMap.INTAKE_OPEN_LOOP_RAMP_TIME_S);

        //instantiate instance variables
        m_motor = intakeMotor;
        m_positionPiston = positionPiston;

        //set our default position to raised
        m_position = Position.kRaised;

    }

    /**
     * Sets the speed of the intake motor
     * @param speed The percent speed between -1.0 and 1.0
     */
    public void setIntakeMotor(double speed) {
        m_motor.set(speed);
    }

    /**
     * Sets the position of the intake to the requested value
     * @param position The position the intake should move to
     */
    public void setPosition(Position position) {
        //breaks out of method if we are already at our target position
        if(m_position == position) {
            return;
        }

        //sets our current position to the desired position
        m_position = position;

        //sets the value of the piston based on the passed in position
        //currently what is forward and what is reverse is arbitrary
        if(m_position == Position.kLowered) {
            m_positionPiston.set(Value.kForward);
        }
        else if (m_position == Position.kRaised) {
            m_positionPiston.set(Value.kReverse);
        }
    }

    /**
     * @return The name of the position we are currently in
     */
    public String getPositionName() {
        return m_position.toString();
    }

    /**
     * @return the current position of the intake
     */
    public Position getPosition() {
        return m_position;
    }

    /**
     * @return the intake represented as a string
     */
    public String toString() {
        return "Motor: " + m_motor.toString() + " | Solenoid: " + m_positionPiston.toString() + " | Position: " + m_position.toString();
    }
}