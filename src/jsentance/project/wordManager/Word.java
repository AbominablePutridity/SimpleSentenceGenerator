package jsentance.project.wordManager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author maxim
 */
public class Word {
    public String value; //слово в настоящем времени
    public Map<Time, String> timesVal = new HashMap<>(); //слово в прошлом времени и будующем времени
    public Genus genus; //род слова
    public PartOfSpeech partOfSpeech; //часть речи слова
    public Map<Declension, String> declensionVal = new HashMap<>(); //все склонения слова
    
    public Word(String value, Genus genus, PartOfSpeech partOfSpeech)
    {
        this.value = value;
        this.genus = genus;
        this.partOfSpeech = partOfSpeech;
        
        generateDeclensions(); //генерируем падежи
        generateTenses(); //генерируем времена
    }
    
    /**
     * ЕДИНСТВЕННАЯ ФУНКЦИЯ для генерации всех склонений слова
     */
    public void generateDeclensions() {
        if (value == null || value.isEmpty()) {
            return;
        }
        
        // Очищаем предыдущие склонения
        declensionVal.clear();
        
        // Для глаголов - просто копируем значение во все падежи
        if (partOfSpeech == PartOfSpeech.VERB) {
            for (Declension d : Declension.values()) {
                declensionVal.put(d, value);
            }
            return;
        }
        
        String wordLower = value.toLowerCase();
        int length = wordLower.length();
        
        // Определяем основу слова (без окончания)
        String stem = wordLower;
        String lastChar = wordLower.substring(length - 1);
        String lastTwoChars = length > 1 ? wordLower.substring(length - 2) : lastChar;
        
        // СУЩЕСТВИТЕЛЬНЫЕ
        if (partOfSpeech == PartOfSpeech.NOUN) {
            
            // 1-е склонение (женский и мужской род на -а, -я)
            if (wordLower.endsWith("а") || wordLower.endsWith("я")) {
                stem = wordLower.substring(0, length - 1);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + (wordLower.endsWith("а") ? "ы" : "и"));
                declensionVal.put(Declension.DATIVE, stem + "е");
                declensionVal.put(Declension.ACCUSTIVE, stem + (wordLower.endsWith("а") ? "у" : "ю"));
                declensionVal.put(Declension.CREATIVE, stem + (wordLower.endsWith("а") ? "ой" : "ей"));
                declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
            }
            
            // 2-е склонение (мужской род с нулевым окончанием, средний род на -о, -е)
            else if (wordLower.endsWith("о") || wordLower.endsWith("е")) {
                stem = wordLower.substring(0, length - 1);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + (wordLower.endsWith("о") ? "а" : "я"));
                declensionVal.put(Declension.DATIVE, stem + (wordLower.endsWith("о") ? "у" : "ю"));
                declensionVal.put(Declension.ACCUSTIVE, wordLower); // для неодушевленных
                declensionVal.put(Declension.CREATIVE, stem + (wordLower.endsWith("о") ? "ом" : "ем"));
                declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
            }
            
            // Мужской род на согласную или -й
            else if (!wordLower.endsWith("ь") && (wordLower.endsWith("й") || 
                     "бвгджзйклмнпрстфхцчшщ".contains(lastChar))) {
                
                if (wordLower.endsWith("й")) {
                    stem = wordLower.substring(0, length - 1);
                    declensionVal.put(Declension.NOMINATIVE, wordLower);
                    declensionVal.put(Declension.GENITIVE, stem + "я");
                    declensionVal.put(Declension.DATIVE, stem + "ю");
                    declensionVal.put(Declension.ACCUSTIVE, stem + "я"); // для одушевленных
                    declensionVal.put(Declension.CREATIVE, stem + "ем");
                    declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
                } else {
                    // Согласная на конце
                    declensionVal.put(Declension.NOMINATIVE, wordLower);
                    declensionVal.put(Declension.GENITIVE, wordLower + "а");
                    declensionVal.put(Declension.DATIVE, wordLower + "у");
                    declensionVal.put(Declension.ACCUSTIVE, wordLower); // для неодушевленных
                    declensionVal.put(Declension.CREATIVE, wordLower + "ом");
                    declensionVal.put(Declension.PREPOSITIONAL, wordLower + "е");
                }
            }
            
            // 3-е склонение (женский род на -ь)
            else if (wordLower.endsWith("ь") && genus == Genus.FEMININE) {
                stem = wordLower.substring(0, length - 1);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "и");
                declensionVal.put(Declension.DATIVE, stem + "и");
                declensionVal.put(Declension.ACCUSTIVE, wordLower);
                declensionVal.put(Declension.CREATIVE, stem + "ью");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "и");
            }
            
            // Мужской род на -ь
            else if (wordLower.endsWith("ь") && genus == Genus.MASCULINE) {
                stem = wordLower.substring(0, length - 1);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "я");
                declensionVal.put(Declension.DATIVE, stem + "ю");
                declensionVal.put(Declension.ACCUSTIVE, stem + "я");
                declensionVal.put(Declension.CREATIVE, stem + "ем");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
            }
            
            // Разносклоняемые (время, имя и т.д.)
            else if (wordLower.equals("время") || wordLower.equals("имя") || 
                     wordLower.equals("племя") || wordLower.equals("семя")) {
                stem = wordLower.substring(0, length - 1);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "ени");
                declensionVal.put(Declension.DATIVE, stem + "ени");
                declensionVal.put(Declension.ACCUSTIVE, wordLower);
                declensionVal.put(Declension.CREATIVE, stem + "енем");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "ени");
            }
            
            else {
                // Если ничего не подошло, копируем значение
                for (Declension d : Declension.values()) {
                    declensionVal.put(d, wordLower);
                }
            }
        }
        
        // ПРИЛАГАТЕЛЬНЫЕ
        else if (partOfSpeech == PartOfSpeech.ADJECTIVE) {
            
            // Твердая основа (мужской род на -ый, -ой)
            if (wordLower.endsWith("ый") || wordLower.endsWith("ой")) {
                stem = wordLower.substring(0, length - 2);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "ого");
                declensionVal.put(Declension.DATIVE, stem + "ому");
                declensionVal.put(Declension.ACCUSTIVE, wordLower.endsWith("ой") ? stem + "ого" : wordLower);
                declensionVal.put(Declension.CREATIVE, stem + "ым");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "ом");
            }
            
            // Мягкая основа (мужской род на -ий)
            else if (wordLower.endsWith("ий") && genus == Genus.MASCULINE) {
                stem = wordLower.substring(0, length - 2);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "его");
                declensionVal.put(Declension.DATIVE, stem + "ему");
                declensionVal.put(Declension.ACCUSTIVE, wordLower);
                declensionVal.put(Declension.CREATIVE, stem + "им");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "ем");
            }
            
            // Женский род на -ая, -яя
            else if (wordLower.endsWith("ая") || wordLower.endsWith("яя")) {
                stem = wordLower.substring(0, length - 2);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + (wordLower.endsWith("ая") ? "ой" : "ей"));
                declensionVal.put(Declension.DATIVE, stem + (wordLower.endsWith("ая") ? "ой" : "ей"));
                declensionVal.put(Declension.ACCUSTIVE, stem + (wordLower.endsWith("ая") ? "ую" : "юю"));
                declensionVal.put(Declension.CREATIVE, stem + (wordLower.endsWith("ая") ? "ой" : "ей"));
                declensionVal.put(Declension.PREPOSITIONAL, stem + (wordLower.endsWith("ая") ? "ой" : "ей"));
            }
            
            // Средний род на -ое, -ее
            else if (wordLower.endsWith("ое") || wordLower.endsWith("ее")) {
                stem = wordLower.substring(0, length - 2);
                
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + (wordLower.endsWith("ое") ? "ого" : "его"));
                declensionVal.put(Declension.DATIVE, stem + (wordLower.endsWith("ое") ? "ому" : "ему"));
                declensionVal.put(Declension.ACCUSTIVE, wordLower);
                declensionVal.put(Declension.CREATIVE, stem + (wordLower.endsWith("ое") ? "ым" : "им"));
                declensionVal.put(Declension.PREPOSITIONAL, stem + (wordLower.endsWith("ое") ? "ом" : "ем"));
            }
            
            else {
                for (Declension d : Declension.values()) {
                    declensionVal.put(d, wordLower);
                }
            }
        }
        
        // ОСТАЛЬНЫЕ ЧАСТИ РЕЧИ
        else {
            for (Declension d : Declension.values()) {
                declensionVal.put(d, wordLower);
            }
        }
        
        // Восстанавливаем регистр для первой буквы
        if (Character.isUpperCase(value.charAt(0))) {
            for (Map.Entry<Declension, String> entry : declensionVal.entrySet()) {
                String val = entry.getValue();
                if (val != null && !val.isEmpty()) {
                    declensionVal.put(entry.getKey(), 
                        Character.toUpperCase(val.charAt(0)) + val.substring(1));
                }
            }
        }
    }
    
    /**
     * метод для генерации временных форм глагола
     */
    public void generateTenses() {
        if (value == null || value.isEmpty()) {
            return;
        }

        timesVal.clear();

        // Если это не глагол - копируем значение
        if (partOfSpeech != PartOfSpeech.VERB) {
            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, value);
            timesVal.put(Time.FUTURE, value);
            return;
        }

        String wordLower = value.toLowerCase();
        int length = wordLower.length();

        // Определяем тип глагола
        if (wordLower.endsWith("ть")) {
            // 1 спряжение (читать, писать)
            String stem = wordLower.substring(0, length - 2);
            String lastConsonant = stem.length() > 0 ? stem.substring(stem.length() - 1) : "";
            
            timesVal.put(Time.PRESENT, value);
            
            // Прошедшее время
            if (genus == Genus.MASCULINE || genus == Genus.NONE) {
                timesVal.put(Time.PAST, stem + "л");
            } else if (genus == Genus.FEMININE) {
                timesVal.put(Time.PAST, stem + "ла");
            } else if (genus == Genus.NEUTER) {
                timesVal.put(Time.PAST, stem + "ло");
            } else {
                timesVal.put(Time.PAST, stem + "ли");
            }
            
            // Настоящее время (для невозвратных глаголов)
            if (!wordLower.endsWith("ся") && !wordLower.endsWith("сь")) {
                if (wordLower.endsWith("ать") || wordLower.endsWith("ять")) {
                    timesVal.put(Time.PRESENT, stem + "ет");
                } else if (wordLower.endsWith("ить")) {
                    timesVal.put(Time.PRESENT, stem + "ит");
                }
            }
            
            timesVal.put(Time.FUTURE, "будет " + value);
        }
        
        else if (wordLower.endsWith("ти")) {
            String stem = wordLower.substring(0, length - 2);
            
            timesVal.put(Time.PRESENT, value);
            
            if (genus == Genus.MASCULINE || genus == Genus.NONE) {
                timesVal.put(Time.PAST, stem + "л");
            } else if (genus == Genus.FEMININE) {
                timesVal.put(Time.PAST, stem + "ла");
            } else if (genus == Genus.NEUTER) {
                timesVal.put(Time.PAST, stem + "ло");
            } else {
                timesVal.put(Time.PAST, stem + "ли");
            }
            
            timesVal.put(Time.FUTURE, "будет " + value);
        }
        
        else if (wordLower.endsWith("чь")) {
            String stem = wordLower.substring(0, length - 2);
            
            timesVal.put(Time.PRESENT, value);
            
            // Особые формы для глаголов на -чь
            if (wordLower.equals("мочь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "могла" : "мог");
            } else if (wordLower.equals("печь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "пекла" : "пек");
            } else if (wordLower.equals("беречь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "берегла" : "берег");
            } else {
                timesVal.put(Time.PAST, stem + "г" + (genus == Genus.FEMININE ? "ла" : ""));
            }
            
            timesVal.put(Time.FUTURE, "будет " + value);
        }
        
        else if (wordLower.endsWith("нуть")) {
            String stem = wordLower.substring(0, length - 4);
            
            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, stem + "нул");
            timesVal.put(Time.FUTURE, value);
        }
        
        else {
            // Для остальных случаев
            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, value.replace("ть", "л").replace("ти", "л"));
            timesVal.put(Time.FUTURE, "будет " + value);
        }

        // Восстанавливаем регистр
        if (Character.isUpperCase(value.charAt(0))) {
            for (Map.Entry<Time, String> entry : timesVal.entrySet()) {
                String val = entry.getValue();
                if (val != null && !val.isEmpty() && !val.startsWith("будет")) {
                    timesVal.put(entry.getKey(), 
                        Character.toUpperCase(val.charAt(0)) + val.substring(1));
                }
            }
        }
    }
}
