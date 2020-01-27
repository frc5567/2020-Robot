/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.Drivetrain;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  Drivetrain m_drivetrain;
  Pathing m_pather;

  TalonSRX leftTalon;
  TalonSRX rightTalon;

  VictorSPX leftVictor;
  VictorSPX rightVictor;

  VictorSPX shooterMotor;

  Launcher shooter;
  ShuffleboardShooterControl shooterControl;

  XboxController testController;

  public Robot() {
    leftTalon = new TalonSRX(1);
    rightTalon = new TalonSRX(2);

    leftVictor = new VictorSPX(11);
    rightVictor = new VictorSPX(12);

    shooterMotor = new VictorSPX(15);

    shooter = new Launcher(0.5, leftTalon, rightTalon);
    shooterControl = new ShuffleboardShooterControl(shooter);

    testController = new XboxController(0);

    m_drivetrain = new Drivetrain(m_gyro);

    try {
      m_pather = new Pathing(m_drivetrain, m_gyro, testController);
    } catch (Exception e) {
      System.out.println("Pather failed to instantiate");
    }

    m_drivetrain.talonDriveConfig();


  }

  @Override
  public void robotInit() {
    leftTalon.set(ControlMode.PercentOutput, 0);
    rightTalon.set(ControlMode.PercentOutput, 0);
    leftVictor.set(ControlMode.PercentOutput, 0);
    rightVictor.set(ControlMode.PercentOutput, 0);

    m_drivetrain.talonDriveConfig();
  }

  @Override
  public void autonomousInit() {
     if (m_pather != null) {
      m_pather.resetFlags();
    }
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    if (m_pather != null) {
      m_pather.resetFlags();
    }
  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    shooterControl.zeroSpeed();
  }

  @Override
  public void testPeriodic() {
    if(testController.getAButton()) {
      shooterControl.setSpeed();
    }
    else shooterControl.zeroSpeed();
  }

  m_drivetrain.talonArcadeDrive((testController.getTriggerAxis(Hand.kRight) - testController.getTriggerAxis(Hand.kLeft)), testController.getX(Hand.kLeft), true);

    // Command for switching between the two solenoid positions. The positions are forward and reverse.
    if(testController.getXButton()){
      if(SOLENOID_POSITION == true){
        solenoidReverse();
      } else {
        solenoidForward();
        }
    }

}
