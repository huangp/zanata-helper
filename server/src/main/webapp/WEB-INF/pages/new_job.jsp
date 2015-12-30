<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="includes/header.jsp"/>

<body class="new-zanata new-zanata-base">
<main role="main" class="l--pad-v-1">
  <div class="l--wrapper">
    <div class="g--centered">
      <div class="g__item w--1-2-m w--3-8-l w--1-2 l--push-bottom-1">
        <p class="txt--meta l--push-all-0"><a href="/"><spring:message code="jsf.home"/></a>
        </p>
        <h1 class="l--push-all-0"><spring:message code="jsf.newJob"/></h1>

        <form:form commandName="jobForm" action="/jobs/new" method="post">
          <div class="g__item l--push-top-1">
            <div class="form__item">
              <label for="name"><spring:message code="jsf.newJob.name"/></label>
              <form:input path="name" id="name" maxlength="100" cssClass="l--push-bottom-quarter"/>
              <form:errors path="name" cssClass="l--pad-all-quarter message--danger"/>
            </div>

            <div class="form__item">
              <label for="description"><spring:message code="jsf.newJob.description"/></label>
              <form:textarea path="description" id="description" cssClass="l--push-bottom-quarter"/>
              <form:errors path="description" cssClass="l--pad-all-quarter message--danger">
              </form:errors>
            </div>

            <div class="form__item">
              <label for="jobType"><spring:message code="jsf.newJob.jobType"/></label>
              <form:select path="jobType" id="jobType">
                <c:forEach var="option" items="${jobTypes}">
                  <form:option value="${option.key}" label="${option.label}"/>
                </c:forEach>
              </form:select>
            </div>
            <div class="form__item">
              <label for="jobType"><spring:message code="jsf.newJob.syncType"/></label>
              <form:select path="syncType" id="syncType">
                <c:forEach var="option" items="${syncTypes}">
                  <form:option value="${option.key}" label="${option.label}"/>
                </c:forEach>
              </form:select>
            </div>
            <div class="form__item">
              <input type="checkbox" name="repeatJob" onclick="toggleSection(this, 'cronConfigSection')"> Repeat job
            </div>
            <div class="form__item is-hidden" id="cronConfigSection">
              <spring:message code="jsf.newJob.cron.placeholder" var="cronPlaceholder"/>
              <label for="cron"><spring:message code="jsf.newJob.cron.label"/>
                <a href="http://en.wikipedia.org/wiki/Cron#CRON_expression" target="_blank"><i class="i i--info"></i></a></label>
              <form:input path="cron" id="cron" maxlength="50" placeholder="${cronPlaceholder}"/>
              <form:errors path="cron" cssClass="l--pad-all-quarter message--danger"/>
            </div>

            <h2 class="heading--secondary l--push-top-half">
              <spring:message code="jsf.newJob.source_repo"/>
            </h2>
            <form:select path="sourceRepoExecutorName" id="sourceRepoExecutorName" onchange="onPluginChanged(this, 'repo')">
              <c:forEach var="plugin" items="${repoPluginOptions}">
                <form:option value="${plugin.getClass().getName()}" label="${plugin.getName()}"/>
              </c:forEach>
            </form:select>

            <div id="sourceRepoSettings">
              <jsp:include page="view/repo_settings.jsp" />
            </div>

            <h2 class="heading--secondary l--push-top-half">
              <spring:message code="jsf.newJob.trans_server"/>
            </h2>
            <form:select path="translationServerExecutorName" id="translationServerExecutorName" onchange="onPluginChanged(this, 'trans')">
              <c:forEach var="plugin" items="${serverPluginOptions}">
                <form:option value="${plugin.getClass().getName()}" label="${plugin.getName()}"/>
              </c:forEach>
            </form:select>

            <div id="transServerSettings" class="form__item">
              <jsp:include page="view/trans_settings.jsp" />
            </div>

            <div class="form__item l--push-top-1">
              <button class="button button--primary button--full loader">
                <span class="loader__label">
                  <spring:message code="jsf.newJob.createJob.button"/>
                </span>
              </button>
            </div>
          </div>
        </form:form>
      </div>
    </div>
  </div>
</main>
</body>