(ns korean-conjugation.hangeul
  (:require [clojure.string :as string]))

(defn is-hangeul?
  [string]
  (let [start-char-code (.charCodeAt "가" 0)
        end-char-code (.charCodeAt "힣" 0)]
    (every? #(and (>= (.charCodeAt %1 0) start-char-code)
                  (<= (.charCodeAt %1 0) end-char-code))
            (seq string))))

(assert (= true (is-hangeul? "안녕")))
(assert (= false (is-hangeul? "?")))
(assert (= false (is-hangeul? "b아")))

; lead, padchim and vowel equations are all from
; http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html

(defn lead
  [character]
  (js/String.fromCharCode
    (+ (/ (js/Math.floor (- (.charCodeAt character 0) 44032)) 588)
       4352)))

(defn padchim
  [character]
  (let [code-point (+ (- (.charCodeAt "ᆨ" 0) 1)
                      (mod (- (.charCodeAt character 0) 44032) 28))]
    (if (= code-point 4519) nil ; code point of empty padchim
      (js/String.fromCharCode code-point))))

(defn vowel
  [character]
  (let [padchim-character (padchim character)
        ; TODO - handle fake padchim
        padchim-offset (cond (nil? padchim-character) -1
                             :else (- (.charCodeAt padchim-character 0)
                                      (.charCodeAt "ᆨ" 0)))]
    (js/String.fromCharCode
      (js/Math.floor (+ (/ (mod (- (.charCodeAt character 0)
                                   44032
                                   padchim-offset)
                                588)
                           28)
                        (.charCodeAt "ㅏ" 0))))))

(defn split
  [character]
  (if (is-hangeul? character)
    [(lead character) (vowel character) (padchim character)]
    [character]))

(assert (= (split "아") ["ᄋ" "ㅏ" nil]))
(assert (= (split "안") ["ᄋ" "ㅏ" "ᆫ"]))
(assert (= (split "웍") ["ᄋ" "ㅝ" "ᆨ"]))
(assert (= (split "안") ["ᄋ" "ㅏ" "ᆫ"]))

(defn join
  [lead vowel padchim]
  (let [lead-offset (- (.charCodeAt lead 0) (.charCodeAt "ᄀ" 0))
        vowel-offset (- (.charCodeAt vowel 0) (.charCodeAt "ㅏ" 0))
        padchim-offset (if padchim
                         (- (.charCodeAt padchim 0) (.charCodeAt "ᆨ" 0))
                         -1)
        code-point (+ padchim-offset (* vowel-offset 28) (* lead-offset 588) 44033)]
    (js/String.fromCharCode code-point)))

(assert (= (join "ᄋ" "ㅏ" "ᆫ") "안"))
(assert (= (join "ᄋ" "ㅝ" "ᆨ") "웍"))

(defn spread
  [string]
  (string/join "" (filter identity (mapcat split string))))

(assert (= (spread "안녕하세요") "ᄋㅏᆫᄂㅕᆼᄒㅏᄉㅔᄋㅛ"))
(assert (= (spread "뭐라고요?") "ᄆㅝᄅㅏᄀㅗᄋㅛ?"))


(defn find-vowel-to-append
  [string]
  (let [endings-that-get-uh #{"뜨" "쓰" "트"}
        vowels-that-get-ah #{"ㅗ" "ㅏ" "ㅑ"}
        appendable-character (first (cons (nth string 0) (reverse (filter #(not (or (= "ㅡ" (vowel %)) (nil? (padchim %)))) string))))]
    (cond
      (contains? endings-that-get-uh appendable-character) "어"
      (contains? vowels-that-get-ah (vowel appendable-character)) "아"
      :else "어")))

(assert (= (find-vowel-to-append "썼어요") "어"))
