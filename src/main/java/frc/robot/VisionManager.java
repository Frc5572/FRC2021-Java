package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class VisionManager {
    double disX = 0, disY = 0, tx = 0, ty = 0, ta = 0, tv = 0;
    boolean targetFound = false;

    void Update(){

    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    tx = table.getEntry("tx").getDouble(0.0);
    ty = table.getEntry("ty").getDouble(0.0);
    ta = table.getEntry("ta").getDouble(0.0);
    tv = table.getEntry("tv").getDouble(0.0);

    if (tv < 1.0) {
            targetFound = false;
            disX = 0;
            disY = 0;
        } else {
            targetFound = true;
            disX = tx;
            disY = ty;
        }
    }

    void TurnOffLights(){

    }
}
