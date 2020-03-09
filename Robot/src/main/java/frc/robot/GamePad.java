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
		Climb_Up(1),
		Climb_Down(2),
		Winch(3),
		Move_Magazine_Down(4),
		Move_Magazine_For_Launch(5),
		Launcher_And_Magazine(6),
		Rev_Launcher(7),
		Enable_Intake(8),
		Disable_Intake(9),
		Dump_Balls(10),
		Color_Wheel_by_Color(11),
		Color_Wheel_by_Distance(12);

		public final int value;

		GamePadControls(int newValue) {
			this.value = newValue;
		}
	}

	// TODO: Document methods with accurate comments
	

	/**
	 * expected to be used in co-pilot conroller 
	 *<p> switches the gear pneumatics to the high gear position 
	*/
	//gear up button
	public boolean getClimbUp() {
		return super.getRawButton(GamePadControls.Climb_Up.value);
	}
	public boolean getClimbUpPressed() {
		return super.getRawButtonPressed(GamePadControls.Climb_Up.value);
	}
	public boolean getClimbUpReleased() {
		return super.getRawButtonReleased(GamePadControls.Climb_Up.value);
	}

	/**
	 * expected to be used in co-pilot controller
	 * <p> switches the gear pneumatics to the low gear position
	 */
	//gear down button
	public boolean getClimbDown() {
		return super.getRawButton(GamePadControls.Climb_Down.value);
	}
	public boolean getClimbDownPressed() {
		return super.getRawButtonPressed(GamePadControls.Climb_Down.value);
	}
	public boolean getClimbDownReleased() {
		return super.getRawButtonReleased(GamePadControls.Climb_Down.value);
	}

	public boolean getWinch() {
		return super.getRawButton(GamePadControls.Winch.value);
	}
	public boolean getWinchPressed() {
		return super.getRawButtonPressed(GamePadControls.Winch.value);
	}
	public boolean getWinchReleased() {
		return super.getRawButtonReleased(GamePadControls.Winch.value);
	}

	// TODO: Fix name for method--> change to moveMgagzineLaunch
	public boolean getMoveMagazine() {
		return super.getRawButton(GamePadControls.Move_Magazine_For_Launch.value);
	}
	public boolean getMoveMagazinePressed() {
		return super.getRawButtonPressed(GamePadControls.Move_Magazine_For_Launch.value);
	}
	public boolean getMoveMagazineReleased() {
		return super.getRawButtonReleased(GamePadControls.Move_Magazine_For_Launch.value);
	}

	public boolean getMoveMagazineDown() {
		return super.getRawButton(GamePadControls.Move_Magazine_Down.value);
	}
	public boolean getMoveMagazineDownPressed() {
		return super.getRawButtonPressed(GamePadControls.Move_Magazine_Down.value);
	}
	public boolean getMoveMagazineDownReleased() {
		return super.getRawButtonReleased(GamePadControls.Move_Magazine_Down.value);
	}

	public boolean getRevLauncher() {
		return super.getRawButton(GamePadControls.Rev_Launcher.value);
	}
	public boolean getRevLauncherPressed() {
		return super.getRawButtonPressed(GamePadControls.Rev_Launcher.value);
	}
	public boolean getRevLauncherReleased() {
		return super.getRawButtonReleased(GamePadControls.Rev_Launcher.value);
	}

	//intake button
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
	public boolean getIntakeReleased() {
		return super.getRawButtonReleased(GamePadControls.Enable_Intake.value);
	}

	//intake turn off button
	/**
	 *  expected to be used in co-pilot controller
	 * <p> disables intake. Doing so raises the drop bar and turns off the intake motors
	 */
	public boolean getDisableIntake() {
		return super.getRawButton(GamePadControls.Disable_Intake.value);
	}
	public boolean getDisableIntakePressed() {
		return super.getRawButtonPressed(GamePadControls.Disable_Intake.value);
	}
	public boolean getDisableIntakeReleased() {
		return super.getRawButtonReleased(GamePadControls.Disable_Intake.value);
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
	public boolean getDumpAllBallsPressed() {
		return super.getRawButtonPressed(GamePadControls.Dump_Balls.value);
	}
	public boolean getDumpAllBallsReleased() {
		return super.getRawButtonReleased(GamePadControls.Dump_Balls.value);
	}

	public boolean getColorWheelColor() {
		return super.getRawButton(GamePadControls.Color_Wheel_by_Color.value);
	}
	public boolean getColorWheelColorPressed() {
		return super.getRawButtonPressed(GamePadControls.Color_Wheel_by_Color.value);
	}
	public boolean getColorWheelColorReleased() {
		return super.getRawButtonReleased(GamePadControls.Color_Wheel_by_Color.value);
	}

	public boolean getColorWheelDistance() {
		return super.getRawButton(GamePadControls.Color_Wheel_by_Distance.value);
	}
	public boolean getColorWheelDistancePressed() {
		return super.getRawButtonPressed(GamePadControls.Color_Wheel_by_Distance.value);
	}
	public boolean getColorWheelDistanceReleased() {
		return super.getRawButtonReleased(GamePadControls.Color_Wheel_by_Distance.value);
	}

	/**
	 * These must be extended because GenericHID is abstract
	 * We cannot delete these, nor make them private
	 */
	public double getX(Hand hand) {
		return getX();
	}

	public double getY(Hand hand) {
		return getY();
	}

}