// /js/review/rating-precise.js
(() => {
  const root   = document.getElementById('ratingPrecise');
  if (!root) return;

  const stars  = root.querySelector('.stars');
  const input  = root.querySelector('#score');
  const output = root.querySelector('#scoreText');

  const MAX  = 5;
  const STEP = 0.1;

  // ✔ 바깥 클릭 허용 범위(px) — 이 영역에서 클릭하면 5.0
  const RIGHT_OVERSHOOT_PX = 24;

  let selected = Number.parseFloat(input?.value) || 0;

  const clamp = (v, min, max) => Math.min(max, Math.max(min, v));

  const setVisual = (val) => {
    const pct = clamp((val / MAX) * 100, 0, 100);
    stars.style.setProperty('--fill', `${pct}%`);
    output.value = val.toFixed(1);
  };

  const snap = (raw) => {
    const snapped = Math.round(raw / STEP) * STEP;
    const fixed = STEP < 1 ? 1 : 0;
    return Number(clamp(snapped, 0, MAX).toFixed(fixed));
  };

  // 호버/미리보기: 별 영역 "내부"만 사용
  const posToValueInside = (clientX) => {
    const rect = stars.getBoundingClientRect();
    const x = clamp(clientX - rect.left, 0, rect.width);
    const ratio = rect.width ? x / rect.width : 0;
    return snap(ratio * MAX);
  };

  // 클릭 확정: 오른쪽 바깥 클릭을 5.0으로 인정
  const posToValueWithOvershoot = (clientX) => {
    const rect = stars.getBoundingClientRect();
    if (clientX > rect.right && clientX <= rect.right + RIGHT_OVERSHOOT_PX) {
      return MAX;
    }
    return posToValueInside(clientX);
  };

  // 초기 표시
  setVisual(selected);

  // 이벤트: 루트에서 받아 별 오른쪽 여백 클릭도 처리
  root.addEventListener('mousemove', (e) => setVisual(posToValueInside(e.clientX)));
  root.addEventListener('mouseleave', () => setVisual(selected));
  root.addEventListener('click', (e) => {
    selected = posToValueWithOvershoot(e.clientX);
    input.value = selected.toFixed(1);
    setVisual(selected);
  });

  // 터치: 미리보기는 내부만, 확정은 바깥 클릭 허용
  root.addEventListener('touchmove', (e) => {
    const t = e.touches?.[0];
    if (t) setVisual(posToValueInside(t.clientX));
  }, { passive: true });

  root.addEventListener('touchend', (e) => {
    const t = e.changedTouches?.[0];
    if (!t) return;
    selected = posToValueWithOvershoot(t.clientX);
    input.value = selected.toFixed(1);
    setVisual(selected);
  });
})();
