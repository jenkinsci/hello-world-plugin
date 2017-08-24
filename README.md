This plugin is written for the [Jenkins plugin tutorial](http://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial),
and hence it's only useful as an example, and no other
practical use.

# Quick install

`mvn -Dmaven.test.skip=true -DskipTests=true clean install`

# Quick run

```
rm -rf work/plugins
mvn -Dmaven.test.skip=true -DskipTests=true clean hpi:run
```

# Run tests with coverage reporting

```
mvn -P enable-jacoco clean test jacoco:report
```

# Run findbugs with gui

```
mvn clean compile findbugs:findbugs findbugs:gui
```
