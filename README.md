- To compile: `gradle install`
- To run test: `gradle dist test`

- To run war: 
    - `gradle appRunWar`, `appRunWarDebug`
    - [http://localhost:8081/home.xhtml](http://localhost:8081/home.xhtml)
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