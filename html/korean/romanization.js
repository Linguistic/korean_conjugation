// vim: set ts=4 sw=4 expandtab
// (C) 2010 Dan Bravender - licensed under the AGPL 3.0

try {
    var pronunciation = require('./pronunciation');
    var hangeul = require('./hangeul');
} catch(e) {}

var romanization = {};

romanization.ipa_transliteration = {
    'ㅏ': 'a',
    'ㅑ': 'ja',
    'ㅓ': 'ə',
    'ㅕ': 'jə',
    'ㅗ': 'o',
    'ㅛ': 'jo',
    'ㅜ': 'u',
    'ㅠ': 'ju',
    'ㅡ': 'ɨ',
    'ㅣ': 'i',
    'ㅐ': 'ɛ',
    'ㅒ': 'jɛ',
    'ㅔ': 'e',
    'ㅖ': 'ye',
    'ㅘ': 'wa',
    'ㅙ': 'wɛ',
    'ㅚ': 'we',
    'ㅝ': 'wə',
    'ㅞ': 'we',
    'ㅟ': 'wi',
    'ㅢ': 'ɨi',
    'ᄀ': ['k', 'k'],
    'ᄂ': ['n', 'n'],
    'ᄃ': ['d', 't'],
    'ᄅ': ['r', 'l'],
    'ᄆ': ['m', 'm'],
    'ᄇ': ['p', 'b'],
    'ᄉ': ['s', 's'],
    'ᄋ': ['', 'ŋ'],
    'ᄌ': ['tɕ', 't'],
    'ᄎ': ['tɕʰ','t'],
    'ᄏ': ['k', 'k'],
    'ᄐ': ['t', 't'],
    'ᄑ': ['pʰ', 'pʰ'],
    'ᄒ': ['h', 't'],
    'ᄁ': ['k͈','k͈'],
    'ᄄ': ['t͈','t͈'],
    'ᄈ': ['p͈','p͈'],
    'ᄊ': ['s͈','s͈'],
    'ᄍ': ['tɕ͈','tɕ͈'],
// The pronunciation engine should remove most of these
// these are here in the off chance that they make it through
    'ᆭ': ['', 'n'],
    'ᆬ': ['', 'n'],
    'ᆪ': ['', 'g'],
    'ᆰ': ['', 'l'],
    'ᆱ': ['', 'l'],
    'ᆲ': ['', 'l'],
    'ᆳ': ['', 'l'],
    'ᆴ': ['', 'l'],
    'ᆵ': ['', 'l'],
    'ᆶ': ['', 'l'],
    'ᆹ': ['', 'p']
};

romanization.transliteration = {
    'ㅏ': 'ah',
    'ㅑ': 'yah',
    'ㅓ': 'uh',
    'ㅕ': 'yuh',
    'ㅗ': 'oh',
    'ㅛ': 'yoh',
    'ㅜ': 'oo',
    'ㅠ': 'yoo',
    'ㅡ': 'eu',
    'ㅣ': 'ee',
    'ㅐ': 'ae',
    'ㅒ': 'yae',
    'ㅔ': 'ae',
    'ㅖ': 'yae',
    'ㅘ': 'wah',
    'ㅙ': 'wae',
    'ㅚ': 'wae',
    'ㅝ': 'wuh',
    'ㅞ': 'weh',
    'ㅟ': 'wee',
    'ㅢ': 'ui',
    'ᄀ': ['g', 'g'],
    'ᄂ': ['n', 'n'],
    'ᄃ': ['d', 't'],
    'ᄅ': ['r', 'l'],
    'ᄆ': ['m', 'm'],
    'ᄇ': ['b', 'p'],
    'ᄉ': ['s', 's'],
    'ᄋ': ['', 'ng'],
    'ᄌ': ['ch', 't'],
    'ᄎ': ['ch','t'],
    'ᄏ': ['k', 'k'],
    'ᄐ': ['t', 't'],
    'ᄑ': ['p', 'p'],
    'ᄒ': ['h', 't'],
    'ᄁ': ['gg','gg'],
    'ᄄ': ['tt','tt'],
    'ᄈ': ['bb','bb'],
    'ᄊ': ['ss','ss'],
    'ᄍ': ['jj','jj'],
// The pronunciation engine should remove most of these
// these are here in the off chance that they make it through
    'ᆭ': ['', 'n'],
    'ᆬ': ['', 'n'],
    'ᆪ': ['', 'g'],
    'ᆰ': ['', 'l'],
    'ᆱ': ['', 'l'],
    'ᆲ': ['', 'l'],
    'ᆳ': ['', 'l'],
    'ᆴ': ['', 'l'],
    'ᆵ': ['', 'l'],
    'ᆶ': ['', 'l'],
    'ᆹ': ['', 'p']
};

romanization.romanize_character = function(character, ipa) {
    if (!hangeul.is_hangeul(character)) {
        return character;
    }
    var lead = hangeul.lead(character);
    var vowel = hangeul.vowel(character);
    var padchim = hangeul.padchim(character);
    var transliterator = null;
    if (ipa) {
        transliterator = romanization.ipa_transliteration;
    } else {
        transliterator = romanization.transliteration;
    }
    var lead_transliteration = transliterator[lead][0];
    var vowel_transliteration = transliterator[vowel];
    var padchim_transliteration = null;
    if (padchim in romanization.transliteration) {
        padchim_transliteration = romanization.transliteration[padchim][1];
    } else {
        try {
            padchim_transliteration = romanization.transliteration[pronunciation.padchim_to_lead[padchim]][1];
        } catch(e) {
            padchim_transliteration = '';
        }
    }
    // What would a language be without irregulars?
    if (lead in {'ᄉ': true, 'ᄊ': true} && vowel in {'ㅑ': true, 'ㅣ': true, 'ㅛ': true, 'ㅠ': true}) {
        if (ipa) return 'ɕ' + vowel_transliteration + padchim_transliteration;
        return 'sh' + vowel_transliteration + padchim_transliteration;
    }
    return lead_transliteration + vowel_transliteration + padchim_transliteration;
};

romanization.romanize = function(word, ipa) {
    return pronunciation.get_pronunciation(word)
           .split('')
           .map(function (x) {return romanization.romanize_character(x, ipa);})
           .join('-');
};

romanization.ipa = function(word) {
    return romanization.romanize(word, true);
};

// This will be incremented when the algorithm is modified so clients
// that have cached API calls will know that their cache is invalid
romanization.version = 2;

// Export functions to node
try {
    for (var f in romanization) {
        exports[f] = romanization[f];
    }
} catch(e) {}
