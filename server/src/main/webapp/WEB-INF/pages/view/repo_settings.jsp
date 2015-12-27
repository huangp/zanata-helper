<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="txt--meta l--push-v-quarter">${selectedSrcPlugin.getDescription()}</div>
<hr class="l--push-top-quarter"/>

<div class="form__item">
  <c:if test="${not empty selectedSrcPlugin}">
    <c:forEach var="srcRepoField" items="${selectedSrcPlugin.fields.values()}">
      <c:set var="key" value="${repoSettingsPrefix}${srcRepoField.key}" />

      <c:choose>
        <c:when test="${not empty errors && errors.containsKey(key)}">
          <c:set var="errorClass" value="form__item--error" />
        </c:when>
        <c:otherwise>
          <c:set var="errorClass" value="" />
        </c:otherwise>
      </c:choose>

      <div class="form__item ${errorClass}" title="${srcRepoField.tooltip}">
        <label for="${srcRepoField.key}">${srcRepoField.label}</label>
        <input id="${key}" name="${key}" type="text" placeholder="${srcRepoField.placeholder}" class="l--push-bottom-quarter"/>
        <c:if test="${not empty errors && errors.containsKey(key)}">
          <span class="l--pad-all-quarter message--danger">${errors.get(key)}</span>
        </c:if>
      </div>
    </c:forEach>
  </c:if>
</div>