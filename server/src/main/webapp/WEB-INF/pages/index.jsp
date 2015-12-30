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
          <div class="l--push-top-quarter panel" id="runningJobsContent">
            <jsp:include page="view/running_jobs.jsp" />
          </div>
        </div>

        <div class="g__item w--1-m w--5-8-l w--3-4">
          <div class="panel">
            <div class="panel__header">
              <div class="panel__header__actions">
                <div class="dropdown dropdown--header dropdown--small dropdown--right js-dropdown">
                  <a class="dropdown__toggle js-dropdown__toggle" href="#">
                    <i class="i i--arrow-down dropdown__toggle__icon"></i>
                    <i class="i i--ellipsis"></i>
                  </a>
                  <ul class="dropdown__content js-dropdown__content" role="content" aria-labelledby="dropdownContent">
                    <li>
                      <a class="i__item--right">
                        <spring:message code="jsf.newJob"/><i class="i i--plus i__item__icon"></i>
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
              <h2 class="panel__heading">
                <spring:message code="jsp.allJobs.title"/>
              </h2>
            </div>

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
                  <c:set var="title">
                    <spring:message code="jsf.job.status.title" arguments="${job.lastJobStatus.status}" />
                  </c:set>

                  <c:choose>
                    <c:when test="${job.lastJobStatus.status.name() eq 'RUNNING'}">
                      <c:set var="statusClass" value="txt--unsure" />
                    </c:when>
                    <c:when test="${job.lastJobStatus.status.name() eq 'ERROR'}">
                      <c:set var="statusClass" value="txt--danger" />
                    </c:when>
                    <c:when test="${job.lastJobStatus.status.name() eq 'COMPLETE'}">
                      <c:set var="statusClass" value="txt--success" />
                    </c:when>
                    <c:when test="${job.lastJobStatus.status.name() eq 'PAUSE'}">
                      <c:set var="statusClass" value="txt--neutral" />
                    </c:when>
                    <c:when test="${job.lastJobStatus.status.name() eq 'NORMAL'}">
                      <c:set var="statusClass" value="" />
                    </c:when>
                    <c:otherwise>
                      <c:set var="statusClass" value="txt--neutral" />
                      <c:set var="title">
                        <spring:message code="jsf.job.status.none.title"/>
                      </c:set>
                    </c:otherwise>
                  </c:choose>

                  <li class="l--pad-all-quarter">
                    <a href="#">
                      <div class="list__item__content">
                        <div class="list__item__info">
                          <h3 class="list__title">${job.name}</h3>
                          <span class="list__item__meta">${job.description}</span>
                        </div>
                        <div class="list__item__actions txt--meta" title="${title}">
                          <span class="l--push-right-quarter ${statusClass}">
                            <i class="i i--clock"></i>
                          </span>
                          <span>${job.lastJobStatus.lastEndTime}</span>
                        </div>
                      </div>
                    </a>
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