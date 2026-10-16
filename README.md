    Artificial chemistry low-level simulation
    Make up our own rules of construction to simulate open-ended, creative evolution

This is the motto of the OrganicBuilder, an application created by [Tim Hutton](http://www.sq3.org.uk).

For more information, please visit [Organic Buidler website](https://bertranddechoux.github.io/OrganicBuilder/).

You are welcome to watch, fork the project and create pull requests. Contact me if you have any questions.


## Prerequisites

You need a JDK (8 or newer) and Apache Maven (`mvn` on your PATH).

### Linux (Debian/Ubuntu)

```bash
sudo apt update && sudo apt install maven default-jdk
mvn -v   # verify
```

Other distributions: `sudo dnf install maven` (Fedora),
`sudo pacman -S maven` (Arch). Without root: grab the binary tarball
from https://maven.apache.org/download.cgi, extract it and put its
`bin/` directory on your PATH.

### Windows

```powershell
winget install Microsoft.OpenJDK.21   # or any JDK 8+
winget install Apache.Maven           # or: choco install maven
mvn -v                                # verify in a NEW terminal
```

Alternative: install a JDK manually, download the Maven zip from
https://maven.apache.org/download.cgi and add its `bin\` directory to
the PATH environment variable.

## Build commands

Run from the project root — identical on Linux and Windows.

**Compile and run the tests**
```
mvn clean test
```

**Create the executable jar**
```
mvn clean package
```
Linux: `ls target/*-jar-with-dependencies.jar`
Windows: `dir target\*-jar-with-dependencies.jar`

**Run the app**
```
java -jar target/organicbuilder-*-jar-with-dependencies.jar
```
(On Windows, double-clicking the jar also works in most setups.)

**Create the artefact site**
```
mvn clean site
```
With depedencies, javadocs, test coverage and code/style checks (checkstyle,pmd,findbugs)

Note: `test`/`package` are verified on JDK 21 (a JDK9+ profile in the
pom opens the internals the legacy mockito-all test dependency needs).
`mvn site` requires **JDK 8** — the cobertura report plugin looks for
`lib/tools.jar`, which no longer exists on modern JDKs.
