<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="includes/header.jsp"/>

<body class="new-zanata new-zanata-base">
  <script>
    $(document).ready(function() {
      setInterval(function() {
          refreshRunningJobs();
      }, refreshPageInterval);
    });
  </script>
  <main role="main" class="l--pad-v-1">
    <div class="l__wrapper">
      <div class="g">
        <div class="g__item w--1-m w--3-8-l w--1-4 l--push-bottom-half">
          <h1><spring:message code="Application.name"/></h1>
          <div class="l--pad-all-quarter l--push-top-quarter panel" id="runningJobsContent">
            <button class="button button--small button--primary loader" onclick="refreshRunningJobs()">
              <span class="loader__label">
                  <spring:message code="jsf.newJob.refresh.button"/>
                </span>
            </button>
            <jsp:include page="view/running_jobs.jsp" />
          </div>
        </div>

        <div class="g__item w--1-m w--5-8-l w--3-4">
          <h3 class="l--push-top-0">
            <spring:message code="jsp.allAvailableJobs.title"/>
          </h3>

          <div class="panel">
            <c:if test="${empty allJobs}">
              <div class="l--pad-all-half">
                <p class="txt--meta">
                  <spring:message code="jsp.noJobs"/>
                </p>
                <a href="jobs/new" class="button button--primary">
                  <spring:message code="jsf.newJob"/>
                </a>
              </div>
            </c:if>
            <c:if test="${not empty allJobs}">
              <ul class="list--panel">
                <c:forEach var="job" items="${allJobs}">
                  <li class="l--pad-all-quarter">
                    <div class="list__item__content">
                      <div class="list__item__info">
                        <h3 class="list__title">${job.name}</h3>
                        <span class="list__item__meta">${job.description}</span>
                      </div>
                      <div class="list__item__actions txt--meta">
                        <span class="l--push-right-quarter">${job.lastJobStatus.status}</span>
                        <span>${job.lastJobStatus.lastEndTime}</span>
                      </div>
                    </div>
                  </li>
                </c:forEach>
              </ul>
            </c:if>
          </div>
        </div>
      </div>
    </div>
  </main>
</body>