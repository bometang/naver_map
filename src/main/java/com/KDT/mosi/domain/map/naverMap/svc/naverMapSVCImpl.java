package com.KDT.mosi.domain.map.naverMap.svc;

import com.KDT.mosi.domain.entity.map.naverMap.AddressInfo;
import com.KDT.mosi.domain.entity.map.naverMap.LocalSearchRes;
import com.KDT.mosi.web.config.NaverProps;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class naverMapSVCImpl implements naverMapSVC{
  private final NaverProps props;
  private final RestTemplate rest = new RestTemplate();


  // 사용자 입력 주소값을 네이버 검색api를 통해  위도,경도로 변경
  /* ------------------------------------------------------------------
   *  키워드 → 지번주소 5건 반환 (네이버 지역검색 OpenAPI)
   * ------------------------------------------------------------------ */
  @Override
  public List<AddressInfo> fetchAddresses(String keyword, double lat, double lng) {
    log.info("fetchAddresses 호출 - keyword=[{}], center=[{},{}]",
        keyword, lat, lng);

    // 1) 키워드 URL-인코딩
    String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

    // 2) 요청 URL: x=경도, y=위도 순서로 넣습니다.
    String apiURL = props.getOpen().getBaseUrl()
        + "/v1/search/local.json"
        + "?query="   + encoded
        + "&display=5"
        + "&x="       + lng
        + "&y="       + lat;
    log.info("▶ Request URL: {}", apiURL);

    HttpURLConnection con = null;
    try {
      URL url = new URL(apiURL);
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("X-Naver-Client-Id",     props.getOpen().getClientId());
      con.setRequestProperty("X-Naver-Client-Secret", props.getOpen().getClientSecret());
      con.setRequestProperty("Accept", "application/json");

      int code = con.getResponseCode();
      InputStream stream = (code == HttpURLConnection.HTTP_OK)
          ? con.getInputStream()
          : con.getErrorStream();
      String responseBody = readBody(stream);

      // 5) JSON → DTO 매핑
      ObjectMapper mapper = new ObjectMapper();
      LocalSearchRes res = mapper.readValue(responseBody, LocalSearchRes.class);

      // 6) DTO → AddressInfo 변환
      List<AddressInfo> result = res.getItems().stream()
          .map(item -> new AddressInfo(
              item.getAddress(),
              item.getRoadAddress(),
              item.getMapx(),
              item.getMapy()
          ))
          .collect(Collectors.toList());

      log.info("▶ 최종 반환 AddressInfo 개수=[{}]", result.size());
      return result;

    } catch (IOException e) {
      log.error("HTTP 요청 실패", e);
      return Collections.emptyList();
    } finally {
      if (con != null) con.disconnect();
    }
  }

  // InputStream → String
  private String readBody(InputStream stream) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      return sb.toString();
    }
  }



  @Override
  public String geocode(String query) {
    String apiPath = "/map-geocode/v2/geocode?query=" + query;

    log.info("요청 URL: https://maps.apigw.ntruss.com{}", apiPath);
    log.info("AccessKey: {}", props.getGateway().getAccessKey());
    log.info("SecretKey: {}", props.getGateway().getSecretKey());

    HttpHeaders headers = new HttpHeaders();
    headers.add("X-NCP-APIGW-API-KEY-ID", props.getGateway().getAccessKey());
    headers.add("X-NCP-APIGW-API-KEY", props.getGateway().getSecretKey());
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    String url = "https://maps.apigw.ntruss.com" + apiPath;

    try {
      ResponseEntity<String> resp = rest.exchange(
          url,
          HttpMethod.GET,
          new HttpEntity<>(headers),
          String.class
      );
      log.info("응답: {}", resp);
      log.info("응답 상태 코드: {}", resp.getStatusCode());
      log.info("응답 본문: {}", resp.getBody());

      return resp.getBody();
    } catch (Exception e) {
      log.error("RestTemplate 요청 실패", e);
      return "요청 실패: " + e.getMessage();
    }
  }

  private String makeSignature(String method, String uri, String timestamp) {
    String message = new StringBuilder()
        .append(method).append(" ").append(uri).append("\n")
        .append(timestamp).append("\n")
        .append(props.getGateway().getAccessKey())
        .toString();

    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(
          props.getGateway().getSecretKey().getBytes(StandardCharsets.UTF_8),
          "HmacSHA256"
      ));
      byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hmacBytes);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("HmacSHA256 서명 생성에 실패했습니다.", e);
    }
  }
}
