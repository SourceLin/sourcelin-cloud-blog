package com.sourcelin.ai.security;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

/** API 密钥只以 AES-GCM 密文落库；密钥来自部署环境。 */
public class AiApiKeyTypeHandler extends BaseTypeHandler<String> {
 private final String configuredKey;
 public AiApiKeyTypeHandler(){ this(System.getenv("AI_CONFIG_ENCRYPTION_KEY")); }
 public AiApiKeyTypeHandler(String key){this.configuredKey=key;}
 private SecretKeySpec key() {
  if(configuredKey == null || configuredKey.isEmpty()) throw new IllegalStateException("未配置 AI_CONFIG_ENCRYPTION_KEY");
  byte[] bytes=Base64.getDecoder().decode(configuredKey);
  if(bytes.length != 32) throw new IllegalStateException("AI_CONFIG_ENCRYPTION_KEY 必须为 Base64 编码的 32 字节密钥");
  return new SecretKeySpec(bytes,"AES");
 }
 public String encrypt(String value) throws Exception {
  byte[] iv=new byte[12];new SecureRandom().nextBytes(iv);
  Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,key(),new GCMParameterSpec(128,iv));
  return "v1:"+Base64.getEncoder().encodeToString(iv)+":"+Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
 }
 public String decrypt(String value) throws Exception {
  if(value == null || value.isEmpty()) return value;
  String[] parts=value.split(":",3);if(parts.length!=3 || !"v1".equals(parts[0])) throw new IllegalStateException("密钥密文格式无效，请通过后台重新配置");
  Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,Base64.getDecoder().decode(parts[1])));
  return new String(cipher.doFinal(Base64.getDecoder().decode(parts[2])),java.nio.charset.StandardCharsets.UTF_8);
 }
 public void setNonNullParameter(PreparedStatement ps,int index,String value,JdbcType type) throws SQLException {try{ps.setString(index,encrypt(value));}catch(Exception e){throw new SQLException("AI 密钥加密失败",e);}}
 private String decode(String value) throws SQLException {try{return decrypt(value);}catch(Exception e){throw new SQLException("AI 密钥解密失败",e);}}
 public String getNullableResult(ResultSet rs,String column) throws SQLException{return decode(rs.getString(column));}
 public String getNullableResult(ResultSet rs,int index) throws SQLException{return decode(rs.getString(index));}
 public String getNullableResult(CallableStatement cs,int index) throws SQLException{return decode(cs.getString(index));}
}
