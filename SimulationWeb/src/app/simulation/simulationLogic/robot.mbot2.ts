import { RobotBase, SelectionListener } from 'robot.base';
import { Interpreter } from 'interpreter.interpreter';
import { ChassisMobile, CyberPi, CyberPiRGBLeds, CyberPiRobotView, Mbot2Shield, MbotChassis, WebAudio } from 'robot.actuators';
import { CyberPiControls, DistanceSensor, GyroSensorExt, Mbot2UltrasonicSensor, QuadRGBSensor } from 'robot.sensors';
import { Pose, RobotBaseMobile } from 'robot.base.mobile';

export default class RobotMbot2 extends RobotBaseMobile {
    chassis: ChassisMobile;
    private shield: Mbot2Shield;
    private cyberPi: CyberPi;
    private cyberPiControls: CyberPiControls;
    private cyberPiRobotView: CyberPiRobotView;
    private cyberPiRGBLeds: CyberPiRGBLeds;
    volume: number = 0.5;
    private webAudio: WebAudio = new WebAudio();
    
    override readonly imgList = ['mbot2-bg-img-1.png', 'mbot2-bg-img-2.png', 'drawBackground', 'robertaBackground', 'rescueBackground', 'blank', 'mathBackground'];

    // mBot2-specific components
    // private accelerometer: ...;
    // private microphone: ...;
    // private lightSensor: ...;
    // private joystick: ...;
    // private gyro: ...;
    // private buttons: ...;
    // private encoderMotorLeft: ...;
    // private encoderMotorRight: ...;

    override updateActions(robot: RobotBase, dt: number, interpreterRunning: boolean): void {
        super.updateActions(robot, dt, interpreterRunning);
        const volume = this.interpreter.getRobotBehaviour().getActionState('volume', true);
        if (volume !== undefined && volume !== null) {
            this.volume = volume / 100;
        }
    }

    override reset(): void {
        super.reset();
        this.volume = 0.5;
    }

    override destroy(): void {
        if (this.cyberPiRobotView) {
            this.cyberPiRobotView.destroy();
        }
        super.destroy();
    }

    protected override configure(configuration: object): void {
        /*
         * mBot2 has the same differential-drive geometry as mBot.
         */
        configuration['TRACKWIDTH'] = 11.5;
        configuration['WHEELDIAMETER'] = 6.5;

        this.chassis = new MbotChassis(this.id, configuration, 3, this.pose);

        const horizontalUnitsPerCentimetre = this.chassis.geom.w / 12.3;
        const verticalUnitsPerCentimetre = this.chassis.geom.h / 9.2;
        const chassisCentreX = this.chassis.geom.x + this.chassis.geom.w / 2;

        // The mBot2 Shield is 10.5 cm x 8.0 cm and covers most of the blue chassis.
        const shieldWidth = 10.5 * horizontalUnitsPerCentimetre;
        const shieldHeight = 8.0 * verticalUnitsPerCentimetre;
        const shieldX = chassisCentreX - shieldWidth / 2;
        const shieldY = -shieldHeight / 2;
        this.shield = new Mbot2Shield(shieldX, shieldY, shieldWidth, shieldHeight);

        // CyberPi uses the rear 3.35 cm section of the shield and spans its full width.
        const cyberPiWidth = 3.35 * horizontalUnitsPerCentimetre;
        const cyberPiHeight = shieldHeight;
        const cyberPiX = shieldX;
        const cyberPiY = shieldY;
        this.cyberPi = new CyberPi(cyberPiX, cyberPiY, cyberPiWidth, cyberPiHeight);
        this.cyberPiControls = new CyberPiControls();
        this.cyberPiRGBLeds = new CyberPiRGBLeds(this.cyberPi.getRgbLedPositions());
        this.cyberPiRobotView = new CyberPiRobotView(this.id, this.cyberPiControls, this.cyberPiRGBLeds);

        const configuredSensors: { [key: string]: any } = configuration['SENSORS'];
        const sensors: { [key: string]: any } = { ...configuredSensors };

        for (const port in configuredSensors) {
            const component = configuredSensors[port];
            if (component['TYPE'] === 'MBUILD_PORT' && component['SUBCOMPONENTS']) {
                Object.assign(sensors, component['SUBCOMPONENTS']);
            }
        }

        for (const c in sensors) {
            switch (sensors[c]['TYPE']) {
                case 'GYRO': {
                    this[c] = new GyroSensorExt(c, 0, 0, 0);
                    break;
                }
                case 'MBUILD_ULTRASONIC2':
                case 'ULTRASONIC': {
                    let myUltraSensors = [];
                    let mbot2 = this;

                    Object.keys(this).forEach((x) => {
                        if (mbot2[x] && mbot2[x] instanceof DistanceSensor) {
                            myUltraSensors.push(mbot2[x]);
                        }
                    });

                    const ord = myUltraSensors.length + 1;

                    const num = Object.keys(sensors).filter(
                        (sensor) => sensors[sensor]['TYPE'] == 'ULTRASONIC' || sensors[sensor]['TYPE'] == 'MBUILD_ULTRASONIC2'
                    ).length;

                    let position: Pose = new Pose(this.chassis.geom.x + this.chassis.geom.w, 0, 0);

                    if (num == 3) {
                        if (ord == 1) {
                            position = new Pose(this.chassis.geom.h / 2, -this.chassis.geom.h / 2, -Math.PI / 4);
                        } else if (ord == 2) {
                            position = new Pose(this.chassis.geom.h / 2, this.chassis.geom.h / 2, Math.PI / 4);
                        }
                    } else if (num % 2 === 0) {
                        switch (ord) {
                            case 1:
                                position = new Pose(this.chassis.geom.x + this.chassis.geom.w, -this.chassis.geom.h / 2, -Math.PI / 4);
                                break;

                            case 2:
                                position = new Pose(this.chassis.geom.x + this.chassis.geom.w, this.chassis.geom.h / 2, Math.PI / 4);
                                break;

                            case 3:
                                position = new Pose(this.chassis.geom.x, -this.chassis.geom.h / 2, (-3 * Math.PI) / 4);
                                break;

                            case 4:
                                position = new Pose(this.chassis.geom.x, this.chassis.geom.h / 2, (3 * Math.PI) / 4);
                                break;
                        }
                    }

                    this[c] = new Mbot2UltrasonicSensor(c, position.x, position.y, position.theta, 255);

                    break;
                }

                case 'MBUILD_QUADRGB':
                case 'QUADRGB': {
                    // The chassis geometry is expressed in simulation units,
                    // while the physical QuadRGB dimensions are in centimetres.
                    // Position the probe row at the front of the chassis and
                    // convert its lateral measurements with the chassis scale.
        const frontEdge = this.chassis.geom.x + this.chassis.geom.w;
        this[c] = new QuadRGBSensor(c, [
            { x: frontEdge, y: -2.78 * verticalUnitsPerCentimetre },
            { x: frontEdge, y: -0.4625 * verticalUnitsPerCentimetre },
            { x: frontEdge, y: 0.4625 * verticalUnitsPerCentimetre },
            { x: frontEdge, y: 2.78 * verticalUnitsPerCentimetre },
        ]);
                    break;
                }
            }
        }
    }
}
