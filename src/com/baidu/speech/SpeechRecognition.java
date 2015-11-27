package com.baidu.speech;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONObject;

public class SpeechRecognition
{
  private static final String serverURL = "http://vop.baidu.com/server_api";
  private static final String apiKey = "Your aKey";
  private static final String secretKey = "Your sKey";
  private static final String cuid = "e68dadca6896fed9fca944c927b4ecca";
  private static final String token = "24.5681403a8bae468d24fb7c72022dce3d.2592000.1450326585.282335-6968461";
  public static long start = 0L;
  public static long end = 0L;
  private String fileName;

  public SpeechRecognition()
  {
  }

  public SpeechRecognition(String amrFile)
  {
    this.fileName = amrFile;
  }

  private static String getToken()
  {
    String token = "";
    String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id=5AghB7MGUSUHdhU5EgpavhYj&client_secret=e68dadca6896fed9fca944c927b4ecca";
    try
    {
      HttpURLConnection conn = (HttpURLConnection)new URL(getTokenURL)
        .openConnection();
      if (conn.getResponseCode() != 200)
      {
        return null;
      }
      InputStream is = conn.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      StringBuffer response = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null)
      {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      System.out.println(response.toString());
      token = new JSONObject(response.toString())
        .getString("access_token");
      System.out.println(token);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return token;
  }

  public JSONObject method1() throws Exception {
    start = System.currentTimeMillis();
    System.out.println("开始时间：" + start);
    File pcmFile = new File(this.fileName);
    String fileExtName = this.fileName.substring(this.fileName.lastIndexOf(".") + 1);
    HttpURLConnection conn = (HttpURLConnection)new URL("http://vop.baidu.com/server_api")
      .openConnection();

    JSONObject params = new JSONObject();
    params.put("format", fileExtName);

    params.put("rate", 16000);
    params.put("channel", "1");
    params.put("token", "24.5681403a8bae468d24fb7c72022dce3d.2592000.1450326585.282335-6968461");
    params.put("cuid", "e68dadca6896fed9fca944c927b4ecca");
    params.put("len", pcmFile.length());

    long begin = System.currentTimeMillis();
    params.put("speech", 
      DatatypeConverter.printBase64Binary(loadFile(pcmFile)));
    System.out.println("处理大小为： " + pcmFile.length() + " 字节的编码音频文件花费时间: " + (
      System.currentTimeMillis() - begin) + "ms");

    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", 
      "application/json; charset=utf-8");

    conn.setDoInput(true);
    conn.setDoOutput(true);

    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
    wr.writeBytes(params.toString());
    wr.flush();
    wr.close();
    return printResponse(conn);
  }

  public JSONObject method2() throws Exception
  {
    start = System.currentTimeMillis();
    File pcmFile = new File(this.fileName);
    String fileExtName = this.fileName.substring(this.fileName.lastIndexOf(".") + 1);
    HttpURLConnection conn = (HttpURLConnection)new URL("http://vop.baidu.com/server_api?cuid=e68dadca6896fed9fca944c927b4ecca&token=24.5681403a8bae468d24fb7c72022dce3d.2592000.1450326585.282335-6968461")
      .openConnection();

    conn.setRequestMethod("POST");

    conn.setRequestProperty("Content-Type", "audio/" + fileExtName + 
      "; rate=16000");
    conn.setDoInput(true);
    conn.setDoOutput(true);

    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
    wr.write(loadFile(pcmFile));
    wr.flush();
    wr.close();

    return printResponse(conn);
  }

  private static JSONObject printResponse(HttpURLConnection conn)
    throws Exception
  {
    if (conn.getResponseCode() != 200)
    {
      return new JSONObject();
    }
    InputStream is = conn.getInputStream();
    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

    StringBuffer response = new StringBuffer();
    String line;
    while ((line = rd.readLine()) != null)
    {
      response.append(line);
      response.append('\r');
    }
    rd.close();

    long time = System.currentTimeMillis() - start;

    JSONObject json = new JSONObject();
    json.put("result", response.toString());
    json.put("time", time);
    return json;
  }

  private static byte[] loadFile(File file) throws IOException {
    InputStream is = new FileInputStream(file);

    long length = file.length();
    byte[] bytes = new byte[(int)length];

    int offset = 0;
    int numRead = 0;
    while ((offset < bytes.length) && (
      (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
      offset += numRead;
    }

    if (offset < bytes.length) {
      is.close();
      throw new IOException("Could not completely read file " + 
        file.getName());
    }

    is.close();
    return bytes;
  }
}