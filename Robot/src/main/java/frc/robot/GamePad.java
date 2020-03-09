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


		@SuppressWarnings("MemberName")
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
		return super.getRawButtonReleased(GamePadControls.Climb_Up.value);
	}
	/**
	 * expected to be used in co-pilot controller
	 * <p> switches the gear pneumatics to the low gear position
	 */
		//gear down button
	public boolean getClimbDown() {
		return super.getRawButtonReleased(GamePadControls.Climb_Down.value);
	}

	public boolean getWinch() {
		return super.getRawButtonPressed(GamePadControls.Winch.value);
	}

	public boolean getMoveMagazine() {
		return super.getRawButtonPressed(GamePadControls.Move_Magazine_For_Launch.value);
	}

	public boolean getMoveMagazineDown() {
		return super.getRawButtonPressed(GamePadControls.Move_Magazine_Down.value);
	}

	public boolean getRevLauncher() {
		return super.getRawButtonPressed(GamePadControls.Rev_Launcher.value);
	}

		//intake button
		/**
		 * expected to be used in co-pilot controller
		 * <p> enables intake. Doing so drops the drop bar and turns on the intake motors
		 */
	public boolean getIntake() {
		return super.getRawButtonPressed(GamePadControls.Enable_Intake.value);
	}
		//intake turn off button
		/**
		 *  expected to be used in co-pilot controller
		 * <p> disables intake. Doing so raises the drop bar and turns off the intake motors
		 */
	public boolean getDisableIntake() {
		return super.getRawButtonPressed(GamePadControls.Disable_Intake.value);
	}
	
	public boolean getLauncherAndMagazine() {
		return super.getRawButtonPressed(GamePadControls.Launcher_And_Magazine.value);
	}

	public boolean getDumpAllBalls() {
		return super.getRawButtonPressed(GamePadControls.Dump_Balls.value);
	}

	public boolean getColorWheelColor() {
		return super.getRawButtonReleased(GamePadControls.Color_Wheel_by_Color.value);
	}

	public boolean getColorWheelDistance() {
		return super.getRawButtonReleased(GamePadControls.Color_Wheel_by_Distance.value);
	}

	/**
	 * These must be extended because GenericHID is abstract
	 * We cannot delete these, nor make them private
	 */
	@Override
	public double getX(Hand hand) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getY(Hand hand) {
		// TODO Auto-generated method stub
		return 0;
	}

}