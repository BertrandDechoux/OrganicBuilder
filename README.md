    Artificial chemistry low-level simulation
    Make up our own rules of construction to simulate open-ended, creative evolution

This is the motto of the OrganicBuilder, an application created by [Tim Hutton](http://www.sq3.org.uk).

For more information, please visit [Organic Buidler website](https://bertranddechoux.github.io/OrganicBuilder/).

You are welcome to watch, fork the project and create pull requests. Contact me if you have any questions.


**Compile and run the tests**
```
mvn clean test
```

**Create the executable jar**
```
mvn clean package
```

**Run the executable jar**
```
java -jar target/*-jar-with-dependencies.jar
```

**Run the executable jar with alternative locale (language)**
```
java -Duser.language=fr -Duser.country=FR -jar target/*-jar-with-dependencies.jar
```

**Create the artefact site**
```
mvn clean site
```
With dependencies, javadocs, test coverage and code/style checks (checkstyle,pmd,findbugs)
