package com.KDT.mosi.domain.naverMap.svc;

import com.KDT.mosi.web.config.naverMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class naverMapSVCImpl implements naverMapSVC{
  private final naverMap props;
  private final RestTemplate rest = new RestTemplate();

  /** GET 방식으로 geocode 호출 */
  @Override
  public String geocode(String query){
    String apiPath   = "/map-geocode/v2/geocode?query=" +
        URLEncoder.encode(query, StandardCharsets.UTF_8);
    String timestamp = String.valueOf(Instant.now().toEpochMilli());
    String signature = makeSignature("GET", apiPath, timestamp);

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-NCP-APIGW-API-KEY-ID",     props.getRest().getAccessKey());
    headers.add("X-NCP-APIGW-TIMESTAMP",      timestamp);
    headers.add("X-NCP-APIGW-SIGNATURE-V2",   signature);

    String url = props.getRest().getBaseUrl() + apiPath;
    ResponseEntity<String> resp = rest.exchange(url, HttpMethod.GET,
        new HttpEntity<>(headers), String.class);
    return resp.getBody();
  }

  private String makeSignature(String method, String uri, String timestamp) {
    String message = new StringBuilder()
        .append(method).append(" ").append(uri).append("\n")
        .append(timestamp).append("\n")
        .append(props.getRest().getAccessKey())
        .toString();

    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(
          props.getRest().getSecretKey().getBytes(StandardCharsets.UTF_8),
          "HmacSHA256"
      ));
      byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hmacBytes);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("HmacSHA256 서명 생성에 실패했습니다.", e);
    }
  }
}
