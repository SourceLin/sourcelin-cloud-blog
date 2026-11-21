package com.sourcelin.ai.security;
import org.junit.jupiter.api.Test;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;
class AiApiKeyTypeHandlerTest {
 @Test void encryptsWithUniqueNonceAndRejectsWrongKey() throws Exception {
  byte[] key=new byte[32];new java.security.SecureRandom().nextBytes(key);
  AiApiKeyTypeHandler handler=new AiApiKeyTypeHandler(Base64.getEncoder().encodeToString(key));
  String first=handler.encrypt("test-provider-key");String second=handler.encrypt("test-provider-key");
  assertNotEquals(first,second);assertFalse(first.contains("test-provider-key"));assertEquals("test-provider-key",handler.decrypt(first));
  key[0]^=1;
  AiApiKeyTypeHandler wrong=new AiApiKeyTypeHandler(Base64.getEncoder().encodeToString(key));
  assertThrows(Exception.class,() -> wrong.decrypt(first));
 }
 @Test void missingKeyDoesNotStorePlaintext() {assertThrows(IllegalStateException.class,() -> new AiApiKeyTypeHandler("").encrypt("test"));}
}
