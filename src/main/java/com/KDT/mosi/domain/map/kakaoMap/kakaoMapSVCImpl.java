package com.KDT.mosi.domain.map.kakaoMap;

import com.KDT.mosi.domain.entity.map.kakaoMap.KakaoLocalSearchRes;
import com.KDT.mosi.domain.entity.map.naverMap.AddressInfo;
import com.KDT.mosi.web.config.KakaoProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class kakaoMapSVCImpl implements kakaoMapSVC {
  private final KakaoProps props;
  private final RestTemplate rest = new RestTemplate();

  @Override
  public List<AddressInfo> fetchAddresses(String keyword, double lat, double lng) {
    try {
      URI uri = UriComponentsBuilder
          .fromHttpUrl(props.getBaseUrl() + "/v2/local/search/keyword.json")
          .queryParam("query", keyword)
          .queryParam("y", lat)
          .queryParam("x", lng)
          //.queryParam("radius", 20000)
          .queryParam("size", 15)
          .queryParam("sort", "distance")
          .build()                              // build() → 아직 미인코딩
          .encode(StandardCharsets.UTF_8)       // ★ 여기서 단 한 번만 인코딩
          .toUri();                             // URI 객체로 반환

      HttpHeaders headers = new HttpHeaders();
      headers.set("Authorization", "KakaoAK " + props.getRestKey());
      headers.setAccept(List.of(MediaType.APPLICATION_JSON));
      HttpEntity<Void> entity = new HttpEntity<>(null, headers);

      KakaoLocalSearchRes kr = rest.exchange(uri, HttpMethod.GET, entity,
          KakaoLocalSearchRes.class).getBody();

      if (kr == null || kr.getDocuments().isEmpty()) {
        log.warn("Kakao에 검색 결과가 없습니다.");
        return Collections.emptyList();
      }

      return kr.getDocuments().stream()
          .map(d -> new AddressInfo(
              d.getAddressName(),
              d.getRoadAddressName(),
              d.getX(),   // 경도
              d.getY()    // 위도
          ))
          .collect(Collectors.toList());


    } catch (Exception e) {
      log.error("Kakao REST API 호출 실패", e);
      return Collections.emptyList();
    }
  }








}
