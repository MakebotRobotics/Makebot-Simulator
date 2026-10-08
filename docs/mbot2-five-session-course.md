# mBot2 Simulator Foundations

## Course design document

**Audience:** one mixed cohort of Grades 6–12. This is one shared curriculum, not separate grade tracks.  
**Format:** five classes × 90 minutes = **7.5 instructional hours**.  
**Primary platform:** Makebot Simulator / Open Roberta-style block environment, using **mBot2**.  
**Teaching approach:** simulator-first, then optionally run selected programs on physical mBot2 robots.  
**End product:** an autonomous mBot2 mission that uses movement, control logic, sensor input, feedback, and testing evidence.

## Executive recommendation

Use the same essential challenge for every learner, with optional extensions rather than age-based separation. The course should deliberately avoid trying to teach every available mBot2 block. In 7.5 hours, students can genuinely learn to build, run, observe, debug, and explain a sensor-driven robot; trying to also master lists, messaging, neural networks, manual source editing, and every sensor would weaken that outcome.

The recommended sequence is:

1. **Navigation and first simulation** — learn the workspace and make a robot perform a short route.
2. **Programming a purposeful robot** — actions, sequence, loops, conditions, variables, and a first procedure.
3. **Sensing the world** — ultrasonic distance and Quad RGB / line sensing; turn readings into a simple reaction.
4. **Reliable autonomous behaviour** — combine sensing, timing/encoders/gyro, feedback, and debugging.
5. **Capstone: Autonomous Delivery Rover** — build, test, improve, and explain a complete robot mission.

That order protects the most important learning progression: **command → repetition/decision → sensing → feedback system → independent design**.

## At-a-glance course table

Each session ends with a small, saveable project. These can be completed in the final 10–20 minutes of class, or assigned as a short “finish and improve” task where students can access the simulator at home. They are deliberately cumulative: the final project reuses ideas from every previous class.

| Class | Objective | Topics students learn | Learning outcome | End-of-class mini-project / homework |
| --- | --- | --- | --- | --- |
| **1. Meet the robot lab** | Become independently comfortable using the simulator and make a first mBot2 behaviour. | Login/class workspace; choosing mBot2; toolbox versus workspace; drag, connect, edit and delete blocks; save/load; simulation start, stop and reset; basic drive, sound/light/display; generated Python view; brief mBot2–EV3 comparison. | Students can create, save, run, stop, and explain a short block program. They understand that the program gives the robot instructions in sequence. | **Robot Hello Route:** drive from a start point to a marked zone, then play a tone or show a coloured light/message and stop. Change one number, predict the change, then test it. |
| **2. Make behaviour repeat and choose** | Use control logic so behaviour is not just a one-time sequence. | Repeat and forever loops; waits; if and if-else; true/false comparisons; arithmetic; variables; optional first procedure/function; debugging nested blocks. | Students can use a loop and a decision to control robot behaviour, and explain why the condition takes one path rather than another. | **Patrol Bot:** repeat a movement pattern three times; use a condition to select one of two celebration signals. Stretch: store speed or turn angle in a variable, or create a `celebrate` procedure. |
| **3. Sense distance and surface/colour** | Turn sensor readings into useful autonomous reactions. | Inputs versus outputs; ultrasonic distance; Quad RGB / line/colour sensing; thresholds; comparisons; calibration; repeat-forever sensor checks; test logs. | Students can read a sensor value, compare it to a chosen condition, and make the robot react differently to the result. | **Sensor Scout:** avoid a nearby obstacle using ultrasonic distance, then stop and signal when a target line/colour/delivery zone is detected. Record at least two threshold tests. |
| **4. Build reliable autonomous behaviour** | Combine sensors and feedback so the robot can handle more than one situation. |   | Students can construct and test a multi-step, two-sensor autonomous behaviour and improve it using evidence from failed tests. | **Smart Navigator:** drive until it finds the delivery zone; avoid obstacles first, recover by turning, and provide different LED/sound/display signals for driving, warning, and success. Stretch: use encoder, gyro, timer, or a `turnAway` procedure. |
| **5. Design, test, and explain** | Independently apply the full programming cycle in a complete robot mission. | Mission planning; algorithm sketching; implementation; simulation testing; iteration; peer feedback; presentation; blocks-to-Python recognition; limitations and next steps. | Students can demonstrate a complete autonomous mBot2 solution, justify its logic with test evidence, and explain one improvement. | **Autonomous Delivery Rover capstone:** leave start, avoid an obstacle, identify/reach delivery zone, signal delivery, and stop safely. Include a loop, condition, two sensors, an output, and a variable or procedure; document three tests. |

### Suggested homework rules

- Make every mini-project **optional to finish at home** unless every student has reliable simulator access. In class, all students should at least save a working first version.
- Set one **required core criterion** and one **optional stretch criterion**. This keeps the same assignment fair across Grades 6–12.
- Ask for a saved program plus a short “prediction / test / change” note, rather than screenshots only. The explanation reveals understanding.
- Reuse mini-projects as capstone components: Class 2 provides a patrol/recovery routine, Class 3 provides sensor logic, and Class 4 provides the navigation structure.

## What this project currently supports

The course has been designed against the mBot2 resources in this repository, rather than assuming a generic robotics platform.

| Capability | How it should be used in the course | Evidence in this project |
| --- | --- | --- |
| mBot2 program levels | Start in **Beginner**; move learners who need more power to **Expert** in sessions 4–5. | `RobotCyberpi/src/main/resources/mbot2/program.toolbox.beginner.xml` and `program.toolbox.expert.xml` |
| Movement | Drive for distance, continuous drive, stop, turns, and curves. | Both mBot2 toolboxes |
| Feedback / outputs | Screen text, sound tone/note/volume, RGB LEDs and brightness. | Beginner has print, tone/note, LEDs; Expert adds display and more outputs. |
| Sensors | Ultrasonic distance, Quad RGB, sound, joystick, buttons, gyro, timer, and encoders. Expert additionally exposes light, accelerometer, line, and sound recording blocks. | mBot2 toolboxes and default configuration |
| Core CS | If/if-else, repeat and forever, wait, comparison, Boolean logic, arithmetic, and variables. Expert adds while/until, for loops, procedures, lists, and richer math. | mBot2 toolboxes |
| Configuration | The default mBot2 configuration includes the chassis, two encoder motors, CyberPi display/buzzer/RGB light, buttons, joystick, light, microphone, accelerometer, gyro, and the ultrasonic and Quad RGB modules. | `RobotCyberpi/src/main/resources/mbot2/configuration.default.xml` |
| Simulation | The project contains a dedicated mBot2 simulator with mobile chassis, CyberPi display/LED rendering, ultrasonic sensor placement, and Quad RGB sensing. | `SimulationWeb/src/app/simulation/simulationLogic/robot.mbot2.ts` |
| Source-code view | The mBot2 workflow generates **Python** source; the UI can show a generated-code view/editor and download it. | `mbot2.properties`, `Mbot2PythonGeneratorWorker.java`, `sourceCodeEditor.controller.ts` |

### Important scope decisions

- Teach **blocks as the authoring language**. Treat generated Python as a “code window”: students notice sequence, indentation, variables, and conditions, but do not hand-edit Python in this first course.
- Use **ultrasonic distance** and **Quad RGB / line-related sensing** as the two anchor sensor experiences. They are concrete and visibly meaningful in a simulator.
- Use gyro, encoder, timer, light, microphone, buttons, joystick, accelerometer, procedures, and variables only when they strengthen the mission. Do not make all of them mandatory.
- Mention EV3 in session 1 only as a comparison: both are programmable robots with motors, sensors, and block code, but their ports, blocks, and hardware are different. All assessed work in this pilot is mBot2.
- The source editor supports importing/downloading/running source, but source editing is intentionally excluded from the core path because its changes do not teach the block-to-simulation cycle and can create mismatches for beginners.

## Learning outcomes

By the end, every student should be able to:

1. Sign in or enter the assigned class workspace, select mBot2, name and save a program, and recover a saved program.
2. Drag, connect, edit, duplicate, delete, and comment on blocks; distinguish the toolbox from the workspace.
3. Start, stop, reset, and observe a simulation; use observed behaviour to revise a program.
4. Program controlled movement and at least one output (screen, sound, or light).
5. Use a loop and an if/if-else decision to make behaviour repeat or change.
6. Read at least two sensors and use sensor values in a comparison or wait condition.
7. Use a variable or a named procedure in a meaningful way.
8. Test against stated success criteria, identify one bug or limitation, and explain one improvement.
9. Read generated Python at a recognition level: identify an action, a loop, a condition, and a variable without needing to write Python.

## Mixed-age design: same mission, different depth

All learners work from the same prompt and core blocks. Differentiation happens through **optional success levels**, never through a separate Grade 6–8 versus Grade 9–12 lesson.

| Level | Expected evidence | Suitable extension |
| --- | --- | --- |
| Core | Program works for the stated route or reaction; student can explain it in plain language. | Change speed, distance, colour, or threshold and predict the result. |
| Secure | Uses a loop and an if/if-else correctly; tests more than once and records a revision. | Store a threshold or counter in a variable. |
| Stretch | Decomposes repeated behaviour with a procedure and improves reliability after a failed test. | Use encoder/gyro/timer feedback, a while/until loop, or a second sensor. |
| Challenge | Can justify a design trade-off from test data and read the matching generated Python. | Add a state variable, calibration routine, or a robust recovery behaviour. |

This gives older or more experienced learners real depth without racing younger learners into syntax or abstract mathematics. Pair programming roles — **driver**, **navigator**, and **tester/explainer** — should rotate each session.

## Instructor preparation

### Before the course starts

1. Create or verify student accounts or a class/group login. Have a fallback guest/demo route if account creation fails.
2. Confirm that mBot2 can be selected and that a two-block drive program launches in the simulator on the actual student devices and browsers.
3. Prepare a one-page “first login” card with the exact local URL, account naming convention, password/reset process, and where students save work.
4. Prepare a shared starter program for each session and a short visual challenge card. Avoid distributing full block-by-block solutions before students attempt the task.
5. Use the default mBot2 configuration for sessions 1–3. Before session 4, verify that the ultrasonic and Quad RGB sensor blocks appear and the configured sensors match the simulation.
6. If physical robots are available, charge them, label them, define a floor-test boundary, and choose a clean high-contrast line/obstacle course. Do not promise hardware transfer until the simulator tasks work.

### Materials per pair

- One computer with a current browser and headphones if possible.
- Access to the simulator and a shared account credential card.
- Printed or digital challenge sheet and test log.
- Optional: one mBot2, charging cable, measured tape path, obstacle, black tape line, and ruler.

### Routine used in every class

- **Launch (5–10 min):** a visible robot problem and a prediction.
- **Mini-lesson (10–15 min):** only the blocks needed today, projected live.
- **Build/test cycles (45–50 min):** pairs plan, build, simulate, observe, revise.
- **Share/debug (10–15 min):** compare different solutions and name common errors.
- **Exit evidence (5–10 min):** one screenshot/save plus a short explanation or prediction.

## Assessment model

Use a low-stakes evidence portfolio, not a long written test. Each session produces one named saved program and one short record: screenshot/video if permitted, observation, or a teacher check.

| Criterion | Emerging | Meets course expectation | Exceeds course expectation |
| --- | --- | --- | --- |
| Workspace fluency | Needs step-by-step help to operate blocks/simulation. | Independently edits, saves, and runs a program. | Uses comments, code view, or organisation to help others debug. |
| Program logic | Sequence is incomplete or does not match intended behaviour. | Correct sequence plus loop or condition for the stated task. | Uses abstraction or multiple conditions to make behaviour more reliable. |
| Sensor reasoning | Treats a reading as a guess. | Connects a sensor value to a condition and a robot action. | Chooses/calibrates a threshold and explains its trade-off. |
| Testing and explanation | Runs once without interpreting the result. | Tests, observes, changes one thing, and explains the change. | Compares trials and uses evidence to justify an improvement. |
| Collaboration | Role is unclear or one learner controls all work. | Pair roles rotate and both can explain the program. | Pair gives useful, specific feedback to another team. |

## Session plans

### Class 1 — Robot lab orientation and first simulation

**Big question:** How do we turn an idea into a robot action in this workspace?

**Essential vocabulary:** robot, program, algorithm, block, toolbox, workspace, configuration, simulation, motor, sensor, source code.

**Student deliverable:** `C1_Name_RobotHello` — mBot2 drives a short route and gives one visible or audible signal; student saves it and identifies the generated Python view.

| Time | Teacher and student actions | Evidence / checks |
| ---: | --- | --- |
| 0–10 | Show an mBot2 mission clip/live demo. Ask: “What instructions must the robot receive?” Introduce the input–process–output idea. | Students name one actuator and one sensor. |
| 10–22 | Tour: log in/class workspace, choose mBot2, program/configuration views, Beginner/Expert level, program name/save, load/list. Briefly compare mBot2 with EV3: same categories, different hardware. | Every pair reaches an empty mBot2 program. |
| 22–35 | Live model: open an Action category; drag a drive-for block, change power/distance, connect a light or tone block, delete a block, undo/redo. Emphasise reading block labels before dropping. | Students reproduce one two-action sequence. |
| 35–52 | Live model: start simulation, run/pause/stop/reset, observe route, change one number, predict then re-run. Explain that a simulator is a test model, not magic proof of real-world performance. | Pair completes a predict → run → observe → revise cycle. |
| 52–67 | Guided challenge: “Leave the start, travel to a marked zone, celebrate, stop.” Provide a maximum of four required actions, not a full solution. | Working program or documented attempt. |
| 67–77 | Show generated source-code view. Identify one Python line that matches a block; do **not** type/edit. Close it and return to blocks. | Students point to an action in blocks and corresponding source. |
| 77–87 | Partner swap: the non-driver explains what will happen before a run. Pairs fix one issue. | Teacher checks both partners can explain sequence. |
| 87–90 | Exit ticket: “Where do you find a block? What does reset do? What changed after your edit?” | Saved `C1` program + response. |

**Teacher look-fors:** students stop simulation before major edits; all work is saved with the agreed name; students do not confuse a sensor with an output.

**Likely misconceptions and response:**

- “The robot understands the goal.” → It only performs connected instructions, in order.
- “A larger speed means it travels farther.” → Separate speed from distance/time; test both.
- “The Python screen is a second program.” → It is a generated representation of the current blocks; refresh after changing blocks.

**Optional extensions:** use screen text or RGB LED instead of sound; change route parameters and make a before/after prediction; find the equivalent action in EV3 without changing the assessed mBot2 task.

### Class 2 — Making a robot behave purposefully

**Big question:** How do loops and decisions let a robot do more than a fixed list of actions?

**Essential vocabulary:** sequence, repeat, forever, condition, true/false, if, if-else, variable, procedure/function, parameter, debugging.

**Student deliverable:** `C2_Name_RobotRoutine` — a movement-and-feedback routine using at least one loop and one decision; Secure level adds a variable, Stretch uses a small procedure.

| Time | Teacher and student actions | Evidence / checks |
| ---: | --- | --- |
| 0–8 | Retrieval warm-up: predict the result of three simple movement programs. | Students explain order of execution. |
| 8–20 | Unplugged “robot dance”: students write a repeated pattern, then compress it with repeat. Introduce why repeated code is hard to maintain. | One loop replaces repeated instructions. |
| 20–33 | Demonstrate repeat and forever, then a timed wait. Model a safety rule: a forever drive needs a stop/release plan when testing. | Pairs add a loop to last lesson’s program. |
| 33–48 | Demonstrate if and if-else using a simple comparison (e.g., timer/variable or a preplanned condition). Explain a condition as a question with a true/false answer. | Students complete a truth-table prediction. |
| 48–63 | Challenge: “Patrol celebration.” Drive a repeated pattern; on a chosen condition, show one signal, otherwise show another. | Loop + if/if-else visible in program. |
| 63–73 | Introduce variables through `speed`, `turnAngle`, or `celebrationCount`: one named value makes a program easier to tune. Students who are ready create `driveSquare` / `celebrate` procedure in Expert level. | Variable is used, not merely created. |
| 73–84 | Debug gallery: give pairs a broken sequence (missing stop, wrong nesting, always-true condition) and ask them to diagnose it. | Students state cause before changing blocks. |
| 84–90 | Exit ticket: distinguish repeat from forever and write a condition in words. | Saved `C2` program. |

**Minimum viable scope:** repeat, if/if-else, comparison, arithmetic, timed wait, and a useful variable. Procedures are an extension, not a required first exposure.

**Why variables and functions fit here:** include variables only after learners have felt the pain of changing the same number in several places. Introduce a procedure only as “give a repeated mini-algorithm a name.” Do not teach parameter design, recursion, or lists in this course.

### Class 3 — Sensors: detecting distance and line/colour

**Big question:** How does a robot make a decision from something it measures?

**Essential vocabulary:** sensor, reading, threshold, comparison, ultrasonic distance, Quad RGB, line/colour, calibration, input, output.

**Student deliverable:** `C3_Name_SensorScout` — the robot responds to an obstacle using ultrasonic distance and completes a line/colour-related response using Quad RGB.

| Time | Teacher and student actions | Evidence / checks |
| ---: | --- | --- |
| 0–10 | Sensor mystery: display several readings/actions and have students infer which might be distance, colour, or sound. | Students articulate “reading first, action second.” |
| 10–22 | Demonstrate the mBot2 configuration view and identify ultrasonic and Quad RGB modules. Explain the physical analogy: ultrasonic asks “how far?”, Quad RGB samples the surface beneath it. | Students locate both sensor blocks. |
| 22–37 | Live-build obstacle avoidance: repeat forever; if distance is less than a chosen threshold, stop/turn/signal; otherwise drive. Run it and change threshold deliberately. | Pair predicts near vs far behaviour. |
| 37–50 | Build/test #1: “Do not bump the crate.” Teams test at least three threshold values and log which value behaves best in the simulated scene. | Three rows in test log: threshold, result, revision. |
| 50–62 | Introduce Quad RGB/line sensing with a visual model of light/dark or colour categories. Model a simple decision: react differently to a dark line/target colour. Keep the first program to one comparison. | Students identify which surface should trigger the condition. |
| 62–77 | Build/test #2: “Find and mark the delivery zone.” Drive/scan, detect a chosen line/colour condition, stop and display/light a success cue. | Working sensor-to-action chain. |
| 77–85 | Pair explanation: student A narrates the distance program; student B narrates the line/colour program. Swap. | Teacher samples explanations, not just screenshots. |
| 85–90 | Exit ticket: “Why might a threshold that works once fail later?” | Saved `C3` program and test log. |

**Teaching notes:**

- Sensor values need interpretation. Say “less than 15 cm” rather than “the sensor sees an obstacle.”
- Calibration is not a one-time magic number. Surface, light, speed, placement, and simulation-vs-hardware differences matter.
- Begin with one sensor and one branch. A continuous line follower with four RGB elements is a stretch goal, not the first requirement.

### Class 4 — From reactions to reliable autonomous behaviour

**Big question:** How can a robot use feedback to complete a mission more reliably?

**Essential vocabulary:** feedback, state, priority, encoder, gyro, timer, tolerance, recovery, test case, trace/debug.

**Student deliverable:** `C4_Name_AutonomousNavigator` — a two-sensor autonomous behaviour that avoids an obstacle and reaches/recognises a target; it includes a recovery plan and a test record.

| Time | Teacher and student actions | Evidence / checks |
| ---: | --- | --- |
| 0–10 | Compare two robots: one blindly drives a fixed distance; one checks a sensor repeatedly. Ask which is more robust and why. | Students use the word feedback accurately. |
| 10–22 | Brief demo of timer, encoder, and gyro as ways to measure elapsed time, wheel movement, or turning. Use one only in the core example; do not teach every sensor in depth. | Students choose a sensible measurement for a stated task. |
| 22–35 | Demonstrate a decision priority pattern: “if obstacle → avoid; else if target detected → finish; else → continue.” Explain why condition order changes behaviour. | Students predict two different orders. |
| 35–50 | Build a starter navigator: forever loop, ultrasonic escape response, Quad RGB target response, otherwise forward. Add sound/LED/display feedback for each state. | Program has clear state cues. |
| 50–68 | Team challenge: tune the navigator for three test cases: clear route, obstacle route, target route. Require a recovery action after avoiding an obstacle. | Completed test matrix. |
| 68–78 | Reliability mini-lesson: use a variable such as `safeDistance` or `attempts`; Stretch teams make a `turnAway` procedure or use gyro/encoder/timer for consistency. | At least one purposeful improvement. |
| 78–86 | Debugging protocol: (1) state expected behaviour, (2) reproduce the failure, (3) inspect one condition/action, (4) change one thing, (5) retest. | Teams document one defect/fix. |
| 86–90 | Capstone briefing and team planning: select roles, sketch mission logic, choose two required sensors/outputs. | Approved one-page plan. |

**Core versus stretch sensor plan:**

- **Core:** ultrasonic + Quad RGB; timer only if useful.
- **Stretch:** gyro for a consistent turn, encoders for movement verification, light/microphone/accelerometer for a context-aware feature, CyberPi buttons/joystick as a launch/menu control.
- **Not a core requirement:** sensors that cannot be clearly tested in the selected simulator scene, or any multi-sensor line-following algorithm that consumes capstone time.

### Class 5 — Capstone: Autonomous Delivery Rover

**Big question:** Can we design, test, explain, and improve a robot that completes a real mission?

**Mission brief:** Program an mBot2 “delivery rover” that leaves a start zone, moves through a course, avoids a detected obstacle, identifies or reaches a delivery zone, signals delivery, and stops safely. The exact simulated map/course should be prepared by the instructor and remain identical for every team’s first test.

**Required features:**

- At least one movement sequence with a deliberate stop.
- At least one loop.
- At least one if/if-else or ordered conditional structure.
- Ultrasonic distance use.
- Quad RGB / line/colour use, or another instructor-approved second sensor when the scene makes it testable.
- One feedback output: display, LED, or sound.
- One variable **or** one procedure.
- At least three documented tests, including one failed or changed case.
- A two-minute explanation in which both partners speak.

| Time | Teacher and student actions | Evidence / checks |
| ---: | --- | --- |
| 0–10 | Revisit mission, constraints, rubric, and safety/role expectations. Teams state their first test plan before coding. | Plan has inputs, decisions, outputs. |
| 10–25 | Build a minimum viable mission: leave start → obstacle reaction → target response → stop. Teacher conferences for feasibility, not to write code. | Every team has runnable skeleton. |
| 25–48 | Iteration 1: run required test course, record outcome, make one targeted revision. | Test log 1 and revised program. |
| 48–63 | Iteration 2: introduce a changed condition (obstacle nearer/farther, different target placement, or speed). Add feedback and variable/procedure if absent. | Test log 2 and feature check. |
| 63–75 | Iteration 3 and peer test: another team reads the program explanation, predicts its behaviour, and provides one “glow” and one precise “grow.” | Peer-feedback card + test log 3. |
| 75–86 | Showcase: two-minute team demo/explanation. Students show blocks first; then show source code and identify one recognisable Python construct. | Rubric scoring and student voice. |
| 86–90 | Individual reflection: “What is one rule my robot follows, one piece of evidence it works, and one limitation I would solve next?” | Individual completion evidence. |

## Suggested capstone logic, in plain language

This is a planning model, not a block-by-block answer:

1. Set `safeDistance` and show “ready.”
2. Repeat while the mission is not finished.
3. If an obstacle is closer than `safeDistance`, stop, signal warning, turn away, then continue.
4. Else if the Quad RGB/line/colour sensor detects the delivery zone, stop, signal delivery, and mark mission complete.
5. Else drive forward at a safe speed.
6. If time/attempt limit is reached, stop and signal “needs help.”

This makes condition priority visible and gives teams a legitimate recovery/safety path. Stretch teams can place “turn away” or “deliver” in named procedures, use an encoder/gyro to tune movement, or compare two threshold values.

## Student test-log template

| Program version | Test condition | Prediction | Actual result | One change for next test |
| --- | --- | --- | --- | --- |
| v1 | Clear path |  |  |  |
| v2 | Obstacle present |  |  |  |
| v3 | Delivery target present |  |  |  |
| v4 (optional) | Changed threshold/speed/lighting |  |  |  |

## Teacher troubleshooting and safeguards

| Symptom | Likely cause | Fast response |
| --- | --- | --- |
| Robot never stops | Continuous drive/forever loop has no reachable stop branch. | Add a stop at mission completion; test with a short run first. |
| Robot always turns or never turns | Threshold/unit or comparison direction is wrong. | Display/log the reading if available; change one threshold at a time and predict result. |
| Robot misses line/colour | Sensor/scene condition differs from the assumed surface. | Re-check configuration and the simulator surface; simplify to one detectable condition; calibrate. |
| Program appears unchanged | Students ran an old/unsaved program or source view was not refreshed. | Save, return to blocks, confirm program name, rerun; refresh generated code after edits. |
| Pair work is unequal | One student controls mouse and explanation. | Enforce timed role rotation; ask the non-driver to predict before every run. |
| Simulation differs from hardware | Physical noise, wheel slip, lighting, timing, sensor placement. | Treat simulator as an early test, not certification; recalibrate thresholds and speeds on hardware. |

## What not to teach in this first pilot

- Manual Python programming or editing generated code.
- Neural networks, communication/message blocks, lists, recursion, or advanced mathematical functions.
- Every available mBot2 sensor.
- EV3 programming tasks alongside mBot2 tasks.
- A high-stakes syntax quiz.

These are strong follow-on modules once students have a reliable sensor-driven mBot2 program.

## Follow-on course options

After this pilot, choose one coherent next step rather than expanding the first course:

1. **mBot2 Control Systems:** calibration, proportional line following, gyro/encoder precision, data logging.
2. **mBot2 Physical Deployment:** simulator-to-hardware transfer, mechanical setup, troubleshooting, and measured performance.
3. **Generated Python Bridge:** use the source view to map blocks to Python, then gradually write small Python changes in a controlled environment.
4. **EV3 Transfer Unit:** revisit the same autonomous-delivery mission on EV3 and compare sensors, ports, and block abstractions.

## Pilot evaluation questions

At the end of the five sessions, collect these before changing scope:

- Could at least 80% of pairs independently save, run, stop, and revise a simulation by the end of Class 2?
- Did students successfully connect a sensor reading to a conditional action by the end of Class 3?
- Which sensor blocks were consistently testable in the actual simulator scene and, if used, on physical hardware?
- Did the capstone fit in 90 minutes after Class 4 planning, or did teams need a simplified course?
- Were the Beginner blocks sufficient for Core learners, and which Expert features were genuinely used by stretch learners?
- Did looking at generated Python help understanding, or distract from block reasoning?

Use the answers to refine the next mBot2 cohort before designing an EV3 version.
