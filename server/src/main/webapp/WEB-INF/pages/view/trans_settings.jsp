<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="txt--meta l--push-v-quarter">${selectedTransPlugin.getDescription()}</div>
<hr class="l--push-top-quarter"/>

<div class="form__item">
  <c:if test="${not empty selectedTransPlugin}">
    <c:forEach var="transPluginField" items="${selectedTransPlugin.fields.values()}">
      <c:set var="key" value="${transSettingsPrefix}${transPluginField.key}" />

      <c:choose>
        <c:when test="${not empty errors && errors.containsKey(key)}">
          <c:set var="errorClass" value="form__item--error" />
        </c:when>
        <c:otherwise>
          <c:set var="errorClass" value="" />
        </c:otherwise>
      </c:choose>

      <div class="form__item ${errorClass}" title="${transPluginField.tooltip}">
        <label for="${transPluginField.key}">${transPluginField.label}</label>
        <input id="${key}" name="${key}" type="text" placeholder="${transPluginField.placeholder}" class="l--push-bottom-quarter"/>
        <c:if test="${not empty errors && errors.containsKey(key)}">
          <span class="l--pad-all-quarter message--danger">${errors.get(key)}</span>
        </c:if>
      </div>
    </c:forEach>
  </c:if>
</div>