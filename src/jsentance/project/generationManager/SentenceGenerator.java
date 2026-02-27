package jsentance.project.generationManager;

import java.util.*;
import java.util.stream.Collectors;
import jsentance.project.wordManager.Declension;
import jsentance.project.wordManager.Genus;
import jsentance.project.wordManager.PartOfSpeech;
import jsentance.project.wordManager.Time;
import jsentance.project.wordManager.Word;

/**
 * Класс для генерации грамматически правильных предложений
 */
public class SentenceGenerator {
    private List<Word> words;
    private Random random;
    
    public SentenceGenerator(List<Word> words) {
        this.words = words;
        this.random = new Random();
    }
    
    /**
     * Создать копию слова
     */
    private Word copyWord(Word original) {
        Word copy = new Word(original.value, original.genus, original.partOfSpeech);
        copy.declensionVal.putAll(original.declensionVal);
        copy.timesVal.putAll(original.timesVal);
        return copy;
    }
    
    /**
     * Получить все слова определенной части речи
     */
    private List<Word> getWordsByPartOfSpeech(PartOfSpeech pos) {
        return words.stream()
            .filter(word -> word.partOfSpeech == pos)
            .collect(Collectors.toList());
    }
    
    /**
     * Проверить, есть ли у слова форма для указанного падежа
     */
    private boolean hasDeclensionForm(Word word, Declension declension) {
        return word.declensionVal.containsKey(declension) && 
               word.declensionVal.get(declension) != null &&
               !word.declensionVal.get(declension).isEmpty();
    }
    
    /**
     * Получить форму слова в нужном падеже (с проверкой)
     */
    private String getDeclensionForm(Word word, Declension declension) {
        if (hasDeclensionForm(word, declension)) {
            return word.declensionVal.get(declension);
        }
        // Если нет нужной формы, возвращаем исходное слово
        return word.value;
    }
    
    /**
    * Получить правильный падеж для существительного после предлога
    */
   private Declension getCaseForPreposition(Word preposition) {
       // Русские предлоги и соответствующие им падежи
       switch (preposition.value) {
           case "в":
           case "на":
           case "о":
           case "при":
               return Declension.PREPOSITIONAL; // предложный падеж
           case "без":
           case "для":
           case "до":
           case "из":
           case "от":
           case "у":
               return Declension.GENITIVE; // родительный падеж
           case "к":
               return Declension.DATIVE; // дательный падеж
           case "под":
           case "за":
           case "над":
           case "перед":
           case "между":
               return Declension.CREATIVE; // творительный падеж
           default:
               return Declension.PREPOSITIONAL;
       }
   }

   /**
    * Генерация предложения
    */
   public List<Word> generateSentence(int maxLength) {
       List<Word> sentence = new ArrayList<>();

       // Шаг 1: Выбираем подлежащее (существительное)
       Word subject = getRandomNoun();
       if (subject == null) return sentence;
       sentence.add(subject);

       // Шаг 2: С вероятностью 50% добавляем прилагательное перед существительным
       if (random.nextBoolean() && sentence.size() < maxLength) {
           Word adjective = getMatchingAdjective(subject.genus, Declension.NOMINATIVE);
           if (adjective != null) {
               sentence.add(0, adjective);
           }
       }

       // Шаг 3: Добавляем глагол (всегда)
       if (sentence.size() < maxLength) {
           Word verb = getRandomVerb();
           if (verb != null) {
               Word conjugatedVerb = conjugateVerb(verb, subject.genus);
               sentence.add(conjugatedVerb);
           }
       }

       // Шаг 4: Добавляем дополнения
       boolean canAddMore = true;
       int safetyCounter = 0;
       Word lastPreposition = null;

       while (sentence.size() < maxLength && canAddMore && safetyCounter < 20) {
           safetyCounter++;
           Word lastWord = sentence.get(sentence.size() - 1);
           Word nextWord = null;

           // Определяем возможное следующее слово на основе последнего
           if (lastWord.partOfSpeech == PartOfSpeech.VERB) {
               // После глагола может быть наречие или дополнение
               int choice = random.nextInt(3);

               if (choice == 0) {
                   // Наречие
                   nextWord = getRandomAdverb();
               } else if (choice == 1) {
                   // Предлог
                   nextWord = getRandomPreposition();
                   if (nextWord != null) {
                       lastPreposition = nextWord;
                   }
               } else {
                   // Существительное в винительном падеже
                   nextWord = getRandomNounInCase(Declension.ACCUSTIVE);
               }
           } 
           else if (lastWord.partOfSpeech == PartOfSpeech.PREPOSITION) {
               // После предлога - существительное в правильном падеже
               Declension requiredCase = getCaseForPreposition(lastWord);
               nextWord = getRandomNounInCase(requiredCase);
               lastPreposition = null;
           }
           else if (lastWord.partOfSpeech == PartOfSpeech.NOUN) {
               // После существительного может быть предлог
               if (random.nextInt(3) == 0) { // 33% шанс
                   nextWord = getRandomPreposition();
                   if (nextWord != null) {
                       lastPreposition = nextWord;
                   }
               }
           }
           else if (lastWord.partOfSpeech == PartOfSpeech.ADVERB) {
               // После наречия может быть предлог
               if (random.nextBoolean()) {
                   nextWord = getRandomPreposition();
                   if (nextWord != null) {
                       lastPreposition = nextWord;
                   }
               } else {
                   canAddMore = false;
               }
           }

           // Проверяем, что получили валидное слово
           if (nextWord != null) {
               Word finalNextWord = nextWord;
               // Проверяем, что слово не повторяется слишком часто
               boolean isDuplicate = sentence.stream()
                   .filter(w -> w.partOfSpeech == finalNextWord.partOfSpeech)
                   .count() > 2;

               if (!isDuplicate) {
                   sentence.add(nextWord);
               }
           } else {
               // Если не можем добавить слово, уменьшаем шанс продолжения
               canAddMore = random.nextInt(100) < 30;
           }
       }

       return sentence;
   }
    
    /**
     * Получить случайное существительное
     */
    private Word getRandomNoun() {
        List<Word> nouns = getWordsByPartOfSpeech(PartOfSpeech.NOUN);
        if (nouns.isEmpty()) return null;
        
        Word noun = nouns.get(random.nextInt(nouns.size()));
        Word copy = copyWord(noun);
        
        String nominative = getDeclensionForm(copy, Declension.NOMINATIVE);
        copy.value = nominative;
        
        return copy;
    }
    
    /**
     * Получить случайное существительное в нужном падеже
     */
    private Word getRandomNounInCase(Declension declension) {
        List<Word> nouns = getWordsByPartOfSpeech(PartOfSpeech.NOUN);
        if (nouns.isEmpty()) return null;
        
        // Фильтруем существительные, у которых есть нужный падеж
        List<Word> validNouns = nouns.stream()
            .filter(noun -> hasDeclensionForm(noun, declension))
            .collect(Collectors.toList());
        
        if (validNouns.isEmpty()) {
            // Если нет слов с нужным падежом, берем любое
            Word noun = nouns.get(random.nextInt(nouns.size()));
            Word copy = copyWord(noun);
            copy.value = copy.value; // Оставляем как есть
            return copy;
        }
        
        Word noun = validNouns.get(random.nextInt(validNouns.size()));
        Word copy = copyWord(noun);
        copy.value = getDeclensionForm(copy, declension);
        
        return copy;
    }
    
    /**
     * Получить прилагательное, подходящее по роду и падежу
     */
    private Word getMatchingAdjective(Genus genus, Declension declension) {
        List<Word> adjectives = getWordsByPartOfSpeech(PartOfSpeech.ADJECTIVE);
        
        // Ищем прилагательные подходящего рода
        List<Word> matching = adjectives.stream()
            .filter(adj -> adj.genus == genus)
            .filter(adj -> hasDeclensionForm(adj, declension))
            .collect(Collectors.toList());
        
        if (matching.isEmpty()) {
            // Если нет точного совпадения, берем любое прилагательное
            if (adjectives.isEmpty()) return null;
            Word adj = adjectives.get(random.nextInt(adjectives.size()));
            Word copy = copyWord(adj);
            copy.value = getDeclensionForm(copy, declension);
            return copy;
        }
        
        Word adj = matching.get(random.nextInt(matching.size()));
        Word copy = copyWord(adj);
        copy.value = getDeclensionForm(copy, declension);
        
        return copy;
    }
    
    /**
     * Получить случайный глагол
     */
    private Word getRandomVerb() {
        List<Word> verbs = getWordsByPartOfSpeech(PartOfSpeech.VERB);
        if (verbs.isEmpty()) return null;
        
        Word verb = verbs.get(random.nextInt(verbs.size()));
        return copyWord(verb);
    }
    
    /**
     * Получить случайный предлог
     */
    private Word getRandomPreposition() {
        List<Word> preps = getWordsByPartOfSpeech(PartOfSpeech.PREPOSITION);
        if (preps.isEmpty()) return null;
        
        Word prep = preps.get(random.nextInt(preps.size()));
        return copyWord(prep);
    }
    
    /**
     * Получить случайное наречие
     */
    private Word getRandomAdverb() {
        List<Word> adverbs = getWordsByPartOfSpeech(PartOfSpeech.ADVERB);
        if (adverbs.isEmpty()) return null;
        
        Word adverb = adverbs.get(random.nextInt(adverbs.size()));
        return copyWord(adverb);
    }
    
    /**
     * Спряжение глагола по роду
     */
    private Word conjugateVerb(Word verb, Genus genus) {
        Word copy = copyWord(verb);
        
        // Проверяем наличие формы прошедшего времени
        if (!copy.timesVal.containsKey(Time.PAST)) {
            // Если нет, возвращаем как есть
            return copy;
        }
        
        String pastForm = copy.timesVal.get(Time.PAST);
        
        if (genus == Genus.MASCULINE) {
            copy.value = pastForm;
        } else if (genus == Genus.FEMININE) {
            // Простая замена окончания
            if (pastForm.endsWith("л")) {
                copy.value = pastForm + "а";
            } else {
                copy.value = pastForm;
            }
        } else if (genus == Genus.NEUTER) {
            if (pastForm.endsWith("л")) {
                copy.value = pastForm + "о";
            } else {
                copy.value = pastForm;
            }
        } else {
            copy.value = pastForm;
        }
        
        return copy;
    }
    
    /**
     * Преобразовать предложение в строку
     */
    public String sentenceToString(List<Word> sentence) {
        if (sentence.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < sentence.size(); i++) {
            Word word = sentence.get(i);

            if (i == 0) {
                // Первое слово с большой буквы
                sb.append(Character.toUpperCase(word.value.charAt(0)))
                  .append(word.value.substring(1));
            } else {
                // Всегда добавляем пробел перед следующим словом
                sb.append(" ");

                // Проверяем, не является ли предыдущее слово предлогом
                Word prevWord = sentence.get(i - 1);
                if (prevWord.partOfSpeech == PartOfSpeech.PREPOSITION) {
                    // Если предыдущее слово - предлог, пишем через пробел как обычно
                    sb.append(word.value);
                } else {
                    sb.append(word.value);
                }
            }
        }

        sb.append(".");
        return sb.toString();
    }
}