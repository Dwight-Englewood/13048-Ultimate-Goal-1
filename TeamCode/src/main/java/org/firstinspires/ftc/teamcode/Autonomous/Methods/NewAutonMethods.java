package org.firstinspires.ftc.teamcode.Autonomous.Methods;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Hardware.Movement;

import com.qualcomm.robotcore.util.Range;

public class NewAutonMethods {
    public DcMotor
            BL, BR, FL, FR, wobbleMotor, intake/*, outtake*/;

    public Servo
            wobbleServo;

    HardwareMap map;
    Telemetry tele;

    public ElapsedTime runtime = new ElapsedTime();

    public int command, target, current;
    private int originTick;
    public int origin;
    float curHeading;

    public double kpVal = 0.000045, kiVal = 0.0000004, kdVal = 0.00003;
    public double lastError, error = 0, errorI = 0, errorD = 0;

    public final double TkpVal = 0.02, TkiVal = 0.00002, TkdVal = 0.0006;
    public double TlastError, Terror = 0, TerrorI = 0, TerrorD = 0;

    public double wobbleMotorRadius = 30.5;

    public static BNO055IMU gyro;

    public boolean strafe = false, backLeft = false, backRight = false;

    public NewAutonMethods() {
        command = 0;
    }

    /**
     * inits hardware
     *
     * @param map creates object on phones config
     */

    public void initNew(HardwareMap map, Telemetry tele) {
        this.map = map;

        BR = this.map.get(DcMotor.class, "BR");
        BL = this.map.get(DcMotor.class, "BL");
        FL = this.map.get(DcMotor.class, "FL");
        FR = this.map.get(DcMotor.class, "FR");
        BR.setDirection(DcMotorSimple.Direction.REVERSE);
        BL.setDirection(DcMotorSimple.Direction.FORWARD);
        FL.setDirection(DcMotorSimple.Direction.FORWARD);
        FR.setDirection(DcMotorSimple.Direction.REVERSE);

        wobbleMotor = this.map.get(DcMotor.class, "wobbleMotor");
        wobbleMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        wobbleMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = this.map.get(DcMotor.class, "intake");
        intake.setDirection((DcMotorSimple.Direction.REVERSE));
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*

        outtake = this.map.get(DcMotor.class, "outtake");
        outtake.setDirection((DcMotorSimple.Direction.REVERSE));
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

         */

        wobbleServo = this.map.get(Servo.class, "wobbleServo");

        this.changeRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.closeWobbleServo();
    }

    public void initGyro() {
        gyro = map.get(BNO055IMU.class, "gyro");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        gyro.initialize(parameters);
        curHeading = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    public boolean isGyroInit() {
        return gyro.isGyroCalibrated();
    }

    /**
     * Changes the encoder state of all four motors.
     *
     * @param runMode a dc motor run mode.
     */
    public void changeRunMode(DcMotor.RunMode runMode) {
        BL.setMode(runMode);
        BR.setMode(runMode);
        FL.setMode(runMode);
        FR.setMode(runMode);
    }

    /**
     * @param in powerlevel
     */
    public void drive(double in) {
        BL.setPower(in);
        BR.setPower(in);
        FR.setPower(in);
        FL.setPower(in);
    }

    public void strafeDrive(double in) { //RightStrafe
        BL.setPower(-in);
        BR.setPower(in);
        FR.setPower(-in);
        FL.setPower(in);
    }

    public void autonDrive(Movement movement, int target) {
        switch (movement) {
            case FORWARD:
                FL.setTargetPosition(target);
                FR.setTargetPosition(target);
                BL.setTargetPosition(target);
                BR.setTargetPosition(target);
                backRight = false;
                backLeft = false;
                break;

            case BACKWARD:
                FL.setTargetPosition(-target);
                FR.setTargetPosition(-target);
                BL.setTargetPosition(-target);
                BR.setTargetPosition(-target);
                backRight = false;
                backLeft = false;
                break;

            case LEFTSTRAFE:
                FL.setTargetPosition(-target);
                FR.setTargetPosition(target);
                BL.setTargetPosition(target);
                BR.setTargetPosition(-target);
                strafe = true;
                backRight = false;
                backLeft = false;
                break;

            case RIGHTSTRAFE:
                FL.setTargetPosition(target);
                FR.setTargetPosition(-target);
                BL.setTargetPosition(-target);
                BR.setTargetPosition(target);
                strafe = true;
                backRight = false;
                backLeft = false;
                break;

            case UPRIGHT:
                FL.setTargetPosition(target);
                BR.setTargetPosition(target);

                FR.setTargetPosition(FR.getCurrentPosition());
                BL.setTargetPosition(BL.getCurrentPosition());
                backRight = true;
                backLeft = false;
                break;

            case UPLEFT:
                FR.setTargetPosition(target);
                BL.setTargetPosition(target);

                FL.setTargetPosition(FL.getCurrentPosition());
                BR.setTargetPosition(BR.getCurrentPosition());
                backLeft = true;
                backRight = false;
                break;

            case DOWNRIGHT:
                FR.setTargetPosition(-target);
                BL.setTargetPosition(-target);

                FL.setTargetPosition(FL.getCurrentPosition());
                BR.setTargetPosition(BR.getCurrentPosition());
                backLeft = true;
                backRight = false;
                break;

            case DOWNLEFT:
                FL.setTargetPosition(-target);
                BR.setTargetPosition(-target);

                FR.setTargetPosition(FR.getCurrentPosition());
                BL.setTargetPosition(BL.getCurrentPosition());
                backRight = true;
                backLeft = false;
                break;

            case LEFTTURN:
                FL.setTargetPosition(-target);
                FR.setTargetPosition(-target);
                BL.setTargetPosition(target);
                BR.setTargetPosition(target);
                break;

            case RIGHTTURN:
                FL.setTargetPosition(target);
                FR.setTargetPosition(target);
                BL.setTargetPosition(-target);
                BR.setTargetPosition(-target);
                break;

            case STOP:
                FL.setTargetPosition(FL.getCurrentPosition());
                FR.setTargetPosition(FR.getCurrentPosition());
                BL.setTargetPosition(BL.getCurrentPosition());
                BR.setTargetPosition(BR.getCurrentPosition());
                break;
        }
    }

    public void drive(Movement movement, double power) {
        switch (movement) {
            case FORWARD:
                FL.setPower(power);
                FR.setPower(power);
                BL.setPower(power);
                BR.setPower(power);
                break;

            case BACKWARD:
                FR.setPower(power);
                FL.setPower(power);
                BL.setPower(-power);
                BR.setPower(-power);
                break;

            case LEFTSTRAFE:
                FL.setPower(power);
                FR.setPower(-power);
                BL.setPower(-power);
                BR.setPower(power);
                break;

            case RIGHTSTRAFE:
                FL.setPower(-power);
                FR.setPower(power);
                BL.setPower(power);
                BR.setPower(-power);
                break;

            case UPRIGHT:
                FL.setPower(power);
                BR.setPower(power);

                FR.setPower(0);
                BL.setPower(0);
                break;

            case UPLEFT:
                FR.setPower(power);
                BL.setPower(power);

                FL.setPower(0);
                BR.setPower(0);
                break;

            case DOWNRIGHT:
                FR.setPower(-power);
                BL.setPower(-power);

                FL.setPower(0);
                BR.setPower(0);
                break;

            case DOWNLEFT:
                FL.setPower(-power);
                BR.setPower(-power);

                FR.setPower(0);
                BL.setPower(0);
                break;

            case LEFTTURN:
                FL.setPower(power);
                FR.setPower(-power);
                BL.setPower(power);
                BR.setPower(-power);
                break;

            case RIGHTTURN:
                FL.setPower(-power);
                FR.setPower(power);
                BL.setPower(-power);
                BR.setPower(power);
                break;

            case STOP:
                FL.setPower(0);
                FR.setPower(0);
                BL.setPower(0);
                BR.setPower(0);
                break;
        }
    }

    public double strafeVal(double target) {
        return (target * 1.2);
    }

    public void encoderReset() {
        this.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.runtime.reset();
        this.command++;
    }

    public void intakeReset() {
        this.intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.command++;
    }

    public void runtimeReset() {
        this.runtime.reset();
    }

    public void openWobbleServo() {
        this.wobbleServo.setPosition(1);
    }

    public void closeWobbleServo() {
        this.wobbleServo.setPosition(0);
    }

    public void raiseWobble(){
        this.wobbleMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.wobbleMotor.setTargetPosition(cmDistance(Math.PI * wobbleMotorRadius / 4));
        this.wobbleMotor.setPower(0.5);
        this.wobbleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void lowerWobble(){
        this.wobbleMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.wobbleMotor.setTargetPosition(cmDistance(Math.PI * wobbleMotorRadius / 4));
        this.wobbleMotor.setPower(-0.5);
        this.wobbleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void wobbleMotorReset(){ this.wobbleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);}

    public boolean adjustHeading(int targetHeading) {
        double headingError;
        curHeading = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
        headingError = targetHeading - curHeading;
        this.changeRunMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (headingError < -1 && headingError > -180)
            this.drive(Movement.LEFTTURN, 0.1 * scaleTurn(targetHeading));
        else if (headingError > 1 && headingError < 180) {
            this.drive(Movement.RIGHTTURN, 0.1 * scaleTurn(targetHeading));
        } else {
            return true;
        }
        return false;
    }
    //Positive is left turn, negative is right turn.

    public double scalePower() {
        if (!backLeft && !backRight) {
            target = cmDistance((Math.abs(BR.getTargetPosition()) + Math.abs(BL.getTargetPosition()) + Math.abs(FL.getTargetPosition()) + Math.abs(FR.getTargetPosition())) / 4);
            current = cmDistance((Math.abs(BR.getCurrentPosition()) + Math.abs(BL.getCurrentPosition()) + Math.abs(FL.getCurrentPosition()) + Math.abs(FR.getCurrentPosition())) / 4);
        } else if (!backRight && backLeft) {
            target = cmDistance(BL.getTargetPosition());
            current = cmDistance(BL.getCurrentPosition());
        } else if (!backLeft && backRight) {
            target = cmDistance(BL.getTargetPosition());
            current = cmDistance(BL.getCurrentPosition());
        } else {
            target = cmDistance((Math.abs(BR.getTargetPosition()) + Math.abs(BL.getTargetPosition()) + Math.abs(FL.getTargetPosition()) + Math.abs(FR.getTargetPosition())) / 4);
            current = cmDistance((Math.abs(BR.getCurrentPosition()) + Math.abs(BL.getCurrentPosition()) + Math.abs(FL.getCurrentPosition()) + Math.abs(FR.getCurrentPosition())) / 4);
        }

        /*if (Math.abs(this.BL.getTargetPosition() - this.BL.getCurrentPosition()) <= 100){
            kpVal *= 0.2;
        } else if (Math.abs(this.BL.getTargetPosition() - this.BL.getCurrentPosition()) <= 200){
            kpVal *= 0.5;
        } else {`
            kpVal = 0.000045;
        }
         */

        if (Math.abs(target - current) <= 0.01 * (Math.abs(target + current))) {
            kpVal *= 0.25;
        } else if (Math.abs(target - current) <= 0.1 * (Math.abs(target + current))) {
            kpVal *= 0.75;
        } else if (Math.abs(target - current) <= 0.25 * (Math.abs(target + current))) {
            kpVal *= 0.85;
        } else {
            kpVal = 0.000045;
        }

        if (Math.abs(target) < 15000) {
            kpVal *= 2.5;
        } else {
            kpVal = 0.000045;
        }

        if ((this.error * this.kpVal) + (this.errorI * this.kiVal) - (this.errorD * this.kdVal) >= .75) {
            this.errorI += 0;
        } else {
            this.errorI = this.errorI + this.error;
        }

        this.error = (target - current); //Distance from current position to end position
        this.errorD = (current - this.lastError);
        this.lastError = current;

        return Range.clip((this.error * this.kpVal) + (this.errorI * this.kiVal) - (this.errorD * this.kdVal), -1, 1);
    }

    public double scaleTurn(double targHeading) {
        curHeading = gyro.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;

        if ((this.Terror * this.TkpVal) + (this.TerrorI * this.TkiVal) - (this.TerrorD * this.TkdVal) > 1) {
            this.TerrorI += 0;
        } else {
            this.TerrorI = this.TerrorI + this.Terror;
        }

        this.Terror = (targHeading - curHeading); //Distance from current position to end position
        this.TerrorD = (curHeading - this.TlastError);
        this.TlastError = curHeading;

        return Math.abs(Range.clip((this.Terror * this.TkpVal) + (this.TerrorI * this.TkiVal) - (this.TerrorD * this.TkdVal), -1, 1));
    }

    public void finishDrive() {
        if (backLeft) {
            if ((Math.abs(this.BL.getTargetPosition() - this.BL.getCurrentPosition()) <= 25
                    || Math.abs(this.BR.getTargetPosition() - this.BR.getCurrentPosition()) <= 25
                    || Math.abs(this.FL.getTargetPosition() - this.FL.getCurrentPosition()) <= 25
                    || Math.abs(this.FR.getTargetPosition() - this.FR.getCurrentPosition()) <= 25) || (timerFailSafe() && tinyPowerValue())) {
                this.drive(Movement.STOP, 0);
                this.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                this.lastError = this.error;
                this.errorI = 0;
                this.error = 0;
                this.runtime.reset();
                this.command++;
            } else {
                this.drive(scalePower());
            }
        } else {
            if ((Math.abs(this.BL.getTargetPosition() - this.BL.getCurrentPosition()) <= 25
                    || Math.abs(this.BR.getTargetPosition() - this.BR.getCurrentPosition()) <= 25
                    || Math.abs(this.FL.getTargetPosition() - this.FL.getCurrentPosition()) <= 25
                    || Math.abs(this.FR.getTargetPosition() - this.FR.getCurrentPosition()) <= 25) || (timerFailSafe() && tinyPowerValue())) {
                this.drive(Movement.STOP, 0);
                this.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                this.lastError = this.error;
                this.errorI = 0;
                this.error = 0;
                this.runtime.reset();
                this.command++;
            } else {
                this.drive(scalePower());
            }
        }
    }

    public void gyroTurn(int turn) {
        if (adjustHeading(turn)) {
            this.drive(Movement.STOP, 0);
            this.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            this.TlastError = this.Terror;
            this.TerrorI = 0;
            this.Terror = 0;
            this.runtime.reset();
            this.command++;
        } else {
            adjustHeading(turn);
        }
    }

    public void setTarget(Movement movement, int target) {
        this.autonDrive(movement, cmDistance(target));
        this.changeRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.originTick = BL.getCurrentPosition();

        if (BL.getTargetPosition() == 0 && BL.getCurrentPosition() == 0) {
            this.originTick = cmDistance(BR.getTargetPosition());
        }
        this.command++;
    }

    public void setHeadingTarget(Movement movement, int target) {
        this.autonDrive(movement, cmDistance(target));
        this.changeRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.originTick = BL.getCurrentPosition();

        if (BL.getTargetPosition() == 0 && BL.getCurrentPosition() == 0) {
            this.originTick = cmDistance(BR.getTargetPosition());
        }
        this.command++;
    }

    public void targetHeadingWrapper(int turn, double targetHeading) {
        //   if (backLef) {
        if ((Math.abs(this.BL.getTargetPosition() - this.BL.getCurrentPosition()) <= 25
                && Math.abs(this.BR.getTargetPosition() - this.BR.getCurrentPosition()) <= 25
                && Math.abs(this.FL.getTargetPosition() - this.FL.getCurrentPosition()) <= 25
                && Math.abs(this.FR.getTargetPosition() - this.FR.getCurrentPosition()) <= 25) && adjustHeading(turn) || timerFailSafe()) {
            this.drive(Movement.STOP, 0);
            this.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            this.lastError = this.error;
            this.errorI = 0;
            this.error = 0;

            this.TlastError = this.Terror;
            this.TerrorI = 0;
            this.Terror = 0;
            this.runtime.reset();
            this.command++;
        } else {
            this.FL.setPower(scalePower() + scaleTurn(targetHeading));
            this.FR.setPower(scalePower() - scaleTurn(targetHeading));
            this.BL.setPower(scalePower() + scaleTurn(targetHeading));
            this.BR.setPower(scalePower() - scaleTurn(targetHeading));
        }
    /*    } else {
            if ((Math.abs(this.BL.getTargetPosition() - this.BL.getCurrentPosition()) <= 25
                    && Math.abs(this.BR.getTargetPosition() - this.BR.getCurrentPosition()) <= 25
                    && Math.abs(this.FL.getTargetPosition() - this.FL.getCurrentPosition()) <= 25
                    && Math.abs(this.FR.getTargetPosition() - this.FR.getCurrentPosition()) <= 25) && adjustHeading(turn)) {

                this.drive(Movement.STOP, 0);
                this.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

                this.lastError = this.error;
                this.errorI = 0;
                this.error = 0;

                this.TlastError = this.Terror;
                this.TerrorI = 0;
                this.Terror = 0;
                this.runtime.reset();
                this.command++;
            } else {
                this.FL.setPower(scalePower() - scaleTurn(targetHeading));
                this.FR.setPower(scalePower() + scaleTurn(targetHeading));
                this.BL.setPower(scalePower() - scaleTurn(targetHeading));
                this.BR.setPower(scalePower() + scaleTurn(targetHeading));
            }
        }
     */
    }

    public boolean timerFailSafe() {
        if (Math.abs(target) < 7500) {
            return runtime.milliseconds() > 700;
        } else if (Math.abs(target) < 15000) {
            return runtime.milliseconds() > 950;
        } else {
            return runtime.milliseconds() > 1500;
        }
    }

    public boolean tinyPowerValue() {
        return Math.abs(FL.getPower()) < 0.25 && Math.abs(FR.getPower()) < 0.25 && Math.abs(BL.getPower()) < 0.25 && Math.abs(BR.getPower()) < 0.25;
    }

    private int cmDistance(double distance) {
        final double wheelCirc = 31.9185813;
        final double gearMotorTick = 537.6; //neverrest orbital 20 = 537.6 counts per revolution
        //1:1 gear ratio so no need for multiplier
        return (int) (gearMotorTick * (distance / wheelCirc));
        //rate = x(0.05937236104)
    }

    }