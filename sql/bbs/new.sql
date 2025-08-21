/*--------------------------------------------------------
                 테이블 전체 칼럼 가져오기
--------------------------------------------------------*/
SELECT * from MEMBER;

SELECT * FROM ROLE;
SELECT * FROM MEMBER_ROLE mr ;
SELECT * FROM TERMS t ;
SELECT * FROM BUYER_PAGE bp ;
SELECT * FROM SELLER_PAGE sp ;

SELECT * FROM bbs;

SELECT * FROM bbs_like;

SELECT * FROM bbs_report;

SELECT * FROM RBBS;

SELECT * FROM rbbs_like;

SELECT * FROM rbbs_report;

SELECT * FROM BBS_UPLOAD ORDER BY UPLOADED_AT;

SELECT * FROM PRODUCT;
SELECT * FROM PRODUCT_image;

SELECT * FROM product WHERE product_id = 1;

SELECT * FROM orders;
SELECT * FROM order_items;

DELETE FROM REVIEW;
DELETE from REVIEW_TAG;
UPDATE order_items
SET reviewed='N'
WHERE order_item_id =1;

SELECT * FROM review;
SELECT * FROM tag;
SELECT * FROM review_tag;

SELECT count(review_id)
FROM review r
LEFT JOIN product p
ON p.product_id=r.PRODUCT_ID
WHERE p.member_id =6;



SELECT buyer_id,product_id,option_type,REVIEWED
FROM order_items i
JOIN orders o
ON i.order_id = o.ORDER_ID
WHERE i.order_item_id=1;


SELECT tag_id, label, slug
FROM tag
WHERE useyn = 'Y'
  AND (commonyn = 'Y' OR tcategory = 'B0101')
ORDER BY
  DECODE(commonyn, 'Y', 0, 1),
  DECODE(recoyn,  'Y', 0, 1),
  tag_id;

SELECT p.product_id AS product_id,p.category AS category,p.title AS title,p.create_date AS create_date,sp.nickname AS nickname,i.mime_type AS MIME_TYPE,i.image_data AS image_data
FROM product p
LEFT JOIN product_image i
  ON i.product_id = p.product_id
 AND i.image_order = (
       SELECT MIN(pi.image_order)
       FROM product_image pi
       WHERE pi.product_id = p.product_id
     )
LEFT JOIN seller_page sp
  ON sp.member_id = p.member_id
WHERE p.product_id = 1;

SELECT * FROM product;
SELECT * FROM review;

SELECT REVIEW_ID,CONTENT, SCORE, SELLER_RECOYN,r.CREATE_DATE AS rcreate, r.UPDATE_DATE AS rupdate,
       p.TITLE AS title, p.CREATE_DATE AS pcreate, p.UPDATE_DATE AS pupdate,
       oi.option_type AS option_type
FROM review r
LEFT JOIN product p
ON p.product_id = r.product_id
LEFT JOIN ORDER_ITEMS oi
ON oi.ORDER_ITEM_ID = r.ORDER_ITEM_ID
WHERE buyer_id= 5
ORDER BY r.create_date DESC
  OFFSET (1 -1) * 5 ROWS
  FETCH NEXT 5 ROWS ONLY;


  SELECT
    rt.review_id,
    LISTAGG(t.tag_id, ',')      WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_ids,
    LISTAGG(t.label, ' | ')     WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_labels
  FROM review_tag rt
  JOIN tag t ON t.tag_id = rt.tag_id
  GROUP BY rt.review_id;



SELECT
  r.review_id,r.content,r.score,r.seller_recoyn,r.create_date AS rcreate,r.update_date AS rupdate,
  p.title AS title,p.create_date AS pcreate,p.update_date AS pupdate,
  oi.option_type AS option_type,
  tags.tag_ids AS tag_ids,tags.tag_labels AS tag_labels
FROM review r
LEFT JOIN product p
  ON p.product_id = r.product_id
LEFT JOIN order_items oi
  ON oi.order_item_id = r.order_item_id
LEFT JOIN (
  SELECT
    rt.review_id,
    LISTAGG(t.tag_id, ',')      WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_ids,
    LISTAGG(t.label, ' | ')     WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_labels
  FROM review_tag rt
  JOIN tag t ON t.tag_id = rt.tag_id
  GROUP BY rt.review_id
) tags
  ON tags.review_id = r.review_id
WHERE p.member_id = 6
ORDER BY r.create_date DESC
OFFSET (1 - 1) * 5 ROWS
FETCH NEXT 5 ROWS ONLY;


SELECT
  r.review_id                              AS review_id,
  r.content                                 AS content,
  r.score                                   AS score,
  r.seller_recoyn                           AS seller_reco_yn,
  r.create_date                             AS rcreate,
  r.update_date                             AS rupdate,
  p.title                                   AS title,
  p.create_date                             AS pcreate,
  p.update_date                             AS pupdate,
  oi.option_type                            AS option_type,
  tags.tag_ids                              AS tag_ids,
  tags.tag_labels                           AS tag_labels,
  i.image_id                                AS product_image_id,
  i.mime_type                               AS product_image_mime,
  m.NICKNAME 																AS nickname
FROM review r
JOIN product p ON p.product_id = r.product_id
LEFT JOIN order_items oi ON oi.order_item_id = r.order_item_id
LEFT JOIN member m ON m.member_id = r.buyer_id
LEFT JOIN (
  SELECT rt.review_id,
         LISTAGG(t.tag_id, ',')  WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_ids,
         LISTAGG(t.label, ' | ') WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_labels
  FROM review_tag rt JOIN tag t ON t.tag_id = rt.tag_id
  GROUP BY rt.review_id
) tags ON tags.review_id = r.review_id
LEFT JOIN (
  SELECT product_id, image_id, mime_type
  FROM (
    SELECT pi.*, ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY image_order, image_id) rn
    FROM product_image pi
  ) WHERE rn = 1 ) i ON i.product_id = r.product_id
WHERE p.member_id = 6
ORDER BY r.create_date DESC
OFFSET (1 - 1) * 5 ROWS
FETCH NEXT 5 ROWS ONLY;


SELECT
r.review_id,r.content,r.score,r.seller_recoyn AS seller_reco_yn,r.create_date AS rcreate,r.update_date AS rupdate,
p.title AS title,p.create_date AS pcreate,p.update_date AS pupdate,
oi.option_type AS option_type,
tags.tag_ids,tags.tag_labels,
i.image_id AS product_image_id,i.mime_type AS product_image_mime,
m.NICKNAME AS nickname
FROM review r
LEFT JOIN product p
ON p.product_id = r.product_id
LEFT JOIN order_items oi
ON oi.order_item_id = r.order_item_id
LEFT JOIN member m ON m.member_id = r.buyer_id
LEFT JOIN (
SELECT
rt.review_id,
LISTAGG(t.tag_id, ',')      WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_ids,
LISTAGG(t.label, ' | ')     WITHIN GROUP (ORDER BY rt.sort_order, t.tag_id) AS tag_labels
FROM review_tag rt
JOIN tag t ON t.tag_id = rt.tag_id
GROUP BY rt.review_id
) tags
ON tags.review_id = r.review_id
LEFT JOIN (
SELECT product_id, image_id, mime_type
FROM (
SELECT pi.*, ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY image_order, image_id) rn
FROM product_image pi
) WHERE rn = 1 ) i ON i.product_id = r.product_id
WHERE p.member_id = 6
ORDER BY r.create_date DESC
OFFSET (1 - 1) * 5 ROWS
FETCH NEXT 5 ROWS ONLY ;
/*--------------------------------------------------------
          더미 데이터 제외 테이블 전체 칼럼 가져오기
--------------------------------------------------------*/
SELECT * FROM bbs WHERE bbs_id >102;

SELECT * FROM RBBS WHERE rbbs_id > 120;

SELECT * FROM rbbs ORDER BY UPDATE_DATE desc;
/*--------------------------------------------------------
           특정 조건 bbs 테이블 전체 칼럼 가져오기
--------------------------------------------------------*/
SELECT * FROM bbs WHERE bbs_id =97;

SELECT * FROM bbs WHERE member_id = 1;

SELECT * FROM bbs WHERE member_id = 1 AND status = 'B0203';

SELECT * FROM bbs WHERE bgroup =103 ORDER BY step;


/*--------------------------------------------------------
            좋아요 신고 테이블 전체 칼럼 가져오기
--------------------------------------------------------*/
SELECT * FROM bbs_like ORDER BY create_date;

SELECT * FROM bbs_report  ORDER BY report_date;

SELECT * FROM rbbs_like ORDER BY create_date;

SELECT * FROM rbbs_report  ORDER BY report_date;


/*--------------------------------------------------------
                 게시글 전체 목록 가져오기
--------------------------------------------------------*/
SELECT
  b.bbs_id as bbs_id,
  b.bcategory as bcategory,
  CASE
  	WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.'
  	ELSE b.title
  END AS title,
  NVL(m.member_id, 0) AS member_id,
  b.create_date AS create_date,
  b.update_date as update_date,
  b.bindent
FROM bbs b
LEFT JOIN member m
  ON b.member_id = m.member_id
ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC;


/*--------------------------------------------------------
          특정 카테고리 게시글 전체 목록 가져오기
--------------------------------------------------------*/
SELECT
  b.bbs_id as bbs_id,
  b.bcategory as bcategory,
  b.pbbs_id AS pbbs_id,
  b.step AS step,
  b.bgroup AS bgroup,
  CASE
  	WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.'
  	ELSE b.title
  END AS title,
  NVL(m.member_id, 0) AS member_id,
  b.create_date AS create_date,
  b.update_date as update_date,
  b.bindent
FROM bbs b
LEFT JOIN member m
  ON b.member_id = m.member_id
  WHERE bcategory = 'B0102'
ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC;


/*--------------------------------------------------------
          특정 카테고리 게시글 1페이지 목록 가져오기
--------------------------------------------------------*/
SELECT
  b.bbs_id as bbs_id,
  b.bcategory as bcategory,
  b.pbbs_id AS pbbs_id,
  b.step AS step,
  b.bgroup AS bgroup,
  CASE
  	WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.'
  	ELSE b.title
  END AS title,
  NVL(m.member_id, 0) AS member_id,
  m.nickname AS nickname,
  b.create_date AS create_date,
  b.update_date as update_date,
  b.bindent
FROM bbs b
LEFT JOIN member m
  ON b.member_id = m.member_id
  WHERE bcategory = 'B0102'
ORDER BY b.bgroup DESC, b.step ASC, b.bbs_id ASC
OFFSET (1 -1) * 10 ROWS
FETCH NEXT 10 ROWS only ;


/*--------------------------------------------------------
                     게시글 가져오기
--------------------------------------------------------*/
SELECT
b.bbs_id as bbs_id,
b.bcategory as bcategory,
CASE
WHEN b.status = 'B0202' THEN '삭제된 게시글입니다.'
ELSE b.title
END AS title,
NVL(m.member_id, 0) AS member_id,
b.hit AS hit,
CASE
WHEN b.status = 'B0202'
THEN to_clob('삭제된 게시글입니다.')
ELSE b.bcontent
END AS bcontent,
b.pbbs_id AS pbbs_id,
b.bgroup AS bgroup,
b.step AS step,
b.bindent AS bindent,
b.create_date AS create_date,
b.update_date as update_date
FROM bbs b
LEFT JOIN member m
ON b.member_id = m.member_id
WHERE bbs_id = 98;
--------------------------------------------------------




SELECT
b.bbs_id as bbs_id,
    b.bcategory as bcategory,
    b.status as status,
    b.title as title,
    NVL(m.member_id, 0) AS member_id,
    b.hit AS hit,
    b.bcontent as bcontent,
    b.pbbs_id AS pbbs_id,
    b.bgroup AS bgroup,
    b.step AS step,
    b.bindent AS bindent,
    b.create_date AS create_date,
    b.update_date as update_date
    FROM bbs b
    LEFT JOIN member m
    ON b.member_id = m.member_id
    where b.member_id = 8
    and b.status = 'B0203'
    and NVL(b.pbbs_id, 0) = NVL(null, 0);





SELECT
  b.bbs_id      AS bbs_id,
  b.bcategory   AS bcategory,
  b.status      AS status,
  b.title       AS title,
  NVL(m.member_id, 0) AS member_id,
  b.hit         AS hit,
  b.bcontent    AS bcontent,
  b.pbbs_id     AS pbbs_id,
  b.bgroup      AS bgroup,
  b.step        AS step,
  b.bindent     AS bindent,
  b.create_date AS create_date,
  b.update_date AS update_date
FROM bbs b
LEFT JOIN member m
  ON b.member_id = m.member_id
WHERE b.member_id = 8
  AND b.status    = 'B0203'
  AND NVL(b.pbbs_id, 0) = NVL(null,   0);


SELECT * FROM bbs ORDER BY bbs_id;


SELECT * FROM bbs_upload
ORDER BY upload_id;
DELETE FROM bbs_upload;

SELECT * FROM bbs WHERE status='B0203';
DELETE FROM bbs WHERE bgroup;
SELECT * FROM bbs_upload
WHERE file_type = 'ATTACHMENT'
AND bbs_id = 135;
ORDER BY sort_order;

--
--게시글60
-- |-게시글60의 답글1
--  |-게시글60의 답글1의 대답글1
--  |-게시글60의 답글1의 대답글2
-- |-게시글60의 답글2
-- |-게시글60의 답글3
--게시글59
--게시글58
--게시글57
--게시글56
--게시글55
--게시글54





SELECT *
  FROM bbs_upload
 WHERE bbs_id   = 119
   AND file_type IN ('INLINE', 'ATTACHMENT')
   AND (
       LOWER(saved_name) LIKE '%.png'
		OR LOWER(saved_name) LIKE '%.jpg'
		OR LOWER(saved_name) LIKE '%.jpeg'
		OR LOWER(saved_name) LIKE '%.gif'
		)
 ORDER BY sort_order
 FETCH FIRST 1 ROWS ONLY;



SELECT * FROM bbs_report;
SELECT * FROM bbs_like;
SELECT * FROM rbbs ORDER BY bgroup;
SELECT COUNT(rbbs_id) FROM rbbs WHERE bbs_id = :bbsId AND status <> 'R0203';
SELECT count(bbs_id) FROM bbs_like WHERE bbs_id=110 AND member_id=1;


INSERT INTO bbs_report (BBS_ID,MEMBER_ID,reason)
VALUES (149,1,'그냥');

SELECT * FROM bbs_like;

INSERT INTO bbs_like (BBS_ID,MEMBER_ID)
VALUES (149,1);

INSERT INTO rbbs (rbbs_id, bbs_id, member_id, status, bcontent, prbbs_id, bgroup, step, bindent)
VALUES (rbbs_rbbs_id_seq.NEXTVAL, 149, 1, 'R0201', '날씨가 좋네요',0, rbbs_rbbs_id_seq.CURRVAL, 0, 0);






-- 디렉터리 생성 권한 부여 (시스템 권한)
GRANT CREATE ANY DIRECTORY TO your_schema;
-- 디렉터리 객체 생성
CREATE OR REPLACE DIRECTORY DOWNLOADS_DIR AS 'C:\Users\user\Downloads';
-- your_schema(또는 MEMBER 스키마)에게 읽기 권한 부여
GRANT READ ON DIRECTORY DOWNLOADS_DIR TO your_schema;


SELECT USER FROM DUAL;


DECLARE
  lob_loc   BLOB;
  bfile_loc BFILE;
BEGIN
  UPDATE member
     SET pic = EMPTY_BLOB()
   WHERE member_id = 1
  RETURNING pic INTO lob_loc;

  bfile_loc := BFILENAME('DOWNLOADS_DIR', '인물1.jpg');
  DBMS_LOB.FILEOPEN(bfile_loc, DBMS_LOB.FILE_READONLY);

  DBMS_LOB.LOADFROMFILE(
    dest_lob    => lob_loc,
    src_bfile   => bfile_loc,
    amount      => DBMS_LOB.GETLENGTH(bfile_loc),
    dest_offset => 1,
    src_offset  => 1
  );

  DBMS_LOB.FILECLOSE(bfile_loc);
  COMMIT;
END;
/


DECLARE
  lob_loc   BLOB;
  bfile_loc BFILE;
BEGIN
  -- 1) BLOB 칼럼 초기화 및 LOB 핸들 확보
  UPDATE member
     SET pic = EMPTY_BLOB()
   WHERE member_id = 1
  RETURNING pic INTO lob_loc;

  -- 2) DIRECTORY 객체와 파일명 지정
  bfile_loc := BFILENAME('DOWNLOADS_DIR', '인물1.jpg');
  DBMS_LOB.FILEOPEN(bfile_loc, DBMS_LOB.FILE_READONLY);

  -- 3) 3-인자 버전 호출
  DBMS_LOB.LOADFROMFILE(
    lob_loc,
    bfile_loc,
    DBMS_LOB.GETLENGTH(bfile_loc)
  );

  -- 4) 자원 해제 및 커밋
  DBMS_LOB.FILECLOSE(bfile_loc);
  COMMIT;
END;
/



SELECT directory_name, directory_path
  FROM all_directories
 WHERE directory_name = 'DOWNLOADS_DIR';




SET SERVEROUTPUT ON
-- (여긴 세미콜론不要)
DECLARE
  bf          BFILE;
  file_exists INTEGER;
BEGIN
  bf := BFILENAME('DOWNLOADS_DIR','인물1.jpg');
  file_exists := DBMS_LOB.FILEEXISTS(bf);
  DBMS_OUTPUT.PUT_LINE('FILEEXISTS => '||file_exists);
END;
/




SELECT DBMS_LOB.FILEEXISTS(
         BFILENAME('DOWNLOADS','base64.txt')
       ) AS FILE_EXISTS
  FROM DUAL;




file:///C:/Users/user/Downloads/%EC%9D%B8%EB%AC%BC8.png




SELECT instance_name, host_name
  FROM v$instance;

SELECT directory_name, directory_path
  FROM all_directories
 WHERE directory_name = 'DOWNLOADS';


SELECT * FROM MEMBER;

SELECT * FROM role;

SELECT count(*) FROM rbbs_like WHERE rbbs_id = 100 AND member_id=1;


SELECT UPLOAD_ID  FROM BBS_UPLOAD WHERE upload_group = 12 AND bbs_id IS NULL;
SELECT *  FROM BBS_UPLOAD WHERE upload_group = 12;

SELECT * FROM BBS_UPLOAD WHERE upload_group = 12 AND bbs_id = ;
CREATE OR REPLACE DIRECTORY IMG_DIR AS 'C:\KDT\projects\naver_map\src\main\resources\static\img\member';

CREATE TABLE MEMBER_PIC_MAP (
  member_id  NUMBER PRIMARY KEY,
  file_name  VARCHAR2(100)
);

INSERT ALL
  INTO MEMBER_PIC_MAP VALUES (1, '1_인물.jpg')
  INTO MEMBER_PIC_MAP VALUES (2, '2_인물.png')
  INTO MEMBER_PIC_MAP VALUES (3, '3_인물.png')
  INTO MEMBER_PIC_MAP VALUES (4, '4_인물.jpg')
  INTO MEMBER_PIC_MAP VALUES (5, '5_인물.jpg')
  INTO MEMBER_PIC_MAP VALUES (6, '6_인물.jpg')
  INTO MEMBER_PIC_MAP VALUES (7, '7_인물.jpg')
  INTO MEMBER_PIC_MAP VALUES (8, '8_인물.png')
  INTO MEMBER_PIC_MAP VALUES (9, '9_인물.jpg')
SELECT 1 FROM dual;
COMMIT;



DECLARE
  v_bfile  BFILE;
  v_blob   BLOB;
BEGIN
  FOR rec IN (SELECT m.member_id, m.file_name
                FROM MEMBER_PIC_MAP m) LOOP

    UPDATE MEMBER
       SET PIC = EMPTY_BLOB()
     WHERE MEMBER_ID = rec.member_id
   RETURNING PIC INTO v_blob;

    v_bfile := BFILENAME('IMG_DIR', rec.file_name);
    DBMS_LOB.OPEN(v_bfile, DBMS_LOB.LOB_READONLY);
    DBMS_LOB.LOADFROMFILE(v_blob,
                          v_bfile,
                          DBMS_LOB.GETLENGTH(v_bfile));
    DBMS_LOB.CLOSE(v_bfile);
  END LOOP;

  COMMIT;
END;


SELECT m.member_id,
       m.file_name,
       DBMS_LOB.FILEEXISTS(BFILENAME('IMG_DIR', m.file_name)) AS exists_flag
FROM   MEMBER_PIC_MAP m;


SELECT count(bbs_id)
FROM bbs
WHERE member_id = 1
AND STATUS = 'B0202';

SELECT *
FROM bbs
WHERE MEMBER_ID =1;

SELECT table_name
FROM user_tables;


SELECT buyer_id,product_id,option_type,REVIEWED
FROM ORDER_ITEMS i
JOIN orders o
ON i.order_id = o.order_id
WHERE i.order_item_id=1;

SELECT p.product_id AS product_id,category,title,p.create_DATE AS create_date,nickname,mime_Type,image_data
FROM product p
LEFT JOIN product_image i
ON p.PRODUCT_ID = i.PRODUCT_ID
AND i.image_order = (
SELECT MIN(pi.image_order) FROM product_image pi WHERE pi.product_id = p.product_id
)
LEFT JOIN MEMBER m ON p.MEMBER_ID = m.MEMBER_ID
WHERE p.product_ID = 1;

SELECT
  s.sid,
  s.serial#,
  s.username,
  s.status,
  s.osuser,
  s.machine,
  s.program,
  q.sql_text
FROM v$session s
JOIN v$sqlarea q
  ON s.sql_id = q.sql_id
WHERE s.status = 'ACTIVE'                               -- 실행 중인 세션만
  AND INSTR(UPPER(q.sql_text), 'BBS') > 0;

BEGIN
  FOR c IN (
    SELECT table_name, constraint_name
      FROM user_constraints
     WHERE constraint_type = 'R'
  ) LOOP
    EXECUTE IMMEDIATE
      'ALTER TABLE "'||c.table_name||'" DROP CONSTRAINT "'||c.constraint_name||'"';
  END LOOP;
END;



BEGIN
  -- 2‑a) 테이블 삭제 (CASCADE CONSTRAINTS로 잔여 제약도 함께 제거)
  FOR t IN (
    SELECT table_name
      FROM user_tables
  ) LOOP
    EXECUTE IMMEDIATE
      'DROP TABLE "'||t.table_name||'" CASCADE CONSTRAINTS PURGE';
  END LOOP;

  -- 2‑b) 시퀀스 삭제
  FOR s IN (
    SELECT sequence_name
      FROM user_sequences
  ) LOOP
    EXECUTE IMMEDIATE
      'DROP SEQUENCE "'||s.sequence_name||'"';
  END LOOP;
END;
