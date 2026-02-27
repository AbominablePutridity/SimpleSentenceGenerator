package jsentance;

import java.util.ArrayList;
import java.util.List;
import jsentance.project.generationManager.SentenceGenerator;
import jsentance.project.wordManager.Genus;
import jsentance.project.wordManager.PartOfSpeech;
import jsentance.project.wordManager.Word;

/**
 *
 * @author maxim
 */
public class JSentance {

    public static void main(String[] args) {
        // ===== 1. СОЗДАЕМ СЛОВА =====
        List<Word> words = createVocabulary();
        
        // ===== 2. СОЗДАЕМ ГЕНЕРАТОР =====
        SentenceGenerator generator = new SentenceGenerator(words);
        
        // ===== 3. ГЕНЕРИРУЕМ ПРЕДЛОЖЕНИЯ =====
        System.out.println("==========================================");
        System.out.println("   ГЕНЕРАЦИЯ ГРАММАТИЧЕСКИ ПРАВИЛЬНЫХ ПРЕДЛОЖЕНИЙ");
        System.out.println("==========================================\n");
        
        // Генерируем 5 предложений разной длины
        for (int i = 1; i <= 5; i++) {
            int length = 5 + i; // от 6 до 10 слов
            List<Word> sentence = generator.generateSentence(length);
            System.out.println(i + ". " + generator.sentenceToString(sentence));
        }
        
        System.out.println("\n==========================================");
        System.out.println("   ГЕНЕРАЦИЯ ЗАВЕРШЕНА");
        System.out.println("==========================================");
    }
    
    private static List<Word> createVocabulary() {
        List<Word> words = new ArrayList<>();
        
        // СУЩЕСТВИТЕЛЬНЫЕ
        words.add(new Word("кот", Genus.MASCULINE, PartOfSpeech.NOUN));
        words.add(new Word("собака", Genus.FEMININE, PartOfSpeech.NOUN));
        words.add(new Word("дом", Genus.MASCULINE, PartOfSpeech.NOUN));
        words.add(new Word("машина", Genus.FEMININE, PartOfSpeech.NOUN));
        words.add(new Word("дерево", Genus.NEUTER, PartOfSpeech.NOUN));
        words.add(new Word("город", Genus.MASCULINE, PartOfSpeech.NOUN));
        words.add(new Word("река", Genus.FEMININE, PartOfSpeech.NOUN));
        words.add(new Word("солнце", Genus.NEUTER, PartOfSpeech.NOUN));
        words.add(new Word("стол", Genus.MASCULINE, PartOfSpeech.NOUN));
        words.add(new Word("книга", Genus.FEMININE, PartOfSpeech.NOUN));
        words.add(new Word("девушка", Genus.FEMININE, PartOfSpeech.NOUN));
        
        // ПРИЛАГАТЕЛЬНЫЕ
        words.add(new Word("красный", Genus.MASCULINE, PartOfSpeech.ADJECTIVE));
        words.add(new Word("красная", Genus.FEMININE, PartOfSpeech.ADJECTIVE));
        words.add(new Word("красное", Genus.NEUTER, PartOfSpeech.ADJECTIVE));
        words.add(new Word("большой", Genus.MASCULINE, PartOfSpeech.ADJECTIVE));
        words.add(new Word("большая", Genus.FEMININE, PartOfSpeech.ADJECTIVE));
        words.add(new Word("большое", Genus.NEUTER, PartOfSpeech.ADJECTIVE));
        words.add(new Word("маленький", Genus.MASCULINE, PartOfSpeech.ADJECTIVE));
        words.add(new Word("маленькая", Genus.FEMININE, PartOfSpeech.ADJECTIVE));
        words.add(new Word("маленькое", Genus.NEUTER, PartOfSpeech.ADJECTIVE));
        
        // ГЛАГОЛЫ
        words.add(new Word("бежать", Genus.NONE, PartOfSpeech.VERB));
        words.add(new Word("сидеть", Genus.NONE, PartOfSpeech.VERB));
        words.add(new Word("лежать", Genus.NONE, PartOfSpeech.VERB));
        words.add(new Word("стоять", Genus.NONE, PartOfSpeech.VERB));
        words.add(new Word("думать", Genus.NONE, PartOfSpeech.VERB));
        words.add(new Word("говорить", Genus.NONE, PartOfSpeech.VERB));
        words.add(new Word("читать", Genus.NONE, PartOfSpeech.VERB));
        
        // ПРЕДЛОГИ
        words.add(new Word("в", Genus.NONE, PartOfSpeech.PREPOSITION));
        words.add(new Word("на", Genus.NONE, PartOfSpeech.PREPOSITION));
        words.add(new Word("под", Genus.NONE, PartOfSpeech.PREPOSITION));
        words.add(new Word("о", Genus.NONE, PartOfSpeech.PREPOSITION));
        
        // НАРЕЧИЯ
        words.add(new Word("быстро", Genus.NONE, PartOfSpeech.ADVERB));
        words.add(new Word("медленно", Genus.NONE, PartOfSpeech.ADVERB));
        words.add(new Word("громко", Genus.NONE, PartOfSpeech.ADVERB));
        words.add(new Word("тихо", Genus.NONE, PartOfSpeech.ADVERB));
        
        return words;
    }
}
