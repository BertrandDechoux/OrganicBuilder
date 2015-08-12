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
* artifact site : target/site/index.html
* [coverage](http://cobertura.github.io/cobertura/) : target/site/cobertura/index.html
* [checkstyle](http://checkstyle.sourceforge.net/) : target/site/checkstyle.html
* [CPD](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html) : target/site/cpd.html
* [PMD](https://pmd.github.io/) : target/site/pmd.htm
* [findbugs](http://findbugs.sourceforge.net/) : target/site/findbugs.html

