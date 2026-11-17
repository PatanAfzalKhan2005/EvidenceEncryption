package com.imageseal.service;
import org.springframework.stereotype.Service;import java.security.MessageDigest;import java.security.NoSuchAlgorithmException;import java.util.HexFormat;
@Service public class Sha256Service {
 public String hash(byte[] content){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));}catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-256 is unavailable",e);}}
 public boolean matches(String first,String second){return first!=null&&second!=null&&MessageDigest.isEqual(first.getBytes(java.nio.charset.StandardCharsets.US_ASCII),second.getBytes(java.nio.charset.StandardCharsets.US_ASCII));}
}
