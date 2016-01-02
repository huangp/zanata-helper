<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="key" value="${param.key}" />
<c:set var="value" value="${param.value}" />
<c:set var="label" value="${param.label}" />
<c:set var="tooltip" value="${param.tooltip}" />
<c:set var="placeholder" value="${param.placeholder}" />
<c:set var="maxlength" value="${param.maxlength}" />
<c:set var="type" value="${param.type}" />

<c:choose>
  <c:when test="${not empty errors && errors.containsKey(key)}">
    <c:set var="errorClass" value="form__item--error" />
  </c:when>
  <c:otherwise>
    <c:set var="errorClass" value="" />
  </c:otherwise>
</c:choose>

<div class="form__item ${errorClass}" title="${tooltip}">
  <label for="${key}">${label}</label>

  <c:choose>
    <c:when test="${type eq 'textArea'}">
      <textarea name="${key}" id="${key}" class="l--push-bottom-quarter">${value}</textarea>
    </c:when>
    <c:otherwise>
      <input id="${key}" name="${key}" type="text" maxlength="${maxlength}" placeholder="${placeholder}" class="l--push-bottom-quarter" value="${value}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${not empty errors && errors.containsKey(key)}">
    <span class="l--pad-all-quarter message--danger">${errors.get(key)}</span>
  </c:if>
</div>