package de.fhg.iais.roberta.visitor;

import de.fhg.iais.roberta.bean.NNBean;
import de.fhg.iais.roberta.bean.UsedHardwareBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.inter.mode.action.ITurnDirection;
import de.fhg.iais.roberta.mode.action.DriveDirection;
import de.fhg.iais.roberta.syntax.Phrase;

// Display / Light
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.light.LedBrightnessAction;

// mBot2-specific actions
import de.fhg.iais.roberta.syntax.action.mbot2.CommunicationReceiveAction;
import de.fhg.iais.roberta.syntax.action.mbot2.CommunicationSendAction;
import de.fhg.iais.roberta.syntax.action.mbot2.DisplaySetColourAction;
import de.fhg.iais.roberta.syntax.action.mbot2.Mbot2RgbLedOffHiddenAction;
import de.fhg.iais.roberta.syntax.action.mbot2.Mbot2RgbLedOnHiddenAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PrintlnAction;
import de.fhg.iais.roberta.syntax.action.mbot2.QuadRGBLightOffAction;
import de.fhg.iais.roberta.syntax.action.mbot2.QuadRGBLightOnAction;
import de.fhg.iais.roberta.syntax.action.mbot2.Ultrasonic2LEDAction;

// Motors
import de.fhg.iais.roberta.syntax.action.motor.MotorGetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorSetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;

// Sound
import de.fhg.iais.roberta.syntax.action.sound.GetVolumeAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayFileAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayNoteAction;
import de.fhg.iais.roberta.syntax.action.sound.SetVolumeAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;

// Expressions
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;

// Generic sensors
import de.fhg.iais.roberta.syntax.sensor.generic.AccelerometerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderReset;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.GetLineSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.GyroSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerReset;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;

// mBot2-specific sensors
import de.fhg.iais.roberta.syntax.sensor.mbot2.GyroResetAxis;
import de.fhg.iais.roberta.syntax.sensor.mbot2.Joystick;
import de.fhg.iais.roberta.syntax.sensor.mbot2.QuadRGBSensor;
import de.fhg.iais.roberta.syntax.sensor.mbot2.SoundRecord;

import de.fhg.iais.roberta.util.basic.C;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.util.syntax.MotorDuration;
import de.fhg.iais.roberta.visitor.lang.codegen.AbstractStackMachineVisitor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Mbot2StackMachineVisitor extends AbstractStackMachineVisitor implements IMbot2Visitor<Void> {

    public Mbot2StackMachineVisitor(
            ConfigurationAst configuration,
            List<List<Phrase>> phrases,
            UsedHardwareBean usedHardwareBean,
            NNBean nnBean) {

        super(configuration, usedHardwareBean, nnBean);
        Assert.isTrue(!phrases.isEmpty());
    }


    // ============================================================
    // SENSORS
    // ============================================================

    @Override
    public Void visitKeysSensor(KeysSensor keysSensor) {
        String port = keysSensor.getUserDefinedPort();
        JSONObject o = makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.BUTTONS).put(C.MODE, port);
        return add(o);
    }

    @Override
    public Void visitJoystick(Joystick joystick) {
        String mode = joystick.getMode();
        String slot = joystick.getSlot();
        String port = joystick.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.JOYSTICK)
                .put(C.MODE, mode.toLowerCase())
                .put(C.SLOT, slot)
                .put(C.PORT, port)
                .put(C.NAME, "mbot2");
        return add(o);
    }

    @Override
    public Void visitEncoderSensor(EncoderSensor encoderSensor) {
        String mode = encoderSensor.getMode();
        String port = encoderSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.ENCODER_SENSOR_SAMPLE)
                .put(C.PORT, port)
                .put(C.MODE, mode.toLowerCase())
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitEncoderReset(EncoderReset encoderReset) {
        String port = encoderReset.sensorPort;

        JSONObject o = makeNode(C.ENCODER_SENSOR_RESET)
                .put(C.PORT, port)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitSoundSensor(SoundSensor soundSensor) {
        String mode = soundSensor.getMode();
        String port = soundSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.SOUND)
                .put(C.PORT, port)
                .put(C.MODE, mode.toLowerCase())
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitSoundRecord(SoundRecord soundRecord) {
        String mode = soundRecord.getMode();
        String port = soundRecord.getUserDefinedPort();

        JSONObject o = makeNode(C.SOUND_RECORD)
                .put(C.MODE, mode)
                .put(C.PORT, port)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitLightSensor(LightSensor lightSensor) {
        String mode = lightSensor.getMode();
        String port = lightSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.LIGHT)
                .put(C.PORT, port)
                .put(C.MODE, mode.toLowerCase())
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitGyroSensor(GyroSensor gyroSensor) {
        String mode = gyroSensor.getMode();
        String slot = gyroSensor.getSlot();
        String port = gyroSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.GET_GYRO_SENSOR_SAMPLE)
                .put(C.PORT, port)
                .put(C.MODE, mode.toLowerCase())
                .put(C.SLOT, slot)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitGyroResetAxis(GyroResetAxis gyroResetAxis) {
        String slot = gyroResetAxis.getSlot();
        String port = gyroResetAxis.getUserDefinedPort();

        JSONObject o = makeNode(C.GYRO_SENSOR_RESET)
                .put(C.SLOT, slot)
                .put(C.PORT, port)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitAccelerometerSensor(AccelerometerSensor accelerometerSensor) {
        String mode = accelerometerSensor.getMode();
        String slot = accelerometerSensor.getSlot();
        String port = accelerometerSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.ACCELEROMETER)
                .put(C.PORT, port)
                .put(C.MODE, mode.toLowerCase())
                .put(C.SLOT, slot)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitUltrasonicSensor(UltrasonicSensor ultrasonicSensor) {
        String mode = ultrasonicSensor.getMode();
        String port = ultrasonicSensor.getUserDefinedPort();
        JSONObject o = makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.ULTRASONIC).put(C.PORT, port).put(C.MODE, mode.toLowerCase()).put(C.NAME, "mbot2");
        return add(o);
    }

    @Override
    public Void visitQuadRGBSensor(QuadRGBSensor quadRGBSensor) {
        String mode = quadRGBSensor.getMode();
        String slot = quadRGBSensor.getSlot();
        String port = quadRGBSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.QUAD_RGB)
                .put(C.PORT, port)
                .put(C.MODE, mode.toLowerCase())
                .put(C.SLOT, slot)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitGetLineSensor(GetLineSensor getLineSensor) {
        String slot = "RIGHT".equals(getLineSensor.getSlot()) ? C.RIGHT : C.LEFT;
        String port = getLineSensor.getUserDefinedPort();

        JSONObject o = makeNode(C.GET_SAMPLE)
                .put(C.GET_SAMPLE, C.LINE)
                .put(C.PORT, port)
                .put(C.SLOT, slot)
                .put(C.NAME, "mbot2");

        return add(o);
    }

    @Override
    public Void visitTimerSensor(TimerSensor timerSensor) {
        String port = timerSensor.getUserDefinedPort();
        JSONObject o = makeNode(C.GET_SAMPLE).put(C.GET_SAMPLE, C.TIMER).put(C.PORT, port).put(C.NAME, "mbot2");
        return add(o);
    }

    @Override
    public Void visitTimerReset(TimerReset timerReset) {
        String port = timerReset.sensorPort;
        JSONObject o = makeNode(C.TIMER_SENSOR_RESET).put(C.PORT, port).put(C.NAME, "mbot2");
        return add(o);
    }



    // ============================================================
    // DISPLAY / LIGHT
    // ============================================================

    @Override
    public Void visitShowTextAction(ShowTextAction showTextAction) {

        showTextAction.y.accept(this);
        showTextAction.x.accept(this);
        showTextAction.msg.accept(this);

        JSONObject o = makeNode(C.SHOW_TEXT_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.MODE, C.TEXT)
                .put(C.PORT, showTextAction.port);

        return add(o);
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction clearDisplayAction) {
        JSONObject o = makeNode(C.CLEAR_DISPLAY_ACTION);
        return add(o);
    }

    @Override
    public Void visitDisplaySetColourAction(DisplaySetColourAction displaySetColourAction) {
        displaySetColourAction.color.accept(this);
        JSONObject o = makeNode(C.DISPLAY_SET_COLOUR_ACTION).put(C.NAME, "mbot2");
        return add(o);
    }

    @Override
    public Void visitMbot2RgbLedOnHiddenAction(Mbot2RgbLedOnHiddenAction mbot2RgbLedOnHiddenAction) {
        mbot2RgbLedOnHiddenAction.color.accept(this);
        JSONObject o = makeNode(C.RGBLED_ON_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.PORT, normalizeLedSlot(mbot2RgbLedOnHiddenAction.slot));
        return add(o);
    }

    @Override
    public Void visitMbot2RgbLedOffHiddenAction(Mbot2RgbLedOffHiddenAction mbot2RgbLedOffHiddenAction) {
        JSONObject o = makeNode(C.RGBLED_OFF_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.PORT, normalizeLedSlot(mbot2RgbLedOffHiddenAction.slot));
        return add(o);
    }

    @Override
    public Void visitLedBrightnessAction(LedBrightnessAction ledBrightnessAction) {
        ledBrightnessAction.brightness.accept(this);
        JSONObject o = makeNode(C.LED_BRIGHTNESS_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.PORT, ledBrightnessAction.getUserDefinedPort());
        return add(o);
    }

    @Override
    public Void visitQuadRGBLightOnAction(QuadRGBLightOnAction quadRGBLightOnAction) {
        quadRGBLightOnAction.color.accept(this);
        JSONObject o = makeNode(C.LIGHT_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.MODE, C.ON)
                .put(C.PORT, quadRGBLightOnAction.getUserDefinedPort());
        return add(o);
    }

    @Override
    public Void visitQuadRGBLightOffAction(QuadRGBLightOffAction quadRGBLightOffAction) {
        JSONObject o = makeNode(C.LIGHT_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.MODE, C.OFF)
                .put(C.PORT, quadRGBLightOffAction.getUserDefinedPort());
        return add(o);
    }

    private String normalizeLedSlot(String slot) {
        return slot.replace("LED", "").toLowerCase();
    }


    // ============================================================
    // ULTRASONIC / SPECIAL mBot2 ACTIONS
    // ============================================================

    @Override
    public Void visitUltrasonic2LEDAction(Ultrasonic2LEDAction ultrasonic2LEDAction) {
        ultrasonic2LEDAction.brightness.accept(this);
        JSONObject o = makeNode(C.ULTRASONIC2_LED_ACTION)
                .put(C.NAME, "mbot2")
                .put(C.PORT, ultrasonic2LEDAction.getUserDefinedPort())
                .put(C.SLOT, normalizeLedSlot(ultrasonic2LEDAction.getLedNumber()));
        return add(o);
    }


    // ============================================================
    // COMMUNICATION / OUTPUT
    // ============================================================

    @Override
    public Void visitCommunicationSendAction(CommunicationSendAction communicationSendAction) {
        // Unsupported in this simulation version: no virtual CyberPi broadcast channel bus exists.
        return null;
    }

    @Override
    public Void visitCommunicationReceiveAction(CommunicationReceiveAction communicationReceiveAction) {
        // Unsupported in this simulation version: no virtual CyberPi broadcast channel bus exists.
        return null;
    }

    @Override
    public Void visitPrintlnAction(PrintlnAction printlnAction) {
        // Unsupported in this simulation version: no simulated CyberPi console is available.
        return null;
    }


    // ============================================================
    // MOTORS
    // ============================================================

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction motorGetPowerAction) {
        String port = motorGetPowerAction.getUserDefinedPort();
        JSONObject o = makeNode(C.MOTOR_GET_POWER)
                .put(C.PORT, port.toLowerCase());
        return add(o);
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction motorSetPowerAction) {
        String port = motorSetPowerAction.port;

        motorSetPowerAction.power.accept(this);
        JSONObject o = makeNode(C.MOTOR_SET_POWER).put(C.PORT, port.toLowerCase());
        return add(o);
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction motorStopAction) {
        String port = motorStopAction.getUserDefinedPort();
        JSONObject o = makeNode(C.MOTOR_STOP).put(C.PORT, port.toLowerCase());
        return add(o);
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction motorOnAction) {
        motorOnAction.param.getSpeed().accept(this);
        MotorDuration duration = motorOnAction.param.getDuration();
        boolean speedOnly = !processOptionalDuration(duration);
        String port = motorOnAction.getUserDefinedPort();

        JSONObject o = makeNode(C.MOTOR_ON_ACTION).put(C.PORT, port.toLowerCase()).put(C.NAME, "mbot2").put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            if ( duration.getType() == null ) {
                o.put(C.SET_TIME, true).put(C.MOTOR_DURATION, C.TIME);
            } else {
                String durationType = duration.getType().toString().toLowerCase();
                o.put(C.SET_TIME, false).put(C.MOTOR_DURATION, durationType);
            }
            add(o);
            return add(makeNode(C.MOTOR_STOP).put(C.PORT, port.toLowerCase()));
        }
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction stopAction) {
        String port = stopAction.port;
        JSONObject o = makeNode(C.STOP_DRIVE)
                .put(C.NAME, "mbot2")
                .put(C.PORT, port);
        return add(o);
    }

    @Override
    public Void visitDriveAction(DriveAction driveAction) {
        driveAction.param.getSpeed().accept(this);
        String port = driveAction.port;
        boolean speedOnly = !processOptionalDuration(driveAction.param.getDuration());
        DriveDirection driveDirection = (DriveDirection) driveAction.direction;
        JSONObject o = makeNode(C.DRIVE_ACTION).put(C.DRIVE_DIRECTION, driveDirection).put(C.NAME, "mbot2").put(C.PORT, port).put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, false));
            return add(makeNode(C.STOP_DRIVE).put(C.NAME, "mbot2"));
        }
    }

    @Override
    public Void visitTurnAction(TurnAction turnAction) {
        turnAction.param.getSpeed().accept(this);
        String port = turnAction.port;
        boolean speedOnly = !processOptionalDuration(turnAction.param.getDuration());
        ITurnDirection turnDirection = turnAction.direction;
        JSONObject o =
                makeNode(C.TURN_ACTION).put(C.TURN_DIRECTION, turnDirection.toString().toLowerCase()).put(C.NAME, "mbot2").put(C.PORT, port).put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, false));
            return add(makeNode(C.STOP_DRIVE).put(C.NAME, "mbot2"));
        }
    }

    @Override
    public Void visitCurveAction(CurveAction curveAction) {
        curveAction.paramLeft.getSpeed().accept(this);
        curveAction.paramRight.getSpeed().accept(this);
        String port = curveAction.port;
        boolean speedOnly = !processOptionalDuration(curveAction.paramLeft.getDuration());
        DriveDirection driveDirection = (DriveDirection) curveAction.direction;

        JSONObject o = makeNode(C.CURVE_ACTION).put(C.DRIVE_DIRECTION, driveDirection).put(C.NAME, "mbot2").put(C.PORT, port).put(C.SPEED_ONLY, speedOnly);
        if ( speedOnly ) {
            return add(o.put(C.SET_TIME, false));
        } else {
            add(o.put(C.SET_TIME, false));
            return add(makeNode(C.STOP_DRIVE).put(C.NAME, "mbot2"));
        }
    }


    // ============================================================
    // SOUND
    // ============================================================

    @Override
    public Void visitToneAction(ToneAction toneAction) {
        toneAction.frequency.accept(this);
        toneAction.duration.accept(this);
        JSONObject o = makeNode(C.TONE_ACTION);
        return add(o);
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction playNoteAction) {
        String freq = playNoteAction.frequency;
        String duration = playNoteAction.duration;
        add(makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, freq));
        add(makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, duration));
        JSONObject o = makeNode(C.TONE_ACTION);
        return add(o);
    }

    @Override
    public Void visitGetVolumeAction(GetVolumeAction getVolumeAction) {
        return add(makeNode(C.GET_VOLUME));
    }

    @Override
    public Void visitSetVolumeAction(SetVolumeAction setVolumeAction) {
        setVolumeAction.volume.accept(this);
        return add(makeNode(C.SET_VOLUME_ACTION));
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction playFileAction) {
        // Unsupported in this simulation version: the mBot2 toolbox and Python generator do not provide file playback.
        return null;
    }

    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction playRecordingAction) {
        // Unsupported in this simulation version: browser recording capture and lifecycle support are not available.
        return null;
    }


    // ============================================================
    // EXPRESSIONS
    // ============================================================

    @Override
    public Void visitColorConst(ColorConst colorConst) {
        int r = colorConst.getRedChannelInt();
        int g = colorConst.getGreenChannelInt();
        int b = colorConst.getBlueChannelInt();

        JSONObject o = makeNode(C.EXPR).put(C.EXPR, "COLOR_CONST").put(C.VALUE, new JSONArray(Arrays.asList(r, g, b)));
        return add(o);
    }
}
