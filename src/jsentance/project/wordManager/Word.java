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
        
        // Для существительных и прилагательных - пытаемся склонить
        String wordLower = value.toLowerCase();
        int length = wordLower.length();
        
        // Правила для разных окончаний
        if (wordLower.endsWith("а") || wordLower.endsWith("я")) {
            // Женский род на -а, -я
            String stem = wordLower.substring(0, length - 1);
            declensionVal.put(Declension.NOMINATIVE, wordLower);
            declensionVal.put(Declension.GENITIVE, stem + (wordLower.endsWith("а") ? "ы" : "и"));
            declensionVal.put(Declension.DATIVE, stem + "е");
            declensionVal.put(Declension.ACCUSTIVE, stem + (wordLower.endsWith("а") ? "у" : "ю"));
            declensionVal.put(Declension.CREATIVE, stem + (wordLower.endsWith("а") ? "ой" : "ей"));
            declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
        }
        else if (wordLower.endsWith("о") || wordLower.endsWith("е")) {
            // Средний род на -о, -е
            String stem = wordLower.substring(0, length - 1);
            declensionVal.put(Declension.NOMINATIVE, wordLower);
            declensionVal.put(Declension.GENITIVE, stem + (wordLower.endsWith("о") ? "а" : "я"));
            declensionVal.put(Declension.DATIVE, stem + (wordLower.endsWith("о") ? "у" : "ю"));
            declensionVal.put(Declension.ACCUSTIVE, wordLower);
            declensionVal.put(Declension.CREATIVE, stem + (wordLower.endsWith("о") ? "ом" : "ем"));
            declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
        }
        else if (wordLower.endsWith("ь")) {
            // Мягкий знак на конце
            String stem = wordLower.substring(0, length - 1);
            if (genus == Genus.MASCULINE) {
                // Мужской род на -ь
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "я");
                declensionVal.put(Declension.DATIVE, stem + "ю");
                declensionVal.put(Declension.ACCUSTIVE, stem + "я");
                declensionVal.put(Declension.CREATIVE, stem + "ем");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
            } else {
                // Женский род на -ь
                declensionVal.put(Declension.NOMINATIVE, wordLower);
                declensionVal.put(Declension.GENITIVE, stem + "и");
                declensionVal.put(Declension.DATIVE, stem + "и");
                declensionVal.put(Declension.ACCUSTIVE, wordLower);
                declensionVal.put(Declension.CREATIVE, stem + "ью");
                declensionVal.put(Declension.PREPOSITIONAL, stem + "и");
            }
        }
        else if (wordLower.endsWith("й")) {
            // Мужской род на -й
            String stem = wordLower.substring(0, length - 1);
            declensionVal.put(Declension.NOMINATIVE, wordLower);
            declensionVal.put(Declension.GENITIVE, stem + "я");
            declensionVal.put(Declension.DATIVE, stem + "ю");
            declensionVal.put(Declension.ACCUSTIVE, stem + "я");
            declensionVal.put(Declension.CREATIVE, stem + "ем");
            declensionVal.put(Declension.PREPOSITIONAL, stem + "е");
        }
        else if (wordLower.endsWith("ый") || wordLower.endsWith("ий")) {
            // Прилагательные мужского рода
            String stem = wordLower.substring(0, length - 2);
            declensionVal.put(Declension.NOMINATIVE, wordLower);
            declensionVal.put(Declension.GENITIVE, stem + "ого");
            declensionVal.put(Declension.DATIVE, stem + "ому");
            declensionVal.put(Declension.ACCUSTIVE, stem + "ый");
            declensionVal.put(Declension.CREATIVE, stem + "ым");
            declensionVal.put(Declension.PREPOSITIONAL, stem + "ом");
        }
        else {
            // Согласная на конце (мужской род)
            declensionVal.put(Declension.NOMINATIVE, wordLower);
            declensionVal.put(Declension.GENITIVE, wordLower + "а");
            declensionVal.put(Declension.DATIVE, wordLower + "у");
            declensionVal.put(Declension.ACCUSTIVE, wordLower);
            declensionVal.put(Declension.CREATIVE, wordLower + "ом");
            declensionVal.put(Declension.PREPOSITIONAL, wordLower + "е");
        }
        
        // Восстанавливаем оригинальный регистр для первой буквы
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
    * Метод для генерации временных форм глагола
    * Заполняет timesVal формами прошедшего и будущего времени
    */
    public void generateTenses() {
        if (value == null || value.isEmpty()) {
            return;
        }

        // Очищаем предыдущие временные формы
        timesVal.clear();

        // Если это не глагол - просто копируем значение во все времена
        if (partOfSpeech != PartOfSpeech.VERB) {
            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, value);
            timesVal.put(Time.FUTURE, value);
            return;
        }

        String wordLower = value.toLowerCase();
        int length = wordLower.length();

        // Определяем тип глагола по окончанию
        if (wordLower.endsWith("ть")) {
            // Инфинитив на -ть (например, "делать", "говорить")
            String stem = wordLower.substring(0, length - 2);

            // Настоящее время (для инфинитива используем исходную форму)
            timesVal.put(Time.PRESENT, value);

            // Прошедшее время (зависит от рода)
            if (genus == Genus.MASCULINE) {
                timesVal.put(Time.PAST, stem + "л");
            } else if (genus == Genus.FEMININE) {
                timesVal.put(Time.PAST, stem + "ла");
            } else if (genus == Genus.NEUTER) {
                timesVal.put(Time.PAST, stem + "ло");
            } else {
                // Если род не указан, используем мужской род по умолчанию
                timesVal.put(Time.PAST, stem + "л");
            }

            // Будущее время (для несовершенного вида - сложное, для совершенного - простое)
            // Для простоты используем сложное будущее время
            timesVal.put(Time.FUTURE, "будет " + value);
        }
        else if (wordLower.endsWith("ти")) {
            // Инфинитив на -ти (например, "нести", "везти")
            String stem = wordLower.substring(0, length - 2);

            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, stem + "л");
            timesVal.put(Time.FUTURE, "будет " + value);
        }
        else if (wordLower.endsWith("чь")) {
            // Инфинитив на -чь (например, "беречь", "печь", "стричь")
            String stem = wordLower.substring(0, length - 2);

            timesVal.put(Time.PRESENT, value);

            // Особое образование прошедшего времени для глаголов на -чь
            if (wordLower.equals("беречь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "берегла" : "берег");
            } else if (wordLower.equals("печь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "пекла" : "пек");
            } else if (wordLower.equals("стричь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "стригла" : "стриг");
            } else if (wordLower.equals("жечь")) {
                timesVal.put(Time.PAST, genus == Genus.FEMININE ? "жгла" : "жёг");
            } else {
                timesVal.put(Time.PAST, stem + "г" + (genus == Genus.FEMININE ? "ла" : ""));
            }

            timesVal.put(Time.FUTURE, "будет " + value);
        }
        else if (wordLower.endsWith("нуть")) {
            // Глаголы на -нуть (например, "прыгнуть", "крикнуть")
            String stem = wordLower.substring(0, length - 4);

            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, stem + "нул");
            timesVal.put(Time.FUTURE, value); // для совершенного вида
        }
        else if (wordLower.endsWith("ать") || wordLower.endsWith("ять")) {
            // Глаголы на -ать, -ять (например, "читать", "гулять")
            String stem = wordLower.substring(0, length - 3);
            String ending = wordLower.substring(length - 3);

            timesVal.put(Time.PRESENT, value);

            // Определяем основу для прошедшего времени
            if (ending.equals("ать")) {
                timesVal.put(Time.PAST, stem + "ал");
            } else if (ending.equals("ять")) {
                timesVal.put(Time.PAST, stem + "ял");
            }

            // Корректировка по роду
            String pastForm = timesVal.get(Time.PAST);
            if (pastForm != null) {
                if (genus == Genus.FEMININE) {
                    timesVal.put(Time.PAST, pastForm + "а");
                } else if (genus == Genus.NEUTER) {
                    timesVal.put(Time.PAST, pastForm + "о");
                } else if (genus == Genus.PLURAL) {
                    timesVal.put(Time.PAST, pastForm + "и");
                }
            }

            timesVal.put(Time.FUTURE, "будет " + value);
        }
        else if (wordLower.endsWith("ить")) {
            // Глаголы на -ить (например, "говорить", "любить")
            String stem = wordLower.substring(0, length - 3);

            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, stem + "ил");

            // Корректировка по роду
            String pastForm = timesVal.get(Time.PAST);
            if (genus == Genus.FEMININE) {
                timesVal.put(Time.PAST, pastForm + "а");
            } else if (genus == Genus.NEUTER) {
                timesVal.put(Time.PAST, pastForm + "о");
            } else if (genus == Genus.PLURAL) {
                timesVal.put(Time.PAST, pastForm + "и");
            }

            timesVal.put(Time.FUTURE, "будет " + value);
        }
        else {
            // Для остальных случаев
            timesVal.put(Time.PRESENT, value);
            timesVal.put(Time.PAST, value + " (прошедшее)");
            timesVal.put(Time.FUTURE, value + " (будущее)");
        }

        // Восстанавливаем оригинальный регистр для первой буквы
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
