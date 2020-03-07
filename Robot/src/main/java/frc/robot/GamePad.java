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
		Gear_Up(1),
		Gear_Down(2),
		Enable_Intake(3),
		Disable_Intake(4),
		Change_Launcher_RPM_Up(5),
		Change_Launcher_RPM_Down(6),
		Launcher(7),
		Disable_Launcher(8);

		@SuppressWarnings("MemberName")
		public final int value;

		GamePadControls(int newValue) {
			this.value = newValue;
		}
	}
	/**
	 * expectedd to be used in co-pilot conroller 
	 *<p> switches the gear pneumatics to the high gear position 
	*/
		//gear up button
	public boolean getGearUp() {
		return super.getRawButtonPressed(GamePadControls.Gear_Up.value);
	}
	/**
	 * expected to be used in co-pilot controller
	 * <p> switches the gear pneumatics to the low gear position
	 */
		//gear down button
	public boolean getGearDown() {
		return super.getRawButtonPressed(GamePadControls.Gear_Down.value);
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
		//Launcher RPM up button
		/**
		 * expected to be used in co-pilot controller
		 * <p> adjusts launcher speed up to accommadate better balls so that we can use the highest speed
		 * possible for the condition of the balls
		 */
	public boolean getLauncherRPMUp() {
		return super.getRawButtonPressed(GamePadControls.Change_Launcher_RPM_Up.value);
	}
		//Launcher RPM down button
		/**
		 * expected to be used in co-pilot controller
		 *<p> adjusts launcher speed up to accommadate tearing balls so that we can use the highest speed
		 * possible for the condition of the balls
		 */
	public boolean getLauncherRPMDown() {
		return super.getRawButtonPressed(GamePadControls.Change_Launcher_RPM_Down.value);
	}
	/**
	 * expected to be used in co-pilot controller
	 * <p> enables the launcher, shifts into low gear, and begins to index the balls into the launcher
	 * when the motor has reached desired rpm
	 */
		//shoot button
	public boolean getEnableLauncher() {
		return super.getRawButtonPressed(GamePadControls.Launcher.value);
	}
	/**
	 * expected to be used in co-pilot controller
	 * <p> disables the launcher and shifts to high gear
	 */
		//stop shooting button
	public boolean getDisableLauncher() {
		return super.getRawButtonPressed(GamePadControls.Disable_Launcher.value);
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