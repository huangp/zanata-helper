<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="txt--meta l--push-v-quarter">${selectedSrcPlugin.getDescription()}</div>
<hr class="l--push-top-quarter"/>

<div class="form__item">
  <c:if test="${not empty selectedSrcPlugin}">
    <c:forEach var="srcRepoField" items="${selectedSrcPlugin.fields.values()}">
      <jsp:include page="/WEB-INF/pages/includes/textField.jsp" >
        <jsp:param name="key" value="${repoSettingsPrefix}${srcRepoField.key}" />
        <jsp:param name="value" value="${srcRepoField.value}" />
        <jsp:param name="label" value="${srcRepoField.label}" />
        <jsp:param name="tooltip" value="${srcRepoField.tooltip}" />
        <jsp:param name="placeholder" value="${srcRepoField.placeholder}" />
      </jsp:include>
    </c:forEach>
  </c:if>
</div>