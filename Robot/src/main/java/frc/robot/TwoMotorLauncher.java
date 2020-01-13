package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

/**
 * A launcher that uses two motors to launch a projectile
 * <p>Utilizes a scrapped together proportionality controller to adjust speed
 * 
 * @version 1/7/2020
 * @author Josh Overbeek
 */
public class TwoMotorLauncher extends Launcher{
    //speed controller used to launch 
    private BaseMotorController secondMotor;
    
    /**
     * Constructor for Launcher objects
     * 
     * <p> To be used for any system that launches a projectile
     * 
     * @param p The proportionality constant used to control this launcher's speed
     * @param motor The speed controller used to launch the projectile
     */
    public TwoMotorLauncher(double p, BaseMotorController motor, BaseMotorController secondMotor) {
        super(p, motor);
        this.secondMotor = secondMotor;
    }

    /**
     * Assigns a speed directly to the motor controllers
     * @param speed A value between -1.0 and 1.0 where 1.0 is full speed forward
     */
    @Override
    public void setMotor(double speed) {
        super.setMotor(speed);
        secondMotor.set(ControlMode.PercentOutput, -speed);
    }

    @Override
    public String toString() {
        return "Motor 1: " + getMotor().toString() + " | Motor 2: " + secondMotor.toString() + " | P Constant: " + getP() + " | Current Speed: " + getCurrentSpeed() + " | Current Error: " + getError();
    }
}