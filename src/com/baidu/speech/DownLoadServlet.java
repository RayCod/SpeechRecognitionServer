package com.baidu.speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet({ "/servlet/DownLoadServlet" })
public class DownLoadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("filename");
		fileName = new String(fileName.getBytes("iso8859-1"), "UTF-8");

		String fileSaveRootPath = getServletContext().getRealPath(
				"/WEB-INF/upload");

		String path = findFileSavePathByFileName(fileName, fileSaveRootPath);

		File file = new File(path + "\\" + fileName);

		if (!file.exists()) {
			request.setAttribute("message", "您要下载的资源已被删除！！");
			request.getRequestDispatcher("/message.jsp").forward(request,
					response);
			return;
		}

		String realname = fileName.substring(fileName.indexOf("_") + 1);

		response.setHeader("content-disposition", "attachment;filename="
				+ URLEncoder.encode(realname, "UTF-8"));

		FileInputStream in = new FileInputStream(path + "\\" + fileName);

		OutputStream out = response.getOutputStream();

		byte[] buffer = new byte[1024];
		int len = 0;

		while ((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}

		in.close();

		out.close();
	}

	public String findFileSavePathByFileName(String filename,
			String saveRootPath) {
		int hashcode = filename.hashCode();
		int dir1 = hashcode & 0xF;
		int dir2 = (hashcode & 0xF0) >> 4;
		String dir = saveRootPath + "\\" + dir1 + "\\" + dir2;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}