/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * 
 * @version 1/25/2019
 */
public class Robot extends TimedRobot {
    /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */

  //declare the launcher motor controllers
  TalonSRX m_masterLauncher;

  /**the launcher slave motor on the same side of the launcher as the master */
  VictorSPX m_closeLauncherSlave;

  /**the launcher slave motors on the opposite side of the launcher from the master */
  VictorSPX m_farLauncherSlave1, m_farLauncherSlave2;

  //declares our launcher system and our controls for that system over the launcher tab
  Launcher m_launcher;
  ShuffleboardShooterControl m_shooterControl;

  //declares an xbox controller used for testing prototype code
  XboxController m_testController;
  Magazine m_magazine;

  TalonSRX m_magazineMotor;

  ShuffleboardTab m_launcherTab;

  NetworkTableEntry m_magazineSpeed;
  
  public Robot() {
    //instantiates our test controller
    m_testController = new XboxController(RobotMap.TEST_CONTROLLER_PORT);

    //instantiate launcher motor controllers and shuffleboard control for those motors
    m_masterLauncher = new TalonSRX(RobotMap.MASTER_LAUNCHER_ID);
    m_closeLauncherSlave = new VictorSPX(RobotMap.CLOSE_LAUNCHER_SLAVE_ID);
    m_farLauncherSlave1 = new VictorSPX(RobotMap.FAR_LAUNCHER_SLAVE1_ID);
    m_farLauncherSlave2 = new VictorSPX(RobotMap.FAR_LAUNCHER_SLAVE2_ID);

    m_magazineMotor = new TalonSRX(30);

    m_launcher = new Launcher(RobotMap.LAUNCHER_ADJUSTMENT_VALUE, m_masterLauncher, m_closeLauncherSlave, m_farLauncherSlave1, m_farLauncherSlave2);
    m_shooterControl = new ShuffleboardShooterControl(m_launcher);

    m_magazine = new Magazine(m_magazineMotor);

    m_launcherTab = Shuffleboard.getTab("Launcher");

    //creates a persistent widget as text for controlling speed
    m_magazineSpeed = m_launcherTab.addPersistent("PercentLaunchSpeed", 0.0)                //creates widget with 0.0 as a default
    .withWidget(BuiltInWidgets.kTextView)             //sets widget to a text view
    .withProperties(Map.of("min", -1.0, "max", 1.0))  //sets min and max values
    .getEntry();  
  }

  @Override
  public void robotInit() {
  }



  @Override
  public void autonomousInit() {
  }
 
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {

  }
        
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    //zeros the shooter
    m_shooterControl.zeroSpeed();
  }

  @Override
  public void testPeriodic() {
    //this test periodic is designed for launcher velocity testing
    //sets the velocity of the launcher while holding the b button
    if(m_testController.getBButton()) {
      m_shooterControl.setVelocity();
    }
    else if(m_testController.getAButton()) {
      m_shooterControl.setPercentSpeed();
    }
    //kills the velocity while not holding
    else {
      m_shooterControl.zeroSpeed();
    }

    if (m_testController.getBumper(Hand.kRight)) {
      m_magazine.runBelt(m_magazineSpeed.getDouble(0));
    }
  }

  @Override
  public void disabledInit() {

  }

  @Override
  public void disabledPeriodic() {
  }


}
