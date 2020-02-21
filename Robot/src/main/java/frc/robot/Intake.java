package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
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
     * An enum for storing our possible intake positions
     * <p>Possible values:
     * <li>{@link #kLowered}</li>
     * <li>{@link #kRaised}</li>
     * <li>{@link #kUnknown}</li>
     */
    public enum Position {
        /**
         * The lowered position of the intake
         * <p>This is the position we use to actually run our system
         */
        kLowered("Lowered"),
        /**
         * The raised position of the intake
         * <p>This is the position we need to be in at the start of the match
         * and the position we use while driving around
         */
        kRaised("Raised"),
        /**
         * The starting value of the intake
         * <p>We intialize our storage variable to this position,
         * however at no point in the match should this ever be the state of the intake
         */
        kUnknown("Unknown");

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

    //declare our intake motor controllers
    SpeedController m_outerMotor;
    BaseMotorController m_innerMotor;

    //delcare our position control solenoid
    DoubleSolenoid m_positionPiston;

    //declare our position tracker
    Position m_position;
    
    /**
     * Constructor for intake objects
     * @param outerIntakeMotor A default motor controller for running the intake
     * @param innerIntakeMotor The inner intake wheel for pulling balls into the magazine
     * @param positionPiston the double solenoid used to control the piston controlling position
     */
    public Intake(SpeedController outerIntakeMotor, BaseMotorController innerIntakeMotor, DoubleSolenoid positionPiston) {
        //instantiate instance variables
        m_outerMotor = outerIntakeMotor;
        m_innerMotor = innerIntakeMotor;
        m_positionPiston = positionPiston;

        //set our starting position to unknown
        m_position = Position.kUnknown;

        //then set the solenoids to the raised position
        setPosition(Position.kRaised);
    }

    /**
     * Constructor for intake objects
     * This constructor requires a spark pro motor controller
     * @param outerIntakeMotor A spark pro motor controller for running the intake
     * @param innerIntakeMotor The inner intake wheel for pulling balls into the magazine
     * @param positionPiston the double solenoid used to control the piston controlling position
     */
    public Intake(CANSparkMax outerIntakeMotor, BaseMotorController innerIntakeMotor, DoubleSolenoid positionPiston) {  
        //sets the time in seconds from zero to full for the intake motor
        //acts as a speed setter to control acceleration
        //this is configured on the passed in variable rather than the member variable because it must be run on a object declared as a CANSparkMax
        //It should make no difference in the end result
        outerIntakeMotor.setOpenLoopRampRate(RobotMap.INTAKE_OPEN_LOOP_RAMP_TIME_S);

        //instantiate instance variables
        m_outerMotor = outerIntakeMotor;
        m_innerMotor = innerIntakeMotor;
        m_positionPiston = positionPiston;

        //set our starting position to unknown
        m_position = Position.kUnknown;

        //then set the solenoids to the raised position
        setPosition(Position.kRaised);
  }

    /**
     * Sets the speed of the inner intake motor
     * @param speed The percent speed between -1.0 and 1.0
     */
    public void setInnerIntakeMotor(double speed) {
        m_innerMotor.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Sets the speed of the outer intake motor
     * @param speed The percent speed between -1.0 and 1.0
     */
    public void setOuterIntakeMotor(double speed) {
        m_outerMotor.set(speed);
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
        return "Inner Motor: " + m_innerMotor.toString() + " | Outer Motor: " + m_outerMotor.toString()+" | Solenoid: " + m_positionPiston.toString() + " | Position: " + m_position.toString();
    }
}