<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<STYLE type="text/css">
BODY {
	MARGIN: 0px
}

.style1 {
	FONT-SIZE: 12px;
	FONT-FAMILY: Arial
}

.style3 {
	FONT-WEIGHT: bold;
	FONT-SIZE: 13px;
	COLOR: #3c5e84;
	FONT-FAMILY: Arial
}

.style4 {
	FONT-WEIGHT: bold;
	FONT-SIZE: 12px;
	COLOR: red;
	FONT-FAMILY: Arial
}

.style5 {
	FONT-WEIGHT: bold;
	FONT-SIZE: 12px;
	COLOR: black;
	FONT-FAMILY: Arial
}
</STYLE>
<title>Upload Multi File</title>
<SCRIPT language=javascript type=text/javascript>
	var fileIndex = 1;
	function addFile() {
		if (fileIndex > 9) {
			alert("同时最多只能上传10个文件！");
		} else {
			fileIndex++;
			var idx = fileIndex;
			addInputFile(idx);
		}
	}

	function checkFileSuffix() {
		var throughCheck = true;
		for (var i = 1; i <= fileIndex; i++) {
			var inputComp = document.getElementById("file" + i);
			if (inputComp.value == null || inputComp.value == "") {
				continue;
			}

			var fileExt = inputComp.value.substr(
					inputComp.value.lastIndexOf(".")).toLowerCase();
			if (!(fileExt == ".pcm" || fileExt == ".wav" || fileExt == ".opus"
					|| fileExt == ".speex" || fileExt == ".amr" || fileExt == ".x-flac")) {
				throughCheck = false;
			}
		}

		var message = document.getElementById("message");
		var UploadButton = document.getElementById("UploadButton");
		if (!throughCheck) {
			message.innerHTML = "<font style='font-family: Arial' color='red'>请选择如下类型文件 pcm/wav/opus/speex/amr/x-flac!</font>";
			UploadButton.disabled = true;
		} else {
			message.innerHTML = "<font style='font-family: Arial' color='green'>OK!</font>";
			UploadButton.disabled = false;
		}
	}

	function addInputFile(idx) {
		var span = document.getElementById("filespan");
		if (span != null) {
			var divObj = document.createElement("div");
			divObj.id = "div" + idx;

			var fileObj = document.createElement("input");
			fileObj.type = "file";
			fileObj.id = "file" + idx;
			fileObj.name = "name" + idx;
			fileObj.onchange = function() {
				checkFileSuffix()
			};
			fileObj.size = "40";
			fileObj.maxlength = "20";

			var delObj = document.createElement("input");
			delObj.id = "btn" + idx;
			delObj.type = "button";
			delObj.onclick = function() {
				delInputFile(delObj)
			};
			delObj.value = "删除";

			divObj.appendChild(document
					.createTextNode("请选择文件: "));
			divObj.appendChild(fileObj);
			divObj.appendChild(document.createTextNode(" "));
			divObj.appendChild(delObj);
			divObj.appendChild(document.createElement("br"));

			span.appendChild(divObj);
		}
	}

	function delInputFile(obj) {
		var idx = obj.id.substring(3);
		var span = document.getElementById("filespan");
		var divObj = document.getElementById("div" + idx);
		if (span != null && divObj != null) {
			idx++;

			for (var i = idx; i <= fileIndex; i++) {
				var inputComp1 = document.getElementById("file" + i);
				var inputComp2 = document.getElementById("btn" + i);
				var inputComp3 = document.getElementById("div" + i);
				i--;
				inputComp1.id = "file" + i;
				inputComp2.id = "btn" + i;
				inputComp3.id = "div" + i;
				i++;
			}

			span.removeChild(divObj);
			fileIndex--;
			checkFileSuffix();
		}
	}

	var bar = 0;
	var plot = ".";
	var plots = "";
	function uploading() {
		document.getElementById('UploadButton').disabled = true;
		document.getElementById('form1').submit();
		count();
	}

	function reseting() {
		document.getElementById("message").innerHTML = "";
		document.getElementById('UploadButton').disabled = true;
		document.getElementById('form1').reset();
	}

	function count() {
		bar = bar + 2;
		plots = plots + plot;

		if (bar > 60) {
			bar = 0;
			plots = "";
		}

		var message = document.getElementById('message');
		message.innerHTML = "<font style='font-family: Arial' color='green'>Uploading, please wait..."
				+ plots + "</font>";
		setTimeout("count()", 1000);
	}
</script>
</head>
<body>
	<form enctype="multipart/form-data" name="form1" id="form1"
		method="post"
		action="${pageContext.request.contextPath}/servlet/UpmoreHandleServlet">
		<table>
			<TBODY>
				<TR>
					<TD></TD>
					<TD width=700 height=20></TD>
					<TD></TD>
				</TR>

				<TR>
					<TD> </TD>
					<TD vAlign=top align=left>
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
							<TBODY>
								<TR>
									<TD width="4%"> </TD>
									<TD width="92%" height=20> </TD>
									<TD width="4%"> </TD>
								</TR>
								<TR>
									<TD> </TD>
									<TD height=25> <SPAN class=style3>上传多文件,文件类型需为百度语音支持的类型</SPAN></TD>
									<TD> </TD>
								</TR>
								<TR>
									<TD colspan="3" height="10"> </TD>
								</TR>
								<tr>
									<TD> </TD>
									<td id="filespan">
										<div>
											请选择文件: <input type="file" onchange="checkFileSuffix()"
												name="file1" id="file1" size="40" maxlength="20"> <input
												id="btnAdd" onclick="addFile()" type="button"
												value="增加一个文件"><br>
										</div>
									</td>
									<TD> </TD>
								</tr>

								<TR>
									<TD> </TD>
									<TD align=center height=40>
										<div id="message">
											<%
												if ((request.getParameter("error")) != null
														&& (request.getParameter("error")).equals("figureTooLarge")) {
											%>
											<font color='red'>The current system accepts file
												with maximum size 5 MB!</font>
											<%
												}
											%>
										</div>
									</TD>
									<TD> </TD>
								</TR>
								<TR>
									<TD> </TD>
									<TD align=center height=40><input type="button"
										id="UploadButton" value="上传文件"
										onclick="uploading();" disabled />  <input type="button"
										id="ResetButton" value="重置" onclick="reseting();" /> </TD>
									<TD> </TD>
								</TR>
							</TBODY>
						</TABLE>
					</TD>
					<TD> </TD>
				</TR>
			</TBODY>
		</table>
	</form>
</body>
</html>