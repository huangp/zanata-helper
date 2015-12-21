<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="includes/header.jsp"/>

<body class="new-zanata new-zanata-base">
  <main role="main" class="l--pad-v-1">
    <script type="text/javascript">
      $(document).ready(function () {
        window.location.reload(true);
      });
    </script>

    <div class="l__wrapper">
      <div class="g">
        <div class="g__item w--1-m w--3-8-l w--1-4 l--push-bottom-half">
          <h1><spring:message code="Application.name"/></h1>

          <div class="l--pad-all-quarter panel">
            <c:if test="${empty runningJobs}">
              <p class="txt--meta"><spring:message code="jsp.noRunningJobs"/></p>
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
          </div>
        </div>

        <div class="g__item w--1-m w--5-8-l w--3-4">
          <h2 class="l--push-top-0"><spring:message code="jsp.allAvailableJobs.title"/></h2>
          <div class="l--pad-all-quarter panel">
            <ul class="list--panel">
              <c:forEach var="job" items="${allJobs}">
                <li class="l--pad-all-quarter">
                  <div class="list__item__content">
                    <div class="list__item__info">
                      <h3 class="list__title">${job.name}</h3>
                      <span class="list__item__meta">${job.description}</span>
                    </div>
                    <div class="list__item__actions">
                      <span class="txt--understated">
                          Last run: <fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${job.lastRun}"/>
                      </span>
                    </div>
                  </div>
                </li>
              </c:forEach>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </main>
</body>