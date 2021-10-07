// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.Math;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
// import edu.wpi.first.networktables.TableEntryListener;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Servo;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;

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
  final double x1 = -0.0000000025291;
  final double x2 = 0.0000334240538;
  final double x3 = -0.1545379987062;
  final double b = 315.5170993015826;
  final double heightOfShooter = 38;
  final double heightOfTower = 98;
  final double heightdiff = heightOfTower - heightOfShooter;
  final double minAngle = 25;
  final double maxAngle = 65;
  final double maxPosition = 0;
  final double minPosition = 1;
  final double m1 = -(maxPosition - minPosition) / (maxAngle - minAngle);
  final double b1 = -.625;
  final double limitTurret = 20;
  final double limitServo = .7;
  final double hoodOffset = 30;
  final double pi = 3.14159265358979323846;

  private final ColorMatch m_colorMatcher = new ColorMatch();

  private final Color kBlueTarget = ColorMatch.makeColor(0,0,1);
  private final Color kGreenTarget = ColorMatch.makeColor(0,1,0);
  private final Color kRedTarget = ColorMatch.makeColor(1,0,0);
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);



  // public var for shooter PID
  double v1;
  double v2;
  int PCM1 = 0;
  int PCM2 = 1;
  double servoPos = 0;


  // initialize timer
  Timer timer = new Timer();

  // init compressor
  Compressor compressor = new Compressor();

  DoubleSolenoid intakeSol = new DoubleSolenoid(PCM1, 6, 1);
  DoubleSolenoid climberSol2 = new DoubleSolenoid(PCM1, 7, 0);
  DoubleSolenoid climberSol1 = new DoubleSolenoid(PCM2, 4, 3);
  DoubleSolenoid hopperSol = new DoubleSolenoid(PCM1, 5, 2);


  //                         not right ?????????????????????????????????????????????????????????????
  Servo servos = new Servo(1);

  // initialize  talon motors
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

  // initialize neo motors
  CANSparkMax m_turretMotor = new CANSparkMax(13, MotorType.kBrushless);

  // init speed controller groups
  SpeedControllerGroup shooterMotors = new SpeedControllerGroup(m_shooterLeft, m_shooterRight);

  WPI_TalonSRX m_IntakeMotors = new WPI_TalonSRX(11);
  // PID object
  PIDController pid = new PIDController(.0045, .0, 0, 100);

  CANSparkMax m_Climber1 = new CANSparkMax(16, MotorType.kBrushless);
  CANSparkMax m_Climber2 = new CANSparkMax(15, MotorType.kBrushless);
  SpeedControllerGroup climberMotors = new SpeedControllerGroup(m_Climber1, m_Climber2);

  private final I2C.Port i2cPort = I2C.Port.kOnboard;

  ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

  WPI_TalonSRX m_hopperLeft = new WPI_TalonSRX(9);
  WPI_TalonSRX m_hopperRight = new WPI_TalonSRX(10);
  SpeedControllerGroup hopperMotors = new SpeedControllerGroup(m_hopperLeft, m_hopperRight);

  // controllers
  Controller driver = new Controller(0);
  Controller operator = new Controller(1);

  VisionManager VisionManager = new VisionManager();

  UsbCamera camera1;
  NetworkTableEntry cameraSelection;
  NetworkTableEntry limelight;

  @Override
  public void robotInit() {

    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    // m_colorMatcher.addColorMatch(kYellowTarget);

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


    servos.set(0);
    servos.setBounds(2.0, 1.8, 1.5, 1.2, 1.0);

    m_IntakeMotors.set(ControlMode.PercentOutput, 0);

    // set neutral mode
    m_frontLeft.setNeutralMode(NeutralMode.Coast);
    m_frontRight.setNeutralMode(NeutralMode.Coast);
    // m_middleLeft.setNeutralMode(NeutralMode.Brake);
    // m_middleRight.setNeutralMode(NeutralMode.Brake);
    m_backLeft.setNeutralMode(NeutralMode.Coast);
    m_backRight.setNeutralMode(NeutralMode.Coast);

    camera1 = CameraServer.getInstance().startAutomaticCapture(0);
    // cameraSelection = NetworkTableInstance.getDefault().getTable("").getEntry("CameraSelection");

    // NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");


    // NetworkTableInstance.GetDefault().GetTable("limelight").PutNumber("ledMode", 3);
    m_shooterLeft.setNeutralMode(NeutralMode.Coast);
    m_shooterRight.setNeutralMode(NeutralMode.Coast);

    // invert motors
    m_shooterLeft.setInverted(true);
    rightDriveMotors.setInverted(true);
    m_hopperLeft.setInverted(true);

    // start compressor
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

  @Override
  public void robotPeriodic() {

    // assign variable to shooter motor speed
    double s1 = m_shooterLeft.getSelectedSensorVelocity();
    double s2 = m_shooterRight.getSelectedSensorVelocity();

    // average speed between two shooter motors
    double speed = (s1 + s2) / 2;

    // output shooter motor speed to smartdashboard
    SmartDashboard.putNumber("RPM", speed);
    SmartDashboard.putNumber("RPM2", speed);

    VisionManager.Update();

  }

  @Override
  public void autonomousInit() {

    // start timer
    timer.start();

    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

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

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {

    getColor();
    PositionHood();
    // TurretMove();
    VisionManager.Update();
    Drive();
    HopperRun();
    ShooterRun();
    IntakeRun();
    Climb();
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



  /* ------------------- FUNCTIONS --------------- */
    void Drive() {
        if (Math.abs(driver.L()) > .1) {
            leftDriveMotors.set(-driver.L() / 2);
        } else {
            leftDriveMotors.set(0);
        }
        if (Math.abs(driver.R()) > .1) {
            rightDriveMotors.set(-driver.R() / 2);
        } else {
            rightDriveMotors.set(0);
        }
    }

    void TurretMove() {
        if (operator.RB()) {
            m_turretMotor.set(.1);
        } else if(operator.LB()) {
            m_turretMotor.set(-.1);
        } else {
            AutoAim();
        }
    }

    void HopperRun() {
        if (operator.POVDown()) {
            hopperMotors.set(.4);
        } else if(operator.POVUp()) {
            hopperMotors.set(-.4);
            hopperSol.set(Value.kForward);
        } else {
            hopperMotors.set(0);
            hopperSol.set(Value.kReverse);
        }
    }

    void ShooterRun() {
        // shooter on driver right trigger
        if (operator.RT() > .4) {
            shooterMotors.set(.7);
        } else {
            shooterMotors.set(0);
        }
    }


    void IntakeRun() {
        // intake on B
        if (operator.B()) {
            intakeSol.set(Value.kReverse);
            m_IntakeMotors.set(ControlMode.PercentOutput, .5);
        } else {
            intakeSol.set(Value.kForward);
            m_IntakeMotors.set(ControlMode.PercentOutput, 0);
        }
    }

    void Climb() {
    // climber 2 on Y
        if (driver.Y()) {
            climberSol2.set(Value.kForward);
            climberSol1.set(Value.kForward);
        }
        if (driver.POVDown()) {
            climberMotors.set(.6);
        } else {
            climberMotors.set(0);
        }
    }

    void AutoAim(){
    //   NetworkTableInstance.getDefault().getTable("limelight");
        double l = leftDriveMotors.get();
        double r = rightDriveMotors.get();

        double s = (VisionManager.Update()/125) - (l/90) + (r/90);

        m_turretMotor.set(s);
    }

    double CalculateDistance(double area){
        double r = x1*java.lang.Math.pow(area, 3) + x2*java.lang.Math.pow(area, 2) +x3*area + b;
        return r;
    }

    void PositionHood(){
        double sShort = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tshort").getDouble(1);
        double sLong = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tlong").getDouble(1);
        double os = SmartDashboard.getNumber("Hood Angle Adjust", hoodOffset);
        double area = sLong * sShort;
        // std::cout << "Total area: " << area << "\n";
        System.out.println(CalculateDistance(area) + "inches\n");
        double a1 = java.lang.Math.atan2(heightdiff, CalculateDistance(area)) * (180/pi);
        // std::cout << "a1 " << a1 << "\n";
        double a2 = 90 - a1 - os;
        // std::cout << "a2 " << a2 << "\n";
        double p = (1 / (maxAngle - minAngle))*(a2-maxAngle) + 1;
        // std::cout << "servo position" << p << "\n";
        if (p >= .7) {
            p = .7;
        }
        servos.set(p);;
    }

    double CalculateAngle(double distance){
        double t = java.lang.Math.atan2(heightdiff , distance);
        double d = t * (180 / pi);
        double corrected_d = (90 - d - 25);
        double r = m1 * corrected_d  + b1;
        if (corrected_d > 64) {
            r = limitServo;
        } else if (corrected_d < 26) {
            r = 0;
        }
        return r;
    }

    void getColor(){
        Color detectedColor = colorSensor.getColor();

        // System.out.println("asdfasdf");

        String colorString;
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

        if (match.color == kBlueTarget) {
            colorString = "Blue";
        } else if (match.color == kRedTarget) {
            colorString = "Red";
        } else if (match.color == kGreenTarget) {
            colorString = "Green";
        } else if (match.color == kYellowTarget) {
            colorString = "Yellow";
        } else {
            colorString = "Unknown";
        }
        // System.out.println(colorString);
        System.out.println("RED: " + detectedColor.red + "GREEN: " +  detectedColor.green + "BLUE: " + detectedColor.blue);
        SmartDashboard.putString("Detected Color", colorString);
    }
}
