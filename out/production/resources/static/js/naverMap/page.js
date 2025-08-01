import { ajax } from '/js/community/common.js';

const map   = new naver.maps.Map('map', { center: new naver.maps.LatLng(37.5665,126.9780), zoom: 13 });
const marker = new naver.maps.Marker({ map });


const keywordInput = document.getElementById('keyword-input');
const keywordBtn   = document.getElementById('keyword-btn');

keywordBtn.addEventListener('click', handleKeywordSearch);
keywordInput.addEventListener('keydown', e => {
  if (e.key === 'Enter') {
    handleKeywordSearch();
  }
});


async function handleKeywordSearch() {
  const kw = keywordInput.value.trim();
  if (!kw) {
    alert('키워드를 입력하세요');
    return;
  }

  try {
    const res = await ajax.get(`/map/dev?query=${kw}`);
    console.log(res);
    /*
    const box = document.getElementById('result-list');
    box.innerHTML = '';

    // 불필요한 const li 삭제, forEach 닫는 괄호/세미콜론 추가
    res.body.forEach(item => {
      const li = document.createElement('li');
      li.textContent = item;
      box.appendChild(li);
    });
    */
  } catch (err) {
    console.error(err);
    alert('요청 실패');
  }
}