package com.baidu.speech;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet({"/servlet/ListFileServlet"})
public class ListFileServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String uploadFilePath = getServletContext().getRealPath("/WEB-INF/upload");

    Map fileNameMap = new HashMap();

    listfile(new File(uploadFilePath), fileNameMap);

    request.setAttribute("fileNameMap", fileNameMap);
    request.getRequestDispatcher("/listfile.jsp").forward(request, response);
  }

  public void listfile(File file, Map<String, String> map)
  {
    if (!file.isFile())
    {
      File[] files = file.listFiles();

      for (File f : files)
      {
        listfile(f, map);
      }

    }
    else
    {
      String realName = file.getName().substring(file.getName().indexOf("_") + 1);

      map.put(file.getName(), realName);
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doGet(request, response);
  }
}