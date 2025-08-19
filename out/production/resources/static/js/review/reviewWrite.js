import { ajax } from '/js/community/common.js';

const product_category = document.querySelector('.summary')?.getAttribute('data-category') ?? null;

try {
  const category = product_category;
  if (category == null) return;
  const resCat = await ajax.get('/api/review/tag/{category}');
  if (resCat.header.rtcd === 'S00' && Array.isArray(resCat.body)) {

  }
} catch (e) {
  console.error('태그 로드 실패', e);
}


