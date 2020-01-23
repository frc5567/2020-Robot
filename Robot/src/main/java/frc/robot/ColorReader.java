package frc.robot;

//import edu.wpi.first.wpilibj.DriverStation;

public class ColorReader{
    
    //declares gameData varibale
    private String gameData;

    //Method for either recieving color from fms at comps or manually changing thre recieved color
    public void colorReader(String gameData){
        //This can be manually changed from the driver station
        this.gameData = DriverStation.getInstance().getGameSpecificMessage();
    }

    //method for setting recievedColor to the color recieved from fms or the manually inputted color
    public char getColor(){
        if(gameData.length() > 0){
            return gameData.charAt(0);
        }
    }
}
