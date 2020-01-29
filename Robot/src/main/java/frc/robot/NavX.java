package frc.robot;

import com.kauailabs.navx.frc.AHRS; //see if fixed after getting phoenix

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SerialPort;

public class NavX extends AHRS {

    private boolean m_offsetApplied;

    public NavX(SPI.Port spi_port_id) {
        super(spi_port_id);
        m_offsetApplied = false;
    }

    public NavX(I2C.Port i2c_port_id) {
        super(i2c_port_id);
        m_offsetApplied = false;
    }

    public NavX(SerialPort.Port serial_port_id) {
        super(serial_port_id);
        m_offsetApplied = false;
    }

    public float getOffsetYaw() {
        float yaw = getYaw();
        if (m_offsetApplied) {
            yaw += 180;

            if (yaw < -180) {
                yaw += 360;
            } else if (yaw > 180) {
                yaw -= 360;
            }
            return yaw;
        } else {
            return getYaw();
        }
    }

    public void flipOffset() {
        if (m_offsetApplied) {
            m_offsetApplied = false;
        }
        else {
            m_offsetApplied = true;
        }
    }

    public boolean getOffsetApplied(){
        return m_offsetApplied;
    }
}