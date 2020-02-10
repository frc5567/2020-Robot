/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.PilotController.DriveType;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;

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


    //declares our drivetrain motor controllers and a currently unused shooter motor
    TalonFX m_masterLeftDriveFalcon;
    TalonFX m_masterRightDriveFalcon;

    TalonFX m_slaveLeftDriveFalcon;
    TalonFX m_slaveRightDriveFalcon;

    //declares controllers and drivetrain for testing drive code
    XboxController m_driveController;
    PilotController m_pilotController;
    ShiftDrive m_drivetrain;

    DoubleSolenoid m_leftPiston;
    DoubleSolenoid m_rightPiston;

    public Robot() {
        //instantiates master motors for drive
        m_masterLeftDriveFalcon = new TalonFX(RobotMap.MASTER_LEFT_FALCON_ID);
        m_masterRightDriveFalcon = new TalonFX(RobotMap.MASTER_RIGHT_FALCON_ID);

        //instantiates slave motors for drive
        m_slaveLeftDriveFalcon = new TalonFX(RobotMap.SLAVE_LEFT_FALCON_ID);
        m_slaveRightDriveFalcon = new TalonFX(RobotMap.SLAVE_RIGHT_FALCON_ID);

        //instantiate drivetrain for tested
        m_driveController = new XboxController(RobotMap.DRIVE_CONTROLLER_PORT);

        m_leftPiston = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.LEFT_SOLENOID_FORWARD_PORT, RobotMap.LEFT_SOLENOID_REVERSE_PORT);
        m_rightPiston = new DoubleSolenoid(RobotMap.PCM_CAN_ID, RobotMap.RIGHT_SOLENOID_FORWARD_PORT, RobotMap.RIGHT_SOLENOID_REVERSE_PORT);
        m_drivetrain = new ShiftDrive(m_masterLeftDriveFalcon, m_masterRightDriveFalcon, m_slaveLeftDriveFalcon, m_slaveRightDriveFalcon, m_leftPiston, m_rightPiston, true);

        m_pilotController = new PilotController(m_driveController, m_drivetrain, DriveType.kArcade);
    }

    @Override
    public void robotInit() {
        //zeros used motor contollers
        m_masterLeftDriveFalcon.set(ControlMode.PercentOutput, 0);
        m_masterRightDriveFalcon.set(ControlMode.PercentOutput, 0);
        m_slaveLeftDriveFalcon.set(ControlMode.PercentOutput, 0);
        m_slaveRightDriveFalcon.set(ControlMode.PercentOutput, 0);
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
        m_pilotController.controlDriveTrain();
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

}
