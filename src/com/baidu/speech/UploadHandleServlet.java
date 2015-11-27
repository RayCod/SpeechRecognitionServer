package com.baidu.speech;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

@WebServlet({"/servlet/UploadHandleServlet"})
public class UploadHandleServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String savePath = getServletContext().getRealPath(
      "/WEB-INF/upload");

    String tempPath = getServletContext().getRealPath("/WEB-INF/temp");
    File tmpFile = new File(tempPath);
    if (!tmpFile.exists())
    {
      tmpFile.mkdir();
    }

    String message = "";
    try
    {
      DiskFileItemFactory factory = new DiskFileItemFactory();

      factory.setSizeThreshold(102400);

      factory.setRepository(tmpFile);

      ServletFileUpload upload = new ServletFileUpload(factory);

      upload.setProgressListener(new ProgressListener()
      {
        public void update(long pBytesRead, long pContentLength, int arg2) {
          System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + 
            pBytesRead);
        }
      });
      upload.setHeaderEncoding("UTF-8");

      if (!ServletFileUpload.isMultipartContent(request))
      {
        return;
      }

      long fileSizeUnint = 1048576L;

      upload.setFileSizeMax(fileSizeUnint * 3L);

      upload.setSizeMax(fileSizeUnint * 10L);

      List<FileItem> list = upload.parseRequest(request);
      for (FileItem item : list)
      {
        if (item.isFormField()) {
          String name = item.getFieldName();

          String value = item.getString("UTF-8");

          System.out.println(name + "=" + value);
        }
        else
        {
          String filename = item.getName();
          System.out.println(filename);
          if ((filename != null) && (!filename.trim().equals("")))
          {
            filename = filename
              .substring(filename.lastIndexOf("\\") + 1);

            String fileExtName = filename.substring(filename
              .lastIndexOf(".") + 1);

            System.out.println("上传的文件的扩展名是：" + fileExtName);

            InputStream in = item.getInputStream();

            String saveFilename = makeFileName(filename);

            String realSavePath = makePath(saveFilename, savePath);

            FileOutputStream out = new FileOutputStream(realSavePath + 
              "\\" + saveFilename);

            byte[] buffer = new byte[1024];

            int len = 0;

            while ((len = in.read(buffer)) > 0)
            {
              out.write(buffer, 0, len);
            }

            in.close();

            out.close();

            message = "文件上传成功！";

            if ("amr".equals(fileExtName))
            {
              message = "客户端上传的语音" + message;

              String amrFile = realSavePath + 
                "\\" + saveFilename;

              SpeechRecognition sr = new SpeechRecognition(amrFile);
              JSONObject obj = sr.method1();
              System.out.println(obj.get("result"));
              System.out.println("识别整个过程花费的时间是：" + obj.get("time") + " ms");
            }
          }
        }
      }
    }
    catch (FileUploadBase.FileSizeLimitExceededException e)
    {
      e.printStackTrace();
      request.setAttribute("message", "单个文件超出最大值！！！");
      request.getRequestDispatcher("/message.jsp").forward(request, 
        response);
      return;
    } catch (FileUploadBase.SizeLimitExceededException e) {
      e.printStackTrace();
      request.setAttribute("message", "上传文件的总的大小超出限制的最大值！！！");
      request.getRequestDispatcher("/message.jsp").forward(request, 
        response);
      return;
    } catch (Exception e) {
      message = "文件上传失败！";
      e.printStackTrace();
    }
    request.setAttribute("message", message);
    request.getRequestDispatcher("/message.jsp").forward(request, response);
  }

  private String makePath(String filename, String savePath)
  {
    int hashcode = filename.hashCode();
    int dir1 = hashcode & 0xF;
    int dir2 = (hashcode & 0xF0) >> 4;

    String dir = savePath + "\\" + dir1 + "\\" + dir2;

    File file = new File(dir);

    if (!file.exists())
    {
      file.mkdirs();
    }
    return dir;
  }

  private String makeFileName(String filename)
  {
    return UUID.randomUUID().toString() + "_" + filename;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doGet(request, response);
  }
}