import { ajax } from '/js/community/common.js';

const map   = new naver.maps.Map('map', { center: new naver.maps.LatLng(37.5665,126.9780), zoom: 13 });
//const marker = new naver.maps.Marker({ map });
let   markers = [];

const keywordInput = document.getElementById('keyword-input');
const keywordBtn   = document.getElementById('keyword-btn');
let lastBounds = map.getBounds();
console.log(lastBounds);

// 1) 시작값 좌표
let lastCenter = {
  lat: map.getCenter().lat(),
  lng: map.getCenter().lng()
};

naver.maps.Event.addListener(map, 'idle', () => {
  lastBounds = map.getBounds();
  const center = map.getCenter();
  lastCenter = {
    lat: center.lat(),
    lng: center.lng()
  };
  console.log('최신 중심 좌표:', lastCenter);
  console.log(lastBounds);
});


keywordBtn.addEventListener('click', handleKeywordSearch);
keywordInput.addEventListener('keydown', e => {
  if (e.key === 'Enter') {
    handleKeywordSearch();
  }
});

//지도 경계의 위도,경도 추출
export function getVisibleBounds() {
  return {
    neLat: lastBounds.getNE().lat(),
    neLng: lastBounds.getNE().lng(),
    swLat: lastBounds.getSW().lat(),
    swLng: lastBounds.getSW().lng()
  };
}

// v1으로 키워드 입력시 가장 비슷한 장소 5개 출력
async function handleKeywordSearch() {
  const kw = keywordInput.value.trim();
  if (!kw) {
    alert('키워드를 입력하세요');
    return;
  }

  try {
    console.log('최신 중심 좌표:', lastCenter);
    const params = new URLSearchParams({
          query: kw,
          start: `${lastCenter.lat},${lastCenter.lng}`
        });
    const res = await ajax.get(`/map/dev?${params.toString()}`);
    clearMarkers();

    const visible = res.body.filter(item => {
      // ★ 서버가 내려주는 모든 후보 필드명을 순서대로 fallback
      const lat = parseFloat(item.mapy ?? item.y   ?? item.lat);
      const lng = parseFloat(item.mapx ?? item.x   ?? item.lng);

      // 숫자 변환 실패(NaN)일 때는 제외
      if (Number.isNaN(lat) || Number.isNaN(lng)) return false;

      return lastBounds.hasLatLng(new naver.maps.LatLng(lat, lng));
    });

    visible.forEach(item => {
      const lat = parseFloat(item.mapy ?? item.y ?? item.lat);
      const lng = parseFloat(item.mapx ?? item.x ?? item.lng);

      const m = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map,
        title: item.roadAddress ?? item.roadAddressName
               ?? item.jibunAddress ?? item.addressName
      });
      markers.push(m);
    });

    const box = document.getElementById('result-list');
    box.innerHTML = '';

    // 불필요한 const li 삭제, forEach 닫는 괄호/세미콜론 추가
    res.body.forEach(item => {
      const li = document.createElement('li');
      li.textContent = JSON.stringify(item, null, 2);
      if (visible.includes(item)) {
          li.style.color = 'red';
        }
      box.appendChild(li);
    });

    if (!visible.length) alert('현재 화면 안에 표시할 결과가 없습니다.');
  } catch (err) {
    console.error(err);
    alert('요청 실패');
  }
}

function clearMarkers() {
  markers.forEach(m => m.setMap(null));
  markers.length = 0;
}