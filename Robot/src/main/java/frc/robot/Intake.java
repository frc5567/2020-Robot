// import motors
// import piston
// import speed controller
// import speed controller group


public class Intake{

// declare motors
// declare piston
// declare xbox controller
// declare IntakeMotor controllers
// declare IntakeMotor button
// declare variable intakeMotorButtonPressed
// declare DropBarPiston button
// declare DropBarPiston boolean
// declare variabe intakeMotorSpeed

    // class in which the intake motor will be told to move
    public void setIntakeMotor(double intakeMotorSpeed){
        //set boolean intakeMotorButtonPressed = 0
                                               // set initial intake speed to 0         do we need?
                                               // set intakeMotorSpeed = 0;
                                               // initiate set motor speed to s
         // set motor speed to intakeMotorSpeed while pressed, 0 when released
          /**if intakeMortorButtonPressed = 1{
        * set intakeMotorSpeed = 1
        * 
        *  } else {
        *  set speed = 0
        * }
        * */ 
    }

    /**             //all the things used here
     *              declare DropBarMotor
     *              declare variable dropBarMotorSpeed
     *              declare DropBarButtonDown
     *              declare DropBarButtonUp
     *              declare boolean dropBarButtonDownPressed
     *              declare boolean dropBarButtonUpPressed
     *              
     *              // encoder on DropBarMotor
     *              declare DropBarEncoder
     * 
     * // resets the encoder value
     * public void encoderReset(){
     * DropBarEncoder.reset
     * }
     *               
     * // general class in which the motor will be told to move
     * public void setDropBarMotor(double DropBarMotorSpeed){
     * 
     *      initiate variable DropBarMotorSpeed = 0
     * 
     *      // buttons on the controller controlling the drop bar
     *      initiate boolean dropBarButtonDownPressed = 0
     *      initiate boolean dropBarButtonUpPressed = 0
     * 
     *      // to move down, conditions in this will have to be met
     *      while DropBarButtonDownPressed = activated {
     * 
     *      // checks bar's position
     *      get() encoder count
     * 
     *            // if the bar is already down, then the bar won't move. else, it will move down when pressed
     *            if encoder value = down encoder value{
     *         
     *            set dropBarMotorSpeed = 0
     * 
     *            } else {
     * 
     *            set dropBarMotorSpeed = (positive or negative depending on how the motor is mounted)
     *         }
     *      } 
     *       // to move up, conditions in this will have to be met
     *       while DropBarButtonUpPressed = activated{
     * 
     *       // checks bar's position
     *       get() encoder count
     * 
     *            // if the bar is already up, then the bar won't move. else, it will move up when pressed
     *            if encoder value = up encoder value (0){
     *         
     *            set dropBarMotor speed = 0
     * 
     *            }else{
     * 
     *      set motorSpeed = (positive or negative depending on how the motor is mounted)
     * 
     *              }
     * 
     *      }
     * } 
    **/
    public void setDropBarPiston(boolean DropBarPiston){

        // make button
        // init boolean to 0
        /**
         * if button = activated{
         * 
         * set piston boolean = extended (1)
         * 
         * } else {
         * 
         * set piston boolean = contracted (0)
         * 
         * }
         */
    }


}