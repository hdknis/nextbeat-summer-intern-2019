-- 組織定義 (sample)
--------------
CREATE TABLE "organization" (
  "id"             INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "location_id"    VARCHAR(8)   NOT NULL,
  "name_kanji"     VARCHAR(255) NOT NULL,
  "name_hurigana"  VARCHAR(255) NOT NULL,
  "name_en"        VARCHAR(255) NOT NULL,
  "address"        VARCHAR(255) NOT NULL,
  "updated_at"     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  "created_at"     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


INSERT INTO "organization" ("location_id", "name_kanji", "name_hurigana", "name_en", "address") VALUES ('01100', '札幌市役所','さっぽろしやくしょ' , 'SapporoCityHall' , '札幌市中央区北1条西2丁目');
INSERT INTO "organization" ("location_id", "name_kanji", "name_hurigana", "name_en", "address") VALUES ('47211', '沖縄市役所','おきなわしやくしょ' , 'OkinawaCityHall' , '沖縄県沖縄市仲宗根町２６−１');
