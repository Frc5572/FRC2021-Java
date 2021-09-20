// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Compressor;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

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
  
  // create constants
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


  // public var for shooter PID
  double v1;
  double v2;
  int PCM1 = 0;
  int PCM2 = 1;


  // initialize timer
  Timer timer = new Timer();

  Compressor compressor = new Compressor();

  DoubleSolenoid intakeSol = new DoubleSolenoid(PCM1, 6, 1);
  DoubleSolenoid climberSol2 = new DoubleSolenoid(PCM1, 7, 0);
  DoubleSolenoid climberSol1 = new DoubleSolenoid(PCM2, 4, 3);
  DoubleSolenoid hopperSol = new DoubleSolenoid(PCM1, 5, 2);

  // initialize motor names and ID
  WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(4);
  // WPI_TalonSRX m_middleLeft = new WPI_TalonSRX(6);
  WPI_TalonSRX m_backLeft = new WPI_TalonSRX(8);
  SpeedControllerGroup leftDriveMotors = new SpeedControllerGroup(m_frontLeft, m_backLeft);
  WPI_TalonSRX m_frontRight = new WPI_TalonSRX(3);
  // WPI_TalonSRX m_middleRight = new WPI_TalonSRX(3);
  WPI_TalonSRX m_backRight = new WPI_TalonSRX(2);
  SpeedControllerGroup rightDriveMotors = new SpeedControllerGroup(m_frontRight, m_backRight);

  WPI_TalonSRX m_shooterLeft = new WPI_TalonSRX(12);
  WPI_TalonSRX m_shooterRight = new WPI_TalonSRX(14);

  SpeedControllerGroup shooterMotors = new SpeedControllerGroup(m_shooterLeft, m_shooterRight);

  WPI_TalonSRX m_IntakeMotors = new WPI_TalonSRX(11);
  // PID object
  PIDController pid = new PIDController(.0045, .0, 0, 100);

  CANSparkMax m_Climber1 = new CANSparkMax(16, MotorType.kBrushless);
  CANSparkMax m_Climber2 = new CANSparkMax(15, MotorType.kBrushless);
  SpeedControllerGroup climberMotors = new SpeedControllerGroup(m_Climber1, m_Climber2);

  //Neos
  CANSparkMax m_TurretMotor = new CANSparkMax(13, MotorType.kBrushless);

  // controllers
  Joystick driver = new Joystick(0);
  Joystick operator = new Joystick(1);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    // smart dashboard put PID values
    // SmartDashboard.putNumber("P Gain", 0);
    // SmartDashboard.putNumber("I Gain", 0);
    // SmartDashboard.putNumber("D Gain", 0);
    // SmartDashboard.putNumber("SetPoint", 0);

    // set motors to 0 at beginning
    m_frontLeft.set(ControlMode.PercentOutput, 0);
    m_frontRight.set(ControlMode.PercentOutput, 0);
    // m_middleLeft.set(ControlMode.PercentOutput, 0);
    // m_middleRight.set(ControlMode.PercentOutput, 0);
    m_backLeft.set(ControlMode.PercentOutput, 0);
    m_backRight.set(ControlMode.PercentOutput, 0);

    m_shooterLeft.set(ControlMode.PercentOutput, 0);
    m_shooterRight.set(ControlMode.PercentOutput, 0);

    m_IntakeMotors.set(ControlMode.PercentOutput, 0);

    // set neutral mode
    m_frontLeft.setNeutralMode(NeutralMode.Coast);
    m_frontRight.setNeutralMode(NeutralMode.Coast);
    // m_middleLeft.setNeutralMode(NeutralMode.Brake);
    // m_middleRight.setNeutralMode(NeutralMode.Brake);
    m_backLeft.setNeutralMode(NeutralMode.Coast);
    m_backRight.setNeutralMode(NeutralMode.Coast);

    // invert motors
    // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? left motor?
    m_shooterLeft.setInverted(true);
    rightDriveMotors.setInverted(true);

    m_shooterLeft.setNeutralMode(NeutralMode.Coast);
    m_shooterRight.setNeutralMode(NeutralMode.Coast);
    compressor.setClosedLoopControl(true);
    compressor.start();

    m_IntakeMotors.setNeutralMode(NeutralMode.Coast);
    // set solenoids to start position
    intakeSol.set(Value.kForward);
    hopperSol.set(Value.kForward);
    climberSol1.set(Value.kReverse);
    climberSol2.set(Value.kReverse);

    m_Climber1.setInverted(true);

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
  public void robotPeriodic() {
    double s1 = m_shooterLeft.getSelectedSensorVelocity();
    double s2 = m_shooterRight.getSelectedSensorVelocity();
  
    double speed = (s1 + s2) / 2;

    SmartDashboard.putNumber("RPM", speed);
  }

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
  public void teleopPeriodic() {
    if(driver.getRawAxis(RIGHT_Z) > .4){
      m_shooterLeft.set(ControlMode.PercentOutput, .7);
      m_shooterRight.set(ControlMode.PercentOutput, .7);
    } else {
      m_shooterLeft.set(ControlMode.PercentOutput, 0);
      m_shooterRight.set(ControlMode.PercentOutput, 0);
    }
    // intake on B
    if(driver.getRawButton(B_BUTTON)){
      intakeSol.set(Value.kReverse);
    }
    else{
      intakeSol.set(Value.kForward);
    }
    
    // hopper on A
    if(driver.getRawButton(A_BUTTON)){
      hopperSol.set(Value.kReverse);
    }
    else{
      hopperSol.set(Value.kForward);
    }
    
    // climber 1 on X
    if(driver.getRawButton(X_BUTTON)){
      climberSol1.set(Value.kForward);
      System.out.println("Pressed X");
    }
    
    else{
      climberSol1.set(Value.kReverse);
    }
    // climber 2 on Y
    if(driver.getRawButton(Y_BUTTON)){
      climberSol2.set(Value.kForward);
      System.out.println("Pressed Y");
    }
    else{
      climberSol2.set(Value.kReverse);
    }

    //Intake on B
    if(operator.getRawButton(B_BUTTON)){
      m_IntakeMotors.set(ControlMode.PercentOutput, .5);
     }
    else{
      m_IntakeMotors.set(ControlMode.PercentOutput, 0);
    }

    //Turret bumpers
    if(operator.getRawButton(RIGHT_BUMPER)){
      // m_TurretMotor.set(.1);
    }
    else if(operator.getRawButton(LEFT_BUMPER)){
      m_TurretMotor.set(-.1);
    }
    else {
      m_TurretMotor.set(0);
    }
    //Climbing motors
    if(driver.getPOV() == DPadDown){
      climberMotors.set(.6);
      System.out.println("down");
    }
    else{
      climberMotors.set(0);
    }
    if(driver.getRawAxis(LEFT_Y)  != 0){
      leftDriveMotors.set(-driver.getRawAxis(LEFT_Y) / 2);
    } else {
      leftDriveMotors.set(0);
      rightDriveMotors.set(0);
    }
    if(driver.getRawAxis(RIGHT_Y) != 0){
      rightDriveMotors.set(-driver.getRawAxis(RIGHT_Y) / 2);
    } else {
      leftDriveMotors.set(0);
      rightDriveMotors.set(0);
    }
  }

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