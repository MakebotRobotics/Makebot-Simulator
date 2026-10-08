# Makebot Simulator

Makebot Simulator is a browser-based robotics programming and simulation platform maintained by [Makebot Robotics](https://github.com/MakebotRobotics). It is derived from the [Open Roberta Lab](https://github.com/OpenRoberta/openroberta-lab) project and combines a Java/Jetty backend, a TypeScript frontend, an embedded HSQLDB database, and robot-specific modules.

This document covers local development, database initialization, release packaging, and deployment to a Linux server.

## Contents

- [Repository layout](#repository-layout)
- [Required tools and versions](#required-tools-and-versions)
- [Quick start](#quick-start)
- [Build details](#build-details)
- [Database setup and administration](#database-setup-and-administration)
- [Development workflow](#development-workflow)
- [Testing](#testing)
- [Deploying to a Linux server](#deploying-to-a-linux-server)
- [Configuration](#configuration)
- [Optional cross-compilers](#optional-cross-compilers)
- [Troubleshooting](#troubleshooting)
- [Licensing and attribution](#licensing-and-attribution)

## Repository layout

| Path | Purpose |
| --- | --- |
| `SimulationServer/` | Jetty/Jersey server, persistence layer, server configuration, and generated/static browser assets |
| `SimulationWeb/` | TypeScript, JavaScript, CSS, and Gulp frontend build |
| `SimulationRobot/` | Shared robot model, AST, code generation, and simulator logic |
| `RobotArdu/` | Arduino-family robot support |
| `RobotCyberpi/` | CyberPi and mBot2 support |
| `RobotEV3/` | LEGO EV3 support |
| `RobotFischertechnik/` | Fischertechnik robot support |
| `admin.sh` / `admin.bat` | Database administration and server startup |
| `ora.sh` | Start from a Git checkout or export a self-contained installation |
| `pom.xml` | Parent Maven build for all Java modules |

The Maven reactor builds seven projects: the parent plus the six Java modules above. Frontend output is written into `SimulationServer/staticResources/` and is served by the Java server.

## Required tools and versions

The following versions are the supported baseline for this repository:

| Tool | Version | Notes |
| --- | --- | --- |
| Java JDK | **11 LTS** | Recommended and verified. Maven compiles Java 8-compatible bytecode (`source`/`target` 1.8). Use a full JDK, not only a JRE. |
| Maven | **3.8+** | Maven 3.9.x is verified. |
| Node.js | **20 LTS or newer** | The frontend is verified with the repository lockfile and modern npm. |
| npm | **10+** | Use `npm ci` for reproducible server/release builds; use `npm install` when intentionally updating dependencies. |
| TypeScript | **4.9.5** | Installed locally by npm; do not install it globally. |
| Gulp CLI | Repository-local | Run it through `npx gulp`; a global Gulp installation is unnecessary. |
| Git | Current stable | Required for cloning and normal development. |
| Bash | Current stable | Required by `admin.sh` and `ora.sh` on Linux/macOS/WSL. |

Optional Python and robot cross-compiler requirements are described under [Optional cross-compilers](#optional-cross-compilers). They are not needed for browser simulation.

Verify the main tools:

```bash
java -version
mvn -version
node --version
npm --version
git --version
```

> **Java compatibility:** JDK 11 is the safest choice for this codebase and its Jetty/Hibernate/JAXB dependency generation. The generated class files remain Java 8 compatible, but the current dependency-check tooling requires Java 11 or newer when the optional OWASP profile is used.

## Quick start

### Linux, macOS, or WSL

```bash
git clone https://github.com/MakebotRobotics/Makebot-Simulator.git
cd Makebot-Simulator

# Install and compile the frontend.
cd SimulationWeb
npm ci
npm run build
npx gulp
cd ..

# Build the Java modules and assemble runtime libraries.
mvn clean install -DskipTests

# Create the embedded database. This does not overwrite an existing database.
./admin.sh -git-mode create-empty-db

# Start the server from the working tree.
./ora.sh start-from-git
```

Open <http://localhost:1999>.

If the shell scripts are not executable after checkout, run:

```bash
chmod +x admin.sh ora.sh
```

### Windows

The most complete workflow is through WSL or Git Bash because `ora.sh` is a Bash script. Native Windows can initialize and start an exported installation with `admin.bat`:

```powershell
cd SimulationWeb
npm ci
npm run build
npx gulp
cd ..
mvn clean install -DskipTests
```

For day-to-day development on Windows, start the server through WSL/Git Bash with the commands above, or configure the IDE to run `de.fhg.iais.roberta.main.ServerStarter` with `SimulationServer/target/resources/*` on its classpath.

## Build details

### Frontend

Run frontend commands from `SimulationWeb/`:

```bash
# Reproducible install from package-lock.json (recommended for CI/deployment)
npm ci

# Or install/update dependencies during development
npm install

# Compile TypeScript to SimulationServer/staticResources/js
npm run build

# Build/copy/minify CSS, JavaScript, and npm-managed browser libraries
npx gulp
```

For continuous frontend development:

```bash
npx gulp watch
```

The browser does not live-reload; refresh it after generated assets change. Do not edit generated files in `SimulationServer/staticResources/js/` directly—edit their sources in `SimulationWeb/src/`.

### Backend

Run Maven from the repository root:

```bash
# Full build including unit tests
mvn clean install

# Faster build for deployment after tests have passed elsewhere
mvn clean install -DskipTests
```

`-DskipTests` is case-sensitive. The build populates `SimulationServer/target/resources/` with the server JAR, robot-module JARs, and all runtime dependencies used by the administration scripts.

### Recommended clean release build

```bash
cd SimulationWeb
npm ci
npm run build
npx gulp
cd ..
mvn clean install -DskipTests
```

Run the test-enabled Maven build before promoting a release whenever practical.

## Database setup and administration

The default database is HSQLDB 2.4.0. Local development and the self-contained deployment below use **embedded mode**.

### Create the development database

From the repository root, after the Maven build:

```bash
./admin.sh -git-mode create-empty-db
```

This creates `SimulationServer/db-embedded/openroberta-db.*`. The command is intentionally non-destructive: it does not replace an existing database.

The default embedded connection is based on:

```text
jdbc:hsqldb:file:./SimulationServer/db-embedded/openroberta-db
```

Default HSQL credentials configured by the application are `orA` / `Pid`. Do not expose the HSQL port publicly, and change credentials as part of any custom external-database deployment.

### Useful database commands

```bash
# Show all administration options
./admin.sh -h

# Open a terminal SQL client (stop the app first in embedded mode)
./admin.sh -git-mode sql-client

# Open the HSQL graphical client where a GUI is available
./admin.sh -git-mode sql-gui

# Execute one SQL statement
./admin.sh -git-mode sql-exec "SELECT COUNT(*) FROM USER;"
```

Only one process may open an embedded HSQLDB database at a time. Stop the server before using a database client against it. If startup reports that the database is locked, check for an already-running Java process and remove a stale lock only after confirming that no process is using the database.

### Database persistence and backups

- Development data lives under `SimulationServer/db-embedded/`.
- An exported installation stores its database under the deployment directory, `db-embedded/` by default.
- Back up the **entire database directory** while the server is stopped. HSQLDB databases consist of multiple related files; copying only one file is not a valid backup.
- Keep database backups outside the release directory before replacing or rolling back an installation.

The scripts also support `-db-mode server`, but they do not provision or start a separate HSQLDB service. In that mode, an administrator must run HSQLDB independently and ensure the database name passed with `-db-name` is available at `jdbc:hsqldb:hsql://localhost/<database-name>`.

## Development workflow

Start the server from a built checkout:

```bash
./ora.sh start-from-git
```

The command automatically creates an empty embedded database when none exists. To pass a cross-compiler resource directory:

```bash
./ora.sh start-from-git /absolute/path/to/ora-cc-rsc
```

For remote JVM debugging:

```bash
./ora.sh start-from-git -rdbg
```

The debug listener uses port `2000` in the current scripts. Application logs and administration files are written below `./admin/` when `admin.sh -git-mode` is used; `ora.sh start-from-git` writes server logging to the console.

## Testing

```bash
# Backend unit tests
mvn test

# Clean build with unit tests
mvn clean install

# Integration tests (requires the optional compilers/resources used by the tests)
mvn clean install -PrunIT

# Frontend type-check/compile
cd SimulationWeb
npm run build

# Formatting check
npm run format:check
```

The `SimulationWeb` package currently has no automated npm test suite; `npm test` intentionally exits with an error. Use `npm run build` as the frontend compile check.

## Deploying to a Linux server

The repository does not currently include the upstream Docker deployment directory referenced by older Open Roberta documentation. The supported deployment path in this repository is the self-contained export created by `ora.sh`.

### 1. Install server prerequisites

On Ubuntu/Debian:

```bash
sudo apt-get update
sudo apt-get install -y openjdk-11-jdk maven git nodejs npm gzip
```

For repeatable production builds, install Node.js 20 LTS from your organization’s approved package source if the distribution package is older.

Create a dedicated unprivileged account and directories according to your organization’s policy. The application itself should not run as `root`.

### 2. Build the release

On the build host or server:

```bash
git clone https://github.com/MakebotRobotics/Makebot-Simulator.git
cd Makebot-Simulator

cd SimulationWeb
npm ci
npm run build
npx gulp
cd ..

mvn clean install -DskipTests
```

### 3. Export a self-contained installation

Choose a new or empty target directory:

```bash
./ora.sh export /opt/makebot-simulator gzip
```

The export contains:

- `lib/` with the server and dependency JARs;
- `staticResources/` with the built browser application;
- `admin.sh`, `admin.bat`, and administration help;
- license and notice files.

Ensure the service account owns the exported directory:

```bash
sudo chown -R makebot:makebot /opt/makebot-simulator
```

### 4. Initialize the production database

```bash
cd /opt/makebot-simulator
sudo -u makebot ./admin.sh create-empty-db
```

If migrating an existing installation, stop the old server and copy the complete database directory into the new deployment instead of creating a new database. Take a verified backup first.

### 5. Test the exported server

```bash
cd /opt/makebot-simulator
sudo -u makebot ./admin.sh -Xmx2G start-server
```

The command runs in the foreground and writes logs to `/opt/makebot-simulator/admin/logs/server.log`. Confirm `http://server-address:1999` works, then stop it with `Ctrl+C` before installing the service.

### 6. Run with systemd

Create `/etc/systemd/system/makebot-simulator.service`:

```ini
[Unit]
Description=Makebot Simulator
After=network.target

[Service]
Type=simple
User=makebot
Group=makebot
WorkingDirectory=/opt/makebot-simulator
ExecStart=/opt/makebot-simulator/admin.sh -Xmx2G start-server
Restart=on-failure
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

Then enable and start it:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now makebot-simulator
sudo systemctl status makebot-simulator
```

Inspect logs with:

```bash
sudo journalctl -u makebot-simulator -f
tail -f /opt/makebot-simulator/admin/logs/server.log
```

### 7. Put a reverse proxy in front of the app

Jetty listens on port `1999` by default. For an internet-facing deployment, bind access through a firewall and use a reverse proxy such as Nginx or Apache for TLS, request limits, and the public hostname. Proxy both normal HTTP traffic and WebSocket upgrade headers. Do not expose an HSQLDB server port to the internet.

Before going live:

- enable HTTPS at the reverse proxy;
- restrict port `1999` to the proxy or trusted network;
- configure automated backups of the stopped/snapshotted database directory;
- monitor both systemd and `admin/logs/server.log`;
- test account creation, login, program storage, and simulation;
- install optional cross-compilers only when physical-robot binary generation is required.

### Updating an installation

1. Back up the database and current exported directory.
2. Build and export the new revision to a **new** directory.
3. Stop the service.
4. Copy or move the complete database directory to the new release.
5. Update the systemd `WorkingDirectory`/`ExecStart` if using versioned release paths.
6. Start the service and check the logs and application health.
7. Keep the prior release and backup until the upgrade is verified.

Do not export over a populated installation directory.

## Configuration

Defaults are defined in `SimulationServer/src/main/resources/openRoberta.properties`. Important settings include:

| Setting | Default | Purpose |
| --- | --- | --- |
| `server.port` | `1999` | HTTP listener |
| `database.mode` | `embedded` | `embedded` or separately managed HSQLDB `server` mode |
| `database.name` | `openroberta-db` | Database name |
| `database.parentdir` | `./SimulationServer/db-embedded` in source defaults | Database directory |
| `server.staticresources.dir` | `SimulationServer/staticResources` in source defaults | Browser assets |
| `server.admin.dir` | `.` in source defaults | Logs, tutorials, backups, and administrative data |
| `robot.crosscompiler.resourcebase` | unset | Optional cross-compiler resources |

The administration script supplies deployment-appropriate directory values. Additional application properties can be appended after `start-server` with repeated `-d key=value` arguments, for example:

```bash
./admin.sh -Xmx2G start-server \
  -d server.port=8080 \
  -d robot.crosscompiler.resourcebase=/opt/ora-cc-rsc
```

Use absolute paths in production where possible. Review `openRoberta.properties` for mail, HTTPS, plugin, and other optional settings before enabling those features.

## Optional cross-compilers

Cross-compilers are **not required** for programming, browser simulation, user management, or code generation. They are needed only when the server must compile downloadable binaries for supported physical robots.

Compiler binaries and system packages vary by robot family. The matching header files and libraries come from [OpenRoberta/ora-cc-rsc](https://github.com/OpenRoberta/ora-cc-rsc):

```bash
git clone https://github.com/OpenRoberta/ora-cc-rsc.git
cd ora-cc-rsc
mvn clean install
```

Pass the resulting resource directory at startup with `robot.crosscompiler.resourcebase` as shown above. Keep toolchain binaries on the service account’s `PATH` and validate each robot family independently.

## Troubleshooting

### `ClassNotFoundException` when running an administration command

Run the Maven build first:

```bash
mvn clean install -DskipTests
```

The scripts expect runtime JARs under `SimulationServer/target/resources/` in Git mode or `lib/` in an exported installation.

### Frontend changes are missing

Rebuild both TypeScript and the Gulp asset pipeline:

```bash
cd SimulationWeb
npm run build
npx gulp
```

Then hard-refresh the browser.

### Address already in use

Another process is using port `1999`. Stop it or start with another port:

```bash
./admin.sh -git-mode start-server -d server.port=8080
```

### Database is locked

Only one process can access an embedded HSQLDB database. Stop the running simulator and any SQL client before restarting. Check the Java process list before treating a lock as stale.

### Build warnings about Java 8 bootstrap classes

The POM targets Java 8 bytecode while the recommended compiler is JDK 11. The warning is expected in the current build. A successful Maven reactor summary is the authoritative result.

## Contributing

See [Contributing.md](Contributing.md) and [Code_Of_Conduct.md](Code_Of_Conduct.md). Keep generated frontend assets and backend changes synchronized, and run the relevant build checks before submitting changes.

## Licensing and attribution

Makebot Simulator is licensed under the [Apache License 2.0](LICENSE). Copyright in Makebot-specific modifications is held by **Makebot Robotics**. The upstream Open Roberta copyright, trademark conditions, acknowledgements, and third-party notices remain in force and are preserved in:

- [NOTICE](NOTICE)
- [ListOfThird-PartyProductsAndLicenses.txt](ListOfThird-PartyProductsAndLicenses.txt)
- [LICENSE](LICENSE)

“Open Roberta,” its associated names, logos, and seals are trademarks of the Fraunhofer Society. The Apache License does not grant trademark rights.
