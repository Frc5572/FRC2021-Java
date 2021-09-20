// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.Math;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.GenericHID;

public class Controller {

    // Button Definitions
    final int LEFT_Z = 2;
    final int LEFT_X = 0;
    final int LEFT_Y = 1;
    final int RIGHT_Z = 3;
    final int RIGHT_X = 4;
    final int RIGHT_Y = 5;
    final int LEFT_BUMPER = 5;
    final int RIGHT_BUMPER = 6;
    final int X_BUTTON = 3;
    final int Y_BUTTON = 4;
    final int B_BUTTON = 2;
    final int A_BUTTON = 1;
    final int START_BUTTON = 8;
    final int BACK_BUTTON = 7;
    final int LEFT_STICK_BUTTON = 9;
    final int RIGHT_STICK_BUTTON = 10;
    final int DPadDown = 180;
    final int DPadUp = 0;
    final int DPadLeft = 270;
    final int DPadRight = 90;
    // final int RIGHT_TRIGGER = 12;
    Joystick controller;
    
    public Controller(int id) {
        controller = new Joystick(id);
    }

    double LT() {
        return this.controller.getRawAxis(LEFT_Z);
    }

    boolean LB() {
        return this.controller.getRawButton(LEFT_BUMPER);
    }

    double RT() {
        return this.controller.getRawAxis(RIGHT_Z);
    }

    boolean RB() {
        return this.controller.getRawButton(RIGHT_BUMPER);
    }

    boolean X() {
        return this.controller.getRawButton(X_BUTTON);
    }

    boolean Y() {
        return this.controller.getRawButton(Y_BUTTON);
    }

    boolean A() {
        return this.controller.getRawButton(A_BUTTON);
    }

    boolean B() {
        return this.controller.getRawButton(B_BUTTON);
    }

    double grad(double n) {
        return n > 0 ? Math.pow(n, 2) : -Math.pow(n, 2);
    }

    double L() {
        return grad(this.controller.getRawAxis(LEFT_Y));
    }

    double R() {
        return grad(this.controller.getRawAxis(RIGHT_Y));
    }

    int POV() {
        return this.controller.getPOV(0);
    }

    boolean POVDown() {
        return this.controller.getPOV() == DPadDown;
    }

    boolean start() {
        return this.controller.getRawButton(START_BUTTON);
    }

    boolean back() {
        return this.controller.getRawButton(BACK_BUTTON);
    }

    boolean Lbutton() {
        return this.controller.getRawButton(LEFT_STICK_BUTTON);
    }

    boolean Rbutton() {
        return this.controller.getRawButton(RIGHT_STICK_BUTTON);
    }

    void rumble(double x, double y) {
        this.controller.setRumble(GenericHID.RumbleType.kLeftRumble, x);
        this.controller.setRumble(GenericHID.RumbleType.kRightRumble, y);
    }



}