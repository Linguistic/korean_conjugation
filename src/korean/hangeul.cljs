(ns korean.hangeul
  (:require [clojure.string :as string]))

(defn geulja
  [string & [hidden-padchim original-padchim]]
  (let [string-with-meta (js/String. string)]
    (set! (.-hidden-padchim string-with-meta) hidden-padchim)
    (set! (.-original-padchim string-with-meta) original-padchim)
    (set! (.-charAt string-with-meta)
      (fn [offset]
        (let [inheriting-padchim-value (if (= (- (.-length string) 1) offset) hidden-padchim nil)]
          (geulja (.charAt string offset) inheriting-padchim-value original-padchim))))
    string-with-meta))

(defn hidden-padchim?
  [string]
  (.-hidden-padchim string))

(defn original-padchim
  [string]
  (.-original-padchim string))

(defn is-hangeul?
  [string]
  (let [start-char-code (.charCodeAt "가" 0)
        end-char-code (.charCodeAt "힣" 0)
        stripped-string (string/replace string #"[\!\"\?\. ]" "")]
    (every? #(and (>= (.charCodeAt %1 0) start-char-code)
                  (<= (.charCodeAt %1 0) end-char-code))
            (seq stripped-string))))

; lead, padchim and vowel equations are all from
; http://www.kfunigraz.ac.at/~katzer/korean_hangul_unicode.html

(defn lead
  [character]
  (js/String.fromCharCode
    (+ (/ (js/Math.floor (- (.charCodeAt character 0) 44032)) 588)
       4352)))

(defn padchim
  [character]
  (let [hidden-padchim? (hidden-padchim? character)
        original-padchim (original-padchim character)
        code-point (+ (- (.charCodeAt "ᆨ" 0) 1)
                      (mod (- (.charCodeAt character 0) 44032) 28))]
    (cond hidden-padchim? true
          original-padchim original-padchim
          (= code-point 4519) nil ; code point of empty padchim
          :else (js/String.fromCharCode code-point))))

(defn vowel
  [character]
  (let [padchim-character (padchim character)
        padchim-offset (cond (or (true? padchim-character) (nil? padchim-character)) -1
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

(defn join
  [lead vowel padchim]
  (let [lead-offset (- (.charCodeAt lead 0) (.charCodeAt "ᄀ" 0))
        vowel-offset (- (.charCodeAt vowel 0) (.charCodeAt "ㅏ" 0))
        padchim-offset (if padchim
                         (- (.charCodeAt padchim 0) (.charCodeAt "ᆨ" 0))
                         -1)
        code-point (+ padchim-offset (* vowel-offset 28) (* lead-offset 588) 44033)]
    (js/String.fromCharCode code-point)))

(defn spread-hangeul
  [string]
  (string/join "" (filter identity (mapcat split string))))

(defn find-vowel-to-append
  [string]
  (let [endings-that-get-uh #{"뜨" "쓰" "트"}
        vowels-that-get-ah #{"ㅗ" "ㅏ" "ㅑ"}
        appendable-character (first (reverse (cons (nth string 0) (filter #(not (and (= "ㅡ" (vowel %)) (nil? (padchim %)))) string))))]
    (cond
      (contains? endings-that-get-uh appendable-character) "어"
      (contains? vowels-that-get-ah (vowel appendable-character)) "아"
      :else "어")))

(defn hangeul-match
  [character lead-match vowel-match padchim-match]
  (let [lead (lead character)
        vowel (vowel character)
        padchim (padchim character)]
    (and (or (= lead-match "*") (= lead lead-match))
         (or (= vowel-match "*") (= vowel vowel-match))
         (or (= padchim-match "*") (= padchim padchim-match)))))
