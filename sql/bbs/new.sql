/*--------------------------------------------------------
                 테이블 전체 칼럼 가져오기
--------------------------------------------------------*/
SELECT * FROM member_role;
SELECT * FROM member_terms;
SELECT * FROM ROLE;
SELECT * FROM seller_page;

SELECT * FROM bbs;

SELECT * FROM bbs_like;

SELECT * FROM bbs_report;

SELECT * FROM RBBS;

SELECT * FROM rbbs_like;

SELECT * FROM rbbs_report;

SELECT * FROM MEMBER;

SELECT * FROM code;

SELECT * FROM bbs_upload;

SELECT * FROM MEMBER;
SELECT * FROM ORDERs;
SELECT * FROM ORDER_ITEMS;
SELECT * FROM product;
SELECT * FROM product_image;

SELECT * FROM review;
SELECT * FROM review_tag;
SELECT * FROM tag WHERE tcategory='B0101'OR commonyn='Y';

DELETE FROM review;
DELETE FROM review_tag;
UPDATE order_items
SET reviewed='N'
WHERE order_id=1;
UPDATE order_items
SET reviewed='N'
WHERE order_id=2;




SELECT REVIEW_ID, PRODUCT_ID, BUYER_ID, ORDER_ITEM_ID,CONTENT, SCORE, SELLER_RECOYN, STATUS, CREATE_DATE, UPDATE_DATE
FROM review
WHERE buyer_id=6
ORDER BY create_date DESC
  OFFSET (1 -1) * 10 ROWS
FETCH NEXT 10 ROWS ONLY ;



SELECT p.product_id AS product_id,category,title,p.create_DATE AS create_date,nickname,mime_Type,image_data
FROM product p
LEFT JOIN product_image i
  ON p.PRODUCT_ID = i.PRODUCT_ID
  AND i.image_order = (
         SELECT MIN(pi.image_order) FROM product_image pi WHERE pi.product_id = p.product_id
       )
LEFT JOIN MEMBER m ON p.MEMBER_ID = m.MEMBER_ID
WHERE p.product_ID = 1;

/*--------------------------------------------------------
          더미 데이터 제외 테이블 전체 칼럼 가져오기
--------------------------------------------------------*/
SELECT * FROM bbs WHERE bbs_id >102;

SELECT * FROM RBBS WHERE rbbs_id > 120;


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






DELETE FROM bbs_upload;

SELECT * FROM bbs_upload;



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




INSERT INTO bbs_upload(image_id,bbs_id,sort_order,file_path,original_name,saved_name)
VALUES (bbs_upload_upload_id_seq.NEXTVAL,:bbsId, :sortOrder, :filePath, :originalName, :savedName);


SELECT * FROM BBS;



insert into member_role values (1, 'R01'); -- 짱구 : 구매자
insert into member_role values (2, 'R02'); -- 형만 : 판매자
insert into member_role values (3, 'R01'); -- 봉미선 : 구매자
insert into member_role values (4, 'R01'); -- 짱아 : 구매자
insert into member_role values (5, 'R02'); -- 맹구 : 판매자

insert into role (role_id, role_name) values ('R01', '구매자');
insert into role (role_id, role_name) values ('R02', '판매자');







BEGIN
  -- 1) FK 제약조건 삭제
  FOR c IN (SELECT constraint_name, table_name
              FROM user_constraints
             WHERE constraint_type = 'R')
  LOOP
    EXECUTE IMMEDIATE 'ALTER TABLE ' || c.table_name || ' DROP CONSTRAINT ' || c.constraint_name;
  END LOOP;

  -- 2) 테이블 삭제
  FOR t IN (SELECT table_name FROM user_tables)
  LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
  END LOOP;

  -- 3) 시퀀스 삭제
  FOR s IN (SELECT sequence_name FROM user_sequences)
  LOOP
    EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
  END LOOP;
END;



