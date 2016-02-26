- To compile: `gradle install`
- To run test: `gradle dist test`

- To run war: 
    - `gradle clean appRunWar`, `clean appRunWarDebug`
    - [http://localhost:8081](http://localhost:8081)
    - [http://localhost:8081/admin/settings.jsf](http://localhost:8081/admin/settings.jsf)
    - For more info, see [http://akhikhl.github.io/gretty-doc/Gretty-configuration.html]

API URL:
- GET - [http://localhost:8081/api/job?id={id}&type={SERVER_SYNC,REPO_SYNC}&status={RUNNING,NONE,NORMAL,PAUSED,COMPLETE,ERROR,BLOCKED}]
- GET - [http://localhost:8081/api/job/status?id=1]
- POST - [http://localhost:8081/api/job/cancel?id=1&type={SERVER_SYNC,REPO_SYNC}]
- POST - [http://localhost:8081/api/job/start?id=1&type={SERVER_SYNC,REPO_SYNC}]
- POST - [http://localhost:8081/api/job/disable?id=1&type={SERVER_SYNC,REPO_SYNC}]
- POST - [http://localhost:8081/api/job/enable?id=1&type={SERVER_SYNC,REPO_SYNC}]

- GET - [http://localhost:8081/api/work?id={id}&type={summary}]
- POST - [http://localhost:8081/api/work]
- PUT - [http://localhost:8081/api/work]
- DELETE - [http://localhost:8081/api/work?id={id}]

Executable WAR
- build: gradle liveWar (server/build/distributions/server-livewar-{version}.war)
- start: java -Djetty.port={PORT} -Djetty.host={IP} -Ddata.path={directory to store data + db} -jar server-livewar-{version}.war
