<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="l--pad-all-quarter d--bottom">
  <button class="button button--small button--primary loader" onclick="refreshRunningJobs()">
    <span class="loader__label">
      <spring:message code="jsf.newJob.refresh.button"/>
    </span>
  </button>
</div>
<c:if test="${empty runningJobs}">
  <p class="txt--meta l--pad-all-half"><spring:message code="jsp.noRunningJobs"/></p>
</c:if>
<c:if test="${not empty runningJobs}">
  <ul class="list--panel" id="runningJobs">
    <c:forEach var="job" items="${runningJobs}">
      <li class="l--pad-all-quarter" id="running-${job.id}">
        <a href="/job?id=${job.id}">
          <div class="list__item__content">
            <div class="list__item__info">
              <span>${job.name}</span>
              <span class="list__item__meta">${job.description}</span>
            </div>
            <div class="list__item__actions">
              <c:set var="cancelButtonTitle">
                <spring:message code="jsf.job.cancel.button.title"/>
              </c:set>
              <button class="button--small button--danger loader" onclick="cancelRunningJob(${job.id})"
                  title="${cancelButtonTitle}">
                <span class="loader__label">
                  <i class="i i--cancel"></i>
                </span>
              </button>
            </div>
          </div>
        </a>
      </li>
    </c:forEach>
  </ul>
</c:if>