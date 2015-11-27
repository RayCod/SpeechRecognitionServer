<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html>
<head>
<title>下载文件显示页面</title>
</head>

<body>
	<!-- 遍历Map集合 -->

	<c:forEach var="me" items="${fileNameMap}">
		<c:url value="/servlet/DownLoadServlet" var="downurl">
			<c:param name="filename" value="${me.key}"></c:param>
		</c:url>
        ${me.value}<a href="${downurl}">下载</a>

		<%--  <c:url value="http://vop.baidu.com/server_api" var="myUrl">
				<c:param name="lan" value="zh" />
				<c:param name="cuid" value="e68dadca6896fed9fca944c927b4ecca" />
				<c:param name="token" value="24.8c6f3efcec8469348fe4ea127a2a38bf.2592000.1447055968.282335-6968461" />
				<c:param name=""></c:param>
		</c:url>
	    <a href="${myUrl}">翻译2</a> --%>

		<br />
	</c:forEach>
</body>
</html>