<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="txt--meta l--push-v-quarter">${selectedSrcPlugin.getDescription()}</div>
<hr class="l--push-top-quarter"/>

<div class="form__item">
  <c:if test="${not empty selectedSrcPlugin}">
    <c:forEach var="srcRepoField" items="${selectedSrcPlugin.fields.values()}">
      <div class="form__item" title="${srcRepoField.tooltip}">
        <label for="${srcRepoField.key}">${srcRepoField.label}</label>
        <form:input path="sourceRepoConfig['${srcRepoField.key}']"
            id="${srcRepoField.key}" placeholder="${srcRepoField.placeholder}"/>
        <form:errors path="sourceRepoConfig['${srcRepoField.key}']"
            cssClass="l--pad-all-quarter message--danger"/>
      </div>
    </c:forEach>
  </c:if>
</div>