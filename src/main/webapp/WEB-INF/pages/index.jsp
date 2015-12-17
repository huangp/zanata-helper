<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="includes/header.jsp"/>

<body class="new-zanata new-zanata-base">
<h1><spring:message code="Application.name"/></h1>

<h3>Your Message : ${msg}</h3>
</body>