package com.sourcelin.file.service;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.sourcelin.common.core.exception.BusinessException;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.*;
class RemoteMediaDownloaderTest {
 @Test void rejectsLocalAddressesEvenWhenHostIsAllowlisted() {
  RemoteMediaDownloader downloader=new RemoteMediaDownloader();ReflectionTestUtils.setField(downloader,"allowedHosts","127.0.0.1");
  assertThrows(BusinessException.class,() -> downloader.validate(new URL("https://127.0.0.1/private")));
 }
 @Test void rejectsProtocolCredentialsPortsAndUnlistedHosts() {
  RemoteMediaDownloader downloader=new RemoteMediaDownloader();ReflectionTestUtils.setField(downloader,"allowedHosts","8.8.8.8");
  for(String url:new String[]{"http://8.8.8.8/x","https://user:pass@8.8.8.8/x","https://8.8.8.8:8080/x","https://127.0.0.1/x"})
   assertThrows(BusinessException.class,() -> downloader.validate(new URL(url)));
 }
}
