package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;

public class GamePad extends GenericHID {

	/**
	 * Constructor, used for calling super constructor
	 * @param port Port the gamepad is connected to
	 */
	public GamePad(final int port) {
		super(port);
	}

	/**
	 * Actions each button performs
	 * Change numbers to correct port number
	 */
	// this enum difines the buttons and what they do when active
	private enum GamePadControls {
		// Buttons
		Climb_Up(RobotMap.GAMEPAD_BUTTON_ID.CLIMB_UP),
		Climb_Down(RobotMap.GAMEPAD_BUTTON_ID.CLIMB_DOWN),
		Winch(RobotMap.GAMEPAD_BUTTON_ID.WINCH),
		Move_Magazine_Down(RobotMap.GAMEPAD_BUTTON_ID.MOVE_MAGAZINE_DOWN),
		Move_Magazine_For_Launch(RobotMap.GAMEPAD_BUTTON_ID.MOVE_MAGAZINE_LAUNCH),
		Launcher_And_Magazine(RobotMap.GAMEPAD_BUTTON_ID.LAUNCHER_AND_MAGAZINE),
		Rev_Launcher(RobotMap.GAMEPAD_BUTTON_ID.REV_LAUNCHER),
		Enable_Intake(RobotMap.GAMEPAD_BUTTON_ID.ENABLE_INTAKE),
		Disable_Intake(RobotMap.GAMEPAD_BUTTON_ID.DISABLE_INTAKE),
		Dump_Balls(RobotMap.GAMEPAD_BUTTON_ID.DUMP_BALLS),
		Color_Wheel_by_Color(RobotMap.GAMEPAD_BUTTON_ID.COLOR_WHEEL_COLOR),
		Color_Wheel_by_Distance(RobotMap.GAMEPAD_BUTTON_ID.COLOR_WHEEL_DISTANCE);

		public final int value;

		GamePadControls(int newValue) {
			this.value = newValue;
		}
	}

	/**
	 * expected to be used in co-pilot conroller 
	 *<p> switches the gear pneumatics to the high gear position 
	*/
	//gear up button
	public boolean getClimbUp() {
		return super.getRawButton(GamePadControls.Climb_Up.value);
	}
	/**
	 * expected to be used in co-pilot controller
	 * <p> switches the gear pneumatics to the low gear position
	 */
	//gear down button
	public boolean getClimbDown() {
		return super.getRawButton(GamePadControls.Climb_Down.value);
	}

	public boolean getWinch() {
		return super.getRawButton(GamePadControls.Winch.value);
	}

	public boolean getMoveMagazine() {
		return super.getRawButton(GamePadControls.Move_Magazine_For_Launch.value);
	}

	public boolean getMoveMagazineDown() {
		return super.getRawButton(GamePadControls.Move_Magazine_Down.value);
	}

	public boolean getRevLauncherPressed() {
		return super.getRawButtonPressed(GamePadControls.Rev_Launcher.value);
	}

	public boolean getRevLauncherReleased() {
		return super.getRawButtonReleased(GamePadControls.Rev_Launcher.value);
	}

	/**
	 * expected to be used in co-pilot controller
	 * <p> enables intake. Doing so drops the drop bar and turns on the intake motors
	 */
	public boolean getIntake() {
		return super.getRawButton(GamePadControls.Enable_Intake.value);
	}

	public boolean getIntakePressed() {
		return super.getRawButtonPressed(GamePadControls.Enable_Intake.value);
	}
	//intake turn off button
	/**
	 *  expected to be used in co-pilot controller
	 * <p> disables intake. Doing so raises the drop bar and turns off the intake motors
	 */
	public boolean getDisableIntakePressed() {
		return super.getRawButtonPressed(GamePadControls.Disable_Intake.value);
	}
	
	public boolean getLauncherAndMagazine() {
		return super.getRawButton(GamePadControls.Launcher_And_Magazine.value);
	}

	public boolean getLauncherAndMagazinePressed() {
		return super.getRawButtonPressed(GamePadControls.Launcher_And_Magazine.value);
	}

	public boolean getLauncherAndMagazineReleased() {
		return super.getRawButtonReleased(GamePadControls.Launcher_And_Magazine.value);
	}

	public boolean getDumpAllBalls() {
		return super.getRawButton(GamePadControls.Dump_Balls.value);
	}

	public boolean getColorWheelColor() {
		return super.getRawButton(GamePadControls.Color_Wheel_by_Color.value);
	}

	public boolean getColorWheelDistanceReleased() {
		return super.getRawButtonReleased(GamePadControls.Color_Wheel_by_Distance.value);
	}

	/**
	 * These must be extended because GenericHID is abstract
	 * We cannot delete these, nor make them private
	 */
	public double getX(Hand hand) {
		return getRawAxis(0);
	}

	public double getY(Hand hand) {
		return getRawAxis(1);
	}

}