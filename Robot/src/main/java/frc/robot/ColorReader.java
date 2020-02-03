package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;

//import edu.wpi.first.wpilibj.DriverStation;

/**
 * For recieving color data sent from the FMS
 * 
 * @version 1/27/2020
 * @author Caleb Overbeek
 */
public class ColorReader{
    
    //declares gameData varibale
    private String m_gameData;

    //Method for either recieving color from fms at comps or manually changing thre recieved color
    public ColorReader(){
        //This can be manually changed from the driver station
        this.m_gameData = DriverStation.getInstance().getGameSpecificMessage();
    }
    
    /**
     *
     * @return what color the field sends us. Returns 0 if no color was recieved
     */
    public char getColor(){
        if(m_gameData.length() > 0){
            return m_gameData.charAt(0);
        }
        return '0';
    }
}
