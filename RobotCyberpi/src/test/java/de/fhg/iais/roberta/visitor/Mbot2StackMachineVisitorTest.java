package de.fhg.iais.roberta.visitor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Properties;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fhg.iais.roberta.factory.RobotFactory;
import de.fhg.iais.roberta.mode.action.DriveDirection;
import de.fhg.iais.roberta.mode.action.MotorMoveMode;
import de.fhg.iais.roberta.mode.action.TurnDirection;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyExpr;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.basic.C;
import de.fhg.iais.roberta.util.PluginProperties;
import de.fhg.iais.roberta.util.Util;
import de.fhg.iais.roberta.util.syntax.MotionParam;
import de.fhg.iais.roberta.util.syntax.MotorDuration;

public class Mbot2StackMachineVisitorTest {

    @BeforeClass
    public static void setupRobotFactory() {
        Properties properties = Util.loadPropertiesRecursively("classpath:/mbot2.properties");
        new RobotFactory(new PluginProperties("mbot2", "", "", properties));
    }

    @Test
    public void turnDegreesAreNotGeneratedAsTime() {
        Mbot2StackMachineVisitor visitor = visitor();
        MotionParam param = motion(50, new MotorDuration(MotorMoveMode.DEGREE, number(90)));

        visitor.visitTurnAction(new TurnAction(TurnDirection.LEFT, param, "_D", null, properties()));

        assertFalse(operation(visitor, C.TURN_ACTION).getBoolean(C.SET_TIME));
    }

    @Test
    public void driveDistanceIsNotGeneratedAsTime() {
        Mbot2StackMachineVisitor visitor = visitor();
        MotionParam param = motion(50, new MotorDuration(MotorMoveMode.DISTANCE, number(20)));

        visitor.visitDriveAction(new DriveAction(DriveDirection.FOREWARD, param, "_D", null, properties()));

        assertFalse(operation(visitor, C.DRIVE_ACTION).getBoolean(C.SET_TIME));
    }

    @Test
    public void curveDistanceIsNotGeneratedAsTime() {
        Mbot2StackMachineVisitor visitor = visitor();
        MotorDuration distance = new MotorDuration(MotorMoveMode.DISTANCE, number(20));
        MotionParam left = motion(40, distance);
        MotionParam right = motion(60, distance);

        visitor.visitCurveAction(new CurveAction(DriveDirection.FOREWARD, left, right, "_D", null, properties()));

        assertFalse(operation(visitor, C.CURVE_ACTION).getBoolean(C.SET_TIME));
    }

    @Test
    public void motorDegreesAreNotGeneratedAsTime() {
        Mbot2StackMachineVisitor visitor = visitor();
        MotionParam param = motion(50, new MotorDuration(MotorMoveMode.DEGREE, number(180)));

        visitor.visitMotorOnAction(new MotorOnAction("L", param, properties()));

        assertFalse(operation(visitor, C.MOTOR_ON_ACTION).getBoolean(C.SET_TIME));
    }

    @Test
    public void motorTimeIsGeneratedAsTime() {
        Mbot2StackMachineVisitor visitor = visitor();
        MotionParam param = motion(50, new MotorDuration(null, number(1000)));

        visitor.visitMotorOnAction(new MotorOnAction("L", param, properties()));

        assertTrue(operation(visitor, C.MOTOR_ON_ACTION).getBoolean(C.SET_TIME));
    }

    private Mbot2StackMachineVisitor visitor() {
        return new Mbot2StackMachineVisitor(null, Collections.singletonList(Collections.<Phrase>emptyList()), null, null);
    }

    private MotionParam motion(int speed, MotorDuration duration) {
        return new MotionParam.Builder().speed(number(speed)).duration(duration).build();
    }

    private Expr number(int value) {
        return new EmptyExpr(BlocklyType.NUMBER);
    }

    private BlocklyProperties properties() {
        return BlocklyProperties.make("math_number", "1", null);
    }

    private JSONObject operation(Mbot2StackMachineVisitor visitor, String opcode) {
        return visitor.getCode().stream().filter(operation -> opcode.equals(operation.optString(C.OPCODE))).findFirst().orElseThrow(AssertionError::new);
    }
}
