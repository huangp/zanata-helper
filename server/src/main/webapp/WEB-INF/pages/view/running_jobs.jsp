<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty runningJobs}">
  <p class="txt--meta l--push-top-half"><spring:message code="jsp.noRunningJobs"/></p>
</c:if>
<c:if test="${not empty runningJobs}">
  <ul class="list--panel">
    <c:forEach var="job" items="${runningJobs}">
      <li class="l--pad-all-quarter">
        <div class="list__item__content">
          <div class="list__item__info">
            <h3 class="list__title">${job.name}</h3>
            <span class="list__item__meta">${job.description}</span>
          </div>
          <div class="list__item__actions">
            <button>
              <i class="i txt--danger i--cancel"/>
            </button>
          </div>
        </div>
      </li>
    </c:forEach>
  </ul>
</c:if>