To run:

`gradle jettyRunWar`

Debug:

`gradle -Dorg.gradle.jvmargs="-XX:MaxPermSize=256M -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005" jettyRunWar`

- [http://localhost:8080/zanataHelper/](http://localhost:8080/)
- [http://localhost:8080/zanataHelper/api/jobs/status?sha=1](http://localhost:8080/api/jobs/status?sha=1)
