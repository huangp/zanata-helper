<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="txt--meta l--push-v-quarter">${selectedTransPlugin.getDescription()}</div>
<hr class="l--push-top-quarter"/>

<div class="form__item">
  <c:if test="${not empty selectedTransPlugin}">
    <c:forEach var="transPluginField" items="${selectedTransPlugin.fields.values()}">
      <div class="form__item" title="${transPluginField.tooltip}">
        <label for="${transPluginField.key}">${transPluginField.label}</label>
        <form:input path="transServerConfig['${transPluginField.key}']"
            id="${transPluginField.key}"
            placeholder="${transPluginField.placeholder}"/>
        <form:errors path="transServerConfig['${transPluginField.key}']"
            cssClass="l--pad-all-quarter message--danger"/>
      </div>
    </c:forEach>
  </c:if>
</div>