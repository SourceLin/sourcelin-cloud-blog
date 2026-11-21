package com.sourcelin.file.service;

import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;

/** 仅下载明确允许的 HTTPS 图片；每次重定向都重新检查地址与大小。 */
@Component
public class RemoteMediaDownloader {
 @Value("${file.remote.allowed-hosts:}") private String allowedHosts;
 @Value("${file.remote.max-bytes:10485760}") private long maxBytes;

 void validate(URL url) throws IOException {
  String host = url.getHost().toLowerCase(Locale.ROOT);
  boolean allowed = Arrays.stream(allowedHosts.split(",")).map(String::trim).anyMatch(host::equalsIgnoreCase);
  if (!"https".equals(url.getProtocol()) || url.getUserInfo() != null || (url.getPort() != -1 && url.getPort() != 443) || !allowed)
   throw new BusinessException(ResultCode.BAD_REQUEST, "文件来源不在允许的 HTTPS 域名中");
  for(InetAddress address : InetAddress.getAllByName(host))
   if(address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress() || address.isSiteLocalAddress() || address.isMulticastAddress())
    throw new BusinessException(ResultCode.BAD_REQUEST, "禁止访问内网文件地址");
 }

 public MultipartFile download(String value) throws IOException {
  if(value == null || value.length() > 4096) throw new BusinessException(ResultCode.BAD_REQUEST);
  URL url = new URL(value);
  for(int hop=0; hop<4; hop++) {
   validate(url);
   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
   connection.setInstanceFollowRedirects(false); connection.setConnectTimeout(5000); connection.setReadTimeout(10000);
   try {
    int status = connection.getResponseCode();
    if(status >= 300 && status < 400) { String location=connection.getHeaderField("Location"); if(location == null) break; url = new URL(url, location); continue; }
    if(status != 200) throw new BusinessException(ResultCode.REMOTE_SERVICE_ERROR, "远程图片下载失败");
    String type = connection.getContentType(); if(type != null) type=type.split(";")[0].trim().toLowerCase(Locale.ROOT);
    Map<String,String> extensions = new HashMap<>(); extensions.put("image/png","png"); extensions.put("image/jpeg","jpg"); extensions.put("image/webp","webp"); extensions.put("image/gif","gif");
    if(!extensions.containsKey(type)) throw new BusinessException(ResultCode.BAD_REQUEST, "仅支持 PNG、JPEG、WebP、GIF 图片");
    if(connection.getContentLengthLong() > maxBytes) throw new BusinessException(ResultCode.BAD_REQUEST, "远程图片超过大小限制");
    try(InputStream stream=connection.getInputStream(); ByteArrayOutputStream output=new ByteArrayOutputStream()) {
     byte[] buffer=new byte[8192]; int count;
     while((count=stream.read(buffer))!=-1) { if(output.size() + (long)count > maxBytes) throw new BusinessException(ResultCode.BAD_REQUEST, "远程图片超过大小限制"); output.write(buffer,0,count); }
     return new DownloadedImage(output.toByteArray(), type, "ai-" + UUID.randomUUID() + "." + extensions.get(type));
    }
   } finally { connection.disconnect(); }
  }
  throw new BusinessException(ResultCode.BAD_REQUEST, "远程图片重定向过多");
 }

 private static class DownloadedImage implements MultipartFile {
  private final byte[] content; private final String type; private final String filename;
  DownloadedImage(byte[] content,String type,String filename) { this.content=content;this.type=type;this.filename=filename; }
  public String getName(){return "file";} public String getOriginalFilename(){return filename;}
  public String getContentType(){return type;} public boolean isEmpty(){return content.length==0;}
  public long getSize(){return content.length;} public byte[] getBytes(){return content;}
  public InputStream getInputStream(){return new ByteArrayInputStream(content);}
  public void transferTo(File target) throws IOException {Files.write(target.toPath(),content);}
 }
}
