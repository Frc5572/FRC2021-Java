// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //initialize timer
  Timer timer = new Timer();

  // initialize motor names and ID
  TalonSRX m_frontLeftSpeed = new TalonSRX(0);
  TalonSRX m_frontRightSpeed = new TalonSRX(1);
  TalonSRX m_backLeftSpeed = new TalonSRX(2);
  TalonSRX m_backRightSpeed = new TalonSRX(3);
  TalonSRX m_frontLeftAngle = new TalonSRX(4);
  TalonSRX m_frontRightAngle = new TalonSRX(5);
  TalonSRX m_backLeftAngle = new TalonSRX(6);
  TalonSRX m_backRightAngle = new TalonSRX(7);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    // set motors to 0 at beginning
    m_frontLeftSpeed.set(ControlMode.PercentOutput, 0);
    m_frontRightSpeed.set(ControlMode.PercentOutput, 0);
    m_backLeftSpeed.set(ControlMode.PercentOutput, 0);
    m_backRightSpeed.set(ControlMode.PercentOutput, 0);
    m_frontLeftAngle.set(ControlMode.PercentOutput, 0);
    m_frontRightAngle.set(ControlMode.PercentOutput, 0);
    m_backLeftAngle.set(ControlMode.PercentOutput, 0);
    m_backRightAngle.set(ControlMode.PercentOutput, 0);

    // set neutral mode
    m_frontLeftSpeed.setNeutralMode(NeutralMode.Coast);
    m_frontRightSpeed.setNeutralMode(NeutralMode.Coast);
    m_backLeftSpeed.setNeutralMode(NeutralMode.Coast);
    m_backRightSpeed.setNeutralMode(NeutralMode.Coast);
    m_frontLeftAngle.setNeutralMode(NeutralMode.Brake);
    m_frontRightAngle.setNeutralMode(NeutralMode.Brake);
    m_backLeftAngle.setNeutralMode(NeutralMode.Brake);
    m_backRightAngle.setNeutralMode(NeutralMode.Brake);

    // frontLeftSpeed.set(ControlMode.Follower, 5);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {

    // start timer
    timer.start();

    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
