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
     * Генерация предложения
     */
    public List<Word> generateSentence(int maxLength) {
        List<Word> sentence = new ArrayList<>();
        
        // Шаг 1: Выбираем подлежащее (существительное)
        Word subject = getRandomNoun();
        if (subject == null) return sentence;
        sentence.add(subject);
        
        // Шаг 2: С вероятностью 50% добавляем прилагательное перед существительным
        if (random.nextBoolean()) {
            Word adjective = getMatchingAdjective(subject.genus, Declension.NOMINATIVE);
            if (adjective != null) {
                sentence.add(0, adjective); // Вставляем в начало
            }
        }
        
        // Шаг 3: Добавляем глагол
        Word verb = getRandomVerb();
        if (verb != null) {
            Word conjugatedVerb = conjugateVerb(verb, subject.genus);
            sentence.add(conjugatedVerb);
        }
        
        // Шаг 4: Добавляем остальные слова до достижения нужной длины
        while (sentence.size() < maxLength) {
            Word lastWord = sentence.get(sentence.size() - 1);
            Word nextWord = null;
            
            // Выбираем следующее слово в зависимости от последнего
            if (lastWord.partOfSpeech == PartOfSpeech.VERB) {
                // После глагола может быть: наречие, предлог, существительное
                int choice = random.nextInt(3);
                if (choice == 0) {
                    nextWord = getRandomAdverb();
                } else if (choice == 1) {
                    nextWord = getRandomPreposition();
                } else {
                    nextWord = getRandomNounInCase(Declension.ACCUSTIVE);
                }
            } 
            else if (lastWord.partOfSpeech == PartOfSpeech.PREPOSITION) {
                // После предлога - существительное в предложном падеже
                nextWord = getRandomNounInCase(Declension.PREPOSITIONAL);
            }
            else if (lastWord.partOfSpeech == PartOfSpeech.NOUN) {
                // После существительного может быть предлог
                if (random.nextBoolean()) {
                    nextWord = getRandomPreposition();
                }
            }
            else if (lastWord.partOfSpeech == PartOfSpeech.ADVERB) {
                // После наречия может быть глагол
                if (random.nextBoolean()) {
                    nextWord = getRandomVerb();
                    if (nextWord != null) {
                        nextWord = conjugateVerb(nextWord, sentence.get(0).genus);
                    }
                }
            }
            
            if (nextWord != null) {
                sentence.add(nextWord);
            } else {
                break;
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
        copy.value = copy.declensionVal.get(Declension.NOMINATIVE);
        return copy;
    }
    
    /**
     * Получить случайное существительное в нужном падеже
     */
    private Word getRandomNounInCase(Declension declension) {
        List<Word> nouns = getWordsByPartOfSpeech(PartOfSpeech.NOUN);
        if (nouns.isEmpty()) return null;
        Word noun = nouns.get(random.nextInt(nouns.size()));
        Word copy = copyWord(noun);
        copy.value = copy.declensionVal.get(declension);
        return copy;
    }
    
    /**
     * Получить прилагательное, подходящее по роду и падежу
     */
    private Word getMatchingAdjective(Genus genus, Declension declension) {
        List<Word> adjectives = getWordsByPartOfSpeech(PartOfSpeech.ADJECTIVE);
        List<Word> matching = adjectives.stream()
            .filter(adj -> adj.genus == genus)
            .collect(Collectors.toList());
        
        if (matching.isEmpty()) return null;
        
        Word adj = matching.get(random.nextInt(matching.size()));
        Word copy = copyWord(adj);
        copy.value = copy.declensionVal.get(declension);
        return copy;
    }
    
    /**
     * Получить случайный глагол
     */
    private Word getRandomVerb() {
        List<Word> verbs = getWordsByPartOfSpeech(PartOfSpeech.VERB);
        if (verbs.isEmpty()) return null;
        return copyWord(verbs.get(random.nextInt(verbs.size())));
    }
    
    /**
     * Получить случайный предлог
     */
    private Word getRandomPreposition() {
        List<Word> preps = getWordsByPartOfSpeech(PartOfSpeech.PREPOSITION);
        if (preps.isEmpty()) return null;
        return copyWord(preps.get(random.nextInt(preps.size())));
    }
    
    /**
     * Получить случайное наречие
     */
    private Word getRandomAdverb() {
        List<Word> adverbs = getWordsByPartOfSpeech(PartOfSpeech.ADVERB);
        if (adverbs.isEmpty()) return null;
        return copyWord(adverbs.get(random.nextInt(adverbs.size())));
    }
    
    /**
     * Спряжение глагола по роду
     */
    private Word conjugateVerb(Word verb, Genus genus) {
        Word copy = copyWord(verb);
        String pastForm = copy.timesVal.get(Time.PAST);
        
        if (genus == Genus.MASCULINE) {
            copy.value = pastForm;
        } else if (genus == Genus.FEMININE) {
            copy.value = pastForm.replace("л", "ла");
        } else if (genus == Genus.NEUTER) {
            copy.value = pastForm.replace("л", "ло");
        } else {
            copy.value = pastForm.replace("л", "ли");
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
                sb.append(Character.toUpperCase(word.value.charAt(0)))
                  .append(word.value.substring(1));
            } else {
                sb.append(word.value);
            }
            
            if (i < sentence.size() - 1) {
                sb.append(" ");
            }
        }
        
        sb.append(".");
        return sb.toString();
    }
}