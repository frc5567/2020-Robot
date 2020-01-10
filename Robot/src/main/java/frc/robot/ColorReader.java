package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;

public class ColorReader{

    private String gameData;

    public colorReader(String gameData){
        this.gameData = DriverStation.getInstance().getGameSpecificMessage();
    }


    public char getColor(){
        if(gameData.length() > 0){
            return gameData.charAt(0);
        } 
    }

}