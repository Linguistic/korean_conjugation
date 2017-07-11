(ns korean.hangeul-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [cljs.test]
            [korean.hangeul :refer [is-hangeul? lead vowel padchim
                                    join find-vowel-to-append hangeul-match]]))

(deftest test-is-hangeul? []
  (testing "is-hangeul?"
    (is (= true (is-hangeul? "안")))
    (is (= true (is-hangeul? "안녕")))
    (is (= false (is-hangeul? "안peace")))
    (is (= true (is-hangeul? "안 녕")))
    (is (= true (is-hangeul? "안녕!")))
    (is (= false (is-hangeul? "die")))))

(deftest test-lead []
  (testing "lead"
    (is (= (lead "가") "ᄀ"))
    (is (= (lead "만") "ᄆ"))
    (is (= (lead "짉") "ᄌ"))))

(deftest test-vowel []
  (testing "vowel"
    (is (= (vowel "갓") "ㅏ"))
    (is (= (vowel "빩") "ㅏ"))
    (is (= (vowel "법") "ㅓ"))
    (is (= (vowel "가") "ㅏ"))))

(deftest test-padchim []
  (testing "padchim"
    (is (= (padchim "강") "ᆼ"))
    (is (= (padchim "댄") "ᆫ"))
    (is (= (padchim "아") nil))))

(deftest test-join []
  (testing "join"
    (is (= (join "ᄀ" "ㅏ" nil) "가"))
    (is (= (join "ᄆ" "ㅕ" "ᆫ") "면"))
    (is (= (join "ᄈ" "ㅙ" "ᆶ") "뾇"))))

(deftest test-find-vowel-to-append []
  (testing "test-find-vowel-to-append"
    (is (= (find-vowel-to-append "아프") "아"))
    (is (= (find-vowel-to-append "흐르") "어"))
    (is (= (find-vowel-to-append "태우") "어"))
    (is (= (find-vowel-to-append "만들") "어"))
    (is (= (find-vowel-to-append "앗") "아"))))

(deftest test-hangeul-match []
  (testing "hangeul-match"
    (is (= true (hangeul-match "아" "*" "ㅏ" nil)))
    (is (= false (hangeul-match "앉" "*" "ㅏ" nil)))
    (is (= true (hangeul-match "아" "ᄋ" "ㅏ" nil)))
    (is (= true (hangeul-match "읽" "*" "*" "ᆰ")))
    (is (= false (hangeul-match "읽" "*" "*" nil)))))

;geulja = new hangeul.Geulja('나');
;geulja.hidden_padchim = true;
;assert.equal(hangeul.padchim(geulja), true);;

;geulja = new hangeul.Geulja('걸');
;geulja.original_padchim = 'ㄷ';
;assert.equal(hangeul.padchim(geulja), 'ㄷ');

;geulja = new hangeul.Geulja('나');
;geulja.hidden_padchim = true;
;assert.equal(geulja.charAt(0), '나');
;assert.equal(geulja.charAt(-1).hidden_padchim, true);))
