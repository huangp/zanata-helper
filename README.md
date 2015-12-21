To compile: `gradle install`
To run war: `gradle jettyRunWar`
To run war(debug): `gradle -Dorg.gradle.jvmargs="-XX:MaxPermSize=256M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" jettyRunWar`
To run test: `gradle dist test`

- [http://localhost:8080/](http://localhost:8080/)
