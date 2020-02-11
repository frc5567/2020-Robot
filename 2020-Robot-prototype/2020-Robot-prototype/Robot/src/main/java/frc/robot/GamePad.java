package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;

// this enum difines the buttons and what they do when active
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
	private enum GamePadControls {
		// Buttons
		Gear_Up(1),
		Gear_Down(2),
		Enable_Intake(3),
		Disable_Intake(4),
		Change_Shooter_RPM_Up(5),
		Change_Shooter_RPM_Down(6),
		Shooter(7),
		Disable_shooter(8),
        Spin_pannel_to_Color(9), //make sure to leave a variable for the color
        Spin_pannel_by_Rotation_Number(10); //make sure to change the number of spins

		@SuppressWarnings("MemberName")
		public final int value;

		GamePadControls(int newValue) {
			this.value = newValue;
		}
	}

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

	/** if button should be pressed once, use this format
	 * Returns if button has been released
	 * @param button Button to view if released
	 * @return Returns button status (if released)
	 */
	public boolean getGamePadButtonReleased(GamePadControls button) {
		return super.getRawButtonReleased(button.value);
	}
		//gear up button
	public boolean getGearUp() {
		return super.getRawButtonReleased(GamePadControls.Gear_Up.value);
	}
		//gear down button
	public boolean getGearDown() {
		return super.getRawButtonReleased(GamePadControls.Gear_Down.value);
	}
		//intake button
	public boolean getIntake() {
		return super.getRawButtonReleased(GamePadControls.Enable_Intake.value);
	}
		//intake turn off button
	public boolean getDisableIntake() {
		return super.getRawButtonReleased(GamePadControls.Disable_Intake.value);
	}
		//shooter RPM up button
	public boolean getShooterRPMUp() {
		return super.getRawButtonReleased(GamePadControls.Change_Shooter_RPM_Up.value);
	}
		//shooter RPM down button
	public boolean getShooterRPMDown() {
		return super.getRawButtonReleased(GamePadControls.Change_Shooter_RPM_Down.value);
	}
		//shoot button
	public boolean getEnableShooter() {
		return super.getRawButtonReleased(GamePadControls.Shooter.value);
	}
		//stop shooting button
	public boolean getDisableShooter() {
		return super.getRawButtonReleased(GamePadControls.Disable_shooter.value);
	}
		//spin to color button
	public boolean getSpinToColor() {
		return super.getRawButtonPressed(GamePadControls.Spin_pannel_to_Color.value);
	}
		//spin by roation number button
	public boolean getSpinByRotationNumber() {
		return super.getRawButtonPressed(GamePadControls.Spin_pannel_by_Rotation_Number.value);
	}


	/**   if button should be held, use this format
	 * Returns the status of the button (true if held)
	 * @param button Button to view if button is held
	 * @return Returns button status (if held)
	 */
	public boolean getGamePadButton(GamePadControls button) {
		return super.getRawButton(button.value);
	}

}