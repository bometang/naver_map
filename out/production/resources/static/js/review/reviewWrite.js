import { ajax } from '/js/community/common.js';

const product_category = document.querySelector('.summary')?.getAttribute('data-category') ?? null;
const tagListEl = document.querySelector('#reviewTags .tag-list');
const selectedTagsInput = document.getElementById('selectedTags');

try {
  if (product_category) {
    const resCat = await ajax.get(`/api/review/tag/${product_category}`);
    if (resCat.header?.rtcd === 'S00' && Array.isArray(resCat.body)) {
      tagListEl.innerHTML = resCat.body
        .map(tag => `<button type="button" class="tag" data-id="${tag.tagId}">${tag.label}</button>`)
        .join('');
    }
  }
} catch (e) {
  console.error('태그 로드 실패', e);
}

// 클릭 이벤트: 여러개 선택 가능
tagListEl?.addEventListener('click', e => {
  if (!e.target.classList.contains('tag')) return;
  e.target.classList.toggle('selected');

  const selected = [...tagListEl.querySelectorAll('.tag.selected')].map(el => el.dataset.id);
  selectedTagsInput.value = selected.join(',');
});