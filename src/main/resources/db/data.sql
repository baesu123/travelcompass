-- 회원가입 시 자동으로 기본 체크리스트(여권/환전/유심/여행자보험)가 생성됩니다.
-- 아래는 이미 id=1 회원이 존재하는 로컬 테스트 환경을 위한 샘플 데이터이며, 없으면 아무 동작도 하지 않습니다.
INSERT INTO checklist (member_id, item_name, checked)
SELECT 1, '여권', TRUE
WHERE EXISTS (SELECT 1 FROM member WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM checklist WHERE member_id = 1 AND item_name = '여권');

INSERT INTO checklist (member_id, item_name, checked)
SELECT 1, '환전', FALSE
WHERE EXISTS (SELECT 1 FROM member WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM checklist WHERE member_id = 1 AND item_name = '환전');

INSERT INTO checklist (member_id, item_name, checked)
SELECT 1, '유심', TRUE
WHERE EXISTS (SELECT 1 FROM member WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM checklist WHERE member_id = 1 AND item_name = '유심');

INSERT INTO checklist (member_id, item_name, checked)
SELECT 1, '여행자보험', TRUE
WHERE EXISTS (SELECT 1 FROM member WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM checklist WHERE member_id = 1 AND item_name = '여행자보험');
