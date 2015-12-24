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

        <form:form commandName="jobForm" action="/jobs/new">
          <div class="g__item l--push-top-1">
            <div class="form__item">
              <label for="name"><spring:message code="jsf.newJob.name"/></label>
              <form:input path="name" id="name" maxlength="100"/>
              <form:errors path="name" cssClass="l--pad-all-quarter message--danger"/>
            </div>

            <div class="form__item">
              <label for="description"><spring:message code="jsf.newJob.description"/></label>
              <form:textarea path="description" id="description"/>
              <form:errors path="description" cssClass="l--pad-all-quarter message--danger">
              </form:errors>
            </div>

            <div class="form__item">
              <label for="jobType"><spring:message code="jsf.newJob.jobType"/></label>
              <spring:message code="jsf.newJob.jobType.SyncToZanata.explanation" var="SyncToZanataLabel"/>
              <spring:message code="jsf.newJob.jobType.SyncToRepo.explanation" var="SyncToRepoLabel"/>
              <form:select path="jobType" id="jobType">
                <form:option value="SYNC_TO_ZANATA" label="${SyncToZanataLabel}"/>
                <form:option value="SYNC_TO_REPO" label="${SyncToRepoLabel}"/>
              </form:select>
            </div>
            <div class="form__item">
              <label for="jobType"><spring:message code="jsf.newJob.syncType"/></label>
              <spring:message code="jsf.newJob.syncType.sourceOnly.explanation" var="sourceOnlyLabel"/>
              <spring:message code="jsf.newJob.syncType.translationsOnly.explanation" var="translationsOnlyLabel"/>
              <spring:message code="jsf.newJob.syncType.both.explanation" var="syncBothLabel"/>
              <form:select path="syncType" id="syncType">
                <form:option value="SOURCE" label="${sourceOnlyLabel}"/>
                <form:option value="TRANSLATIONS" label="${translationsOnlyLabel}"/>
                <form:option value="BOTH" label="${syncBothLabel}"/>
              </form:select>
            </div>

            <div class="form__item">
              <spring:message code="jsf.newJob.cron.placeholder" var="cronPlaceholder"/>
              <label for="cron"><spring:message code="jsf.newJob.cron.label"/>
                <a href="http://en.wikipedia.org/wiki/Cron#CRON_expression" target="_blank"><i class="i i--info"></i></a></label>
              <form:input path="cron" id="cron" maxlength="50" placeholder="${cronPlaceholder}"/>
              <form:errors path="cron" cssClass="l--pad-all-quarter message--danger"/>
            </div>

            <h2 class="heading--secondary l--push-top-half">
              <spring:message code="jsf.newJob.source_repo"/>
            </h2>
            <form:select path="sourceRepoExecutorName" id="sourceRepoExecutorName">
              <c:forEach var="plugin" items="${repoPluginOptions}">
                <form:option value="${plugin.getClass().getName()}" label="${plugin.getName()}"/>
              </c:forEach>
            </form:select>
            <hr class="l--push-top-quarter"/>
            <div id="sourceRepoConfig">
              <%--<div class="form__item">--%>
                <%--<spring:message code="jsf.newJob.source_repo_url.placeholder" var="sourceRepoPlaceholder"/>--%>
                <%--<form:input path="sourceUrl" id="sourceUrl" placeholder="${sourceRepoPlaceholder}"/>--%>
                <%--<form:errors path="sourceUrl" cssClass="l--pad-all-quarter message--danger"/>--%>
              <%--</div>--%>

              <%--<div class="form__item">--%>
                <%--<label for="sourceUsername"><spring:message code="jsf.newJob.username"/></label>--%>
                <%--<spring:message code="jsf.newJob.username.tooltip" var="sourceRepoUsername"/>--%>
                <%--<form:input path="sourceUsername" id="sourceUsername" title="${sourceRepoUsername}"/>--%>
                <%--<form:errors path="sourceUsername" cssClass="l--pad-all-quarter message--danger"/>--%>
              <%--</div>--%>

              <%--<div class="form__item">--%>
                <%--<label for="sourceApiKey"><spring:message code="jsf.newJob.apiKey"/></label>--%>
                <%--<spring:message code="jsf.newJob.apikey.tooltip" var="sourceRepoApi"/>--%>
                <%--<form:input path="sourceApiKey" id="sourceApiKey" title="${sourceRepoApi}"/>--%>
                <%--<form:errors path="sourceApiKey" cssClass="l--pad-all-quarter message--danger"/>--%>
              <%--</div>--%>
            </div>


            <h2 class="heading--secondary l--push-top-half">
              <spring:message code="jsf.newJob.trans_server"/>
            </h2>
            <form:select path="translationServerExecutorName" id="translationServerExecutorName">
              <c:forEach var="plugin" items="${serverPluginOptions}">
                <form:option value="${plugin.getClass().getName()}" label="${plugin.getName()}"/>
              </c:forEach>
            </form:select>
            <hr class="l--push-top-quarter"/>

            <div id="transServerConfig">
              <%--<div class="form__item">--%>
                <%--<spring:message code="jsf.newJob.trans_server.placeholder" var="zanataRepoPlaceholder"/>--%>
                <%--<form:input path="zanataUrl" id="zanataUrl" placeholder="${zanataRepoPlaceholder}"/>--%>
                <%--<form:errors path="zanataUrl" cssClass="l--pad-all-quarter message--danger"/>--%>
              <%--</div>--%>

              <%--<div class="form__item">--%>
                <%--<label for="zanataUsername"><spring:message code="jsf.newJob.username"/></label>--%>
                <%--<spring:message code="jsf.newJob.username.tooltip" var="zanataUsername"/>--%>
                <%--<form:input path="zanataUsername" id="zanataUsername" title="${zanataUsername}"/>--%>
                <%--<form:errors path="zanataUsername" cssClass="l--pad-all-quarter message--danger"/>--%>
              <%--</div>--%>

              <%--<div class="form__item">--%>
                <%--<label for="zanataApiKey"><spring:message code="jsf.newJob.apiKey"/></label>--%>
                <%--<spring:message code="jsf.newJob.apikey.tooltip" var="zanataApiKey"/>--%>
                <%--<form:input path="zanataApiKey" id="sourceApiKey" title="${zanataApiKey}"/>--%>
                <%--<form:errors path="zanataApiKey" cssClass="l--pad-all-quarter message--danger"/>--%>
              <%--</div>--%>
            </div>

            <div class="form__item">
              <button class="button button--primary button--full">
                <spring:message code="jsf.newJob.createJob.button"/>
              </button>
            </div>
          </div>
        </form:form>
      </div>
    </div>
  </div>
</main>
</body>