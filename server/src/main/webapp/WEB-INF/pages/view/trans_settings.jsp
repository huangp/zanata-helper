<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="txt--meta l--push-v-quarter">${selectedTransPlugin.getDescription()}</div>
<hr class="l--push-top-quarter"/>

<div class="form__item">
  <c:if test="${not empty selectedTransPlugin}">
    <c:forEach var="transPluginField" items="${selectedTransPlugin.fields.values()}">
      <jsp:include page="/WEB-INF/pages/includes/textField.jsp" >
        <jsp:param name="key" value="${transSettingsPrefix}${transPluginField.key}" />
        <jsp:param name="value" value="${transPluginField.value}" />
        <jsp:param name="label" value="${transPluginField.label}" />
        <jsp:param name="tooltip" value="${transPluginField.tooltip}" />
        <jsp:param name="placeholder" value="${transPluginField.placeholder}" />
      </jsp:include>
    </c:forEach>
  </c:if>
</div>