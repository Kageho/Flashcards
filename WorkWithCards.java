package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;


public class WorkWithCards {
    private static Map<String, String> flashCards = new LinkedHashMap<>();
    private static Map<String, Integer> mistakesInFlashCards = new LinkedHashMap<>();
    private static Map<String, String> bufferMapForImport = new LinkedHashMap<>();
    private static Map<String, Integer> bufferMapForMistakes = new LinkedHashMap<>();
    private static String filePath;
    private static List<String> logList = new ArrayList<>(); // for logging every the input and output
    private static String importPath = null; // file to get cards
    private static String exportPath = null; // file to save cards after termination of program


    final static Scanner scanner = new Scanner(System.in);

    static void makeYourChoice(String[] args) {
        processCLI(args);
//        import cards from file if i have some path
        if (!Objects.equals(null, importPath)) {
            importCards(importPath);
        }
        boolean isTerminate = false;
        do {
            println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String action = inputString().toLowerCase();
            switch (action) {
                case "exit":
                    isTerminate = true;
                    break;
                case "add":
                    // add some stuff
                    addCard();
                    break;
                case "remove":
//                   remove card
                    removeCard();
                    break;
                case "import":
//                    working with files
                    importCardsInRunTime();
                    break;
                case "export":
//                    again working with files
                    exportCardsInRunTime();
                    break;
                case "ask":
                    askRandomCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    printHardestCard();
                    break;
                default:
                    println("Looks like you miss. Try again");
            }
        } while (!isTerminate);

        println("Bye bye!");
//        if program get path for export, it will save cards
        if (!Objects.equals(exportPath, null)) {
            exportCards(exportPath);
        }
    }

    //    process command line arguments
    private static void processCLI(String[] args) {
        for (var i = 0; i < args.length - 1; i++) {
            if (Objects.equals("-import", args[i])) {
                importPath = args[++i];
            }
            if (Objects.equals("-export", args[i])) {
                exportPath = args[++i];
            }
        }
    }

    // method for logging output
    private static void println(String str) {
        logList.add(str);
        System.out.println(str);
    }

    // method for getting user's input as int
    private static Integer inputNumber() {
        int number = scanner.nextInt();
        logList.add(String.valueOf(number));
        return number;
    }

    // method for get user's input as string
    private static String inputString() {
        String str = scanner.nextLine();
        logList.add(str);
        return str;
    }

    //    method for clear statics i.e. delete values in mistakesInFlashCards
    private static void resetStats() {
        for (var entry : mistakesInFlashCards.entrySet()) {
            mistakesInFlashCards.put(entry.getKey(), 0);
        }
        println("Card statistics has been reset.");
    }

    // write all the input and output to some file
    private static void log() {
        println("File name:");
        String filePath = inputString();
        try (PrintWriter printWriter = new PrintWriter(new File(filePath))) {
            for (String str : logList) {
                printWriter.write(str + "\n");
            }
            println("The log has been saved.");
            printWriter.write(logList.get(logList.size() - 1));
        } catch (FileNotFoundException fnf) {
            println("File not found");
        }
    }

    //    method for asking random card
    private static void askRandomCard() {
        Object[] keys = flashCards.keySet().toArray();
        Random random = new Random();
        println("How many times to ask?");
        int numberOfQuestions = inputNumber();
        scanner.nextLine();
        // here i ask definitions for random cards
        for (int i = 0; i < numberOfQuestions; i++) {
            String card = String.valueOf(keys[random.nextInt(keys.length)]);
            println("Print the definition of \"" + card + "\":");
            String definition = inputString();
            if (Objects.equals(definition, flashCards.get(card))) {
                println("Correct!");
            } else if (!flashCards.containsValue(definition)) {
                println("Wrong. The right answer is \"" + flashCards.get(card) + "\"");
//                for count mistakes
                mistakesInFlashCards.put(card, mistakesInFlashCards.get(card) + 1);
            } else {
                println("Wrong. The right answer is \"" + flashCards.get(card) + "\", but your definition is correct for "
                        + "\"" + getKeyByDefinition(definition) + "\".");
                mistakesInFlashCards.put(card, mistakesInFlashCards.get(card) + 1);

            }
        }
    }

    //method removes certain card
    private static void removeCard() {
        println("The card:");
        String card = inputString();
        if (!flashCards.containsKey(card)) {
            println("Can't remove \"" + card + "\": there is no such card.");
        } else {
            flashCards.remove(card);
            mistakesInFlashCards.remove(card);
            println("The card has been removed.");
        }
    }

    //    method return key for giving value
    private static String getKeyByDefinition(String value) {
        for (var entry : flashCards.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void printHardestCard() {
//   variable for find the biggest number of mistake
        int maxMistake = 0;
        int numberOfHardestCard = 0;
        for (var entry : mistakesInFlashCards.entrySet()) {
            if (entry.getValue() > maxMistake) {
                maxMistake = entry.getValue();
                numberOfHardestCard = 1;
            } else if (entry.getValue() == maxMistake) {
                numberOfHardestCard++;
            }

        }
        if (maxMistake == 0) {
            println("There are no cards with errors.");
            return;
        }
        StringBuilder str = new StringBuilder();
        boolean isItTheFirstCard = true;
        if (numberOfHardestCard == 1) {
            str.append("The hardest card is ");
        } else {
            str.append("The hardest cards are ");
        }
        for (var entry : mistakesInFlashCards.entrySet()) {

            if (entry.getValue() == maxMistake) {
                if (!isItTheFirstCard) {
                    str.append(", ");
                }
                str.append("\"" + entry.getKey() + "\"");

                isItTheFirstCard = false;
            }
        }
        str.append(".");
        if (numberOfHardestCard == 1) {
            str.append(" You have " + maxMistake + " error" + (maxMistake == 1 ? " " : "s ") + "answering it.");
        } else {
            str.append(" You have " + maxMistake + " error" + (maxMistake == 1 ? " " : "s ") + "answering them.");
        }
        println(str.toString());
    }

    // method for adding cards
    private static void addCard() {
        println("The card:");
        String card = inputString();
        if (flashCards.containsKey(card)) {
            println("The card \"" + card + "\" already exists.");
            return;
        }
        println("The definition of the card:");
        String definition = inputString();

        if (flashCards.containsValue(definition)) {
            println("The definition \"" + definition + "\" already exists.");
            return;
        }
        flashCards.put(card, definition);
        mistakesInFlashCards.put(card, 0);
        println("The pair (\"" + card + ":\"" + definition + "\") has been added.");
    }

    // save cards to the file
//    but first it makes buffer map from information from the file
//    first method write key
//    then definition and number of mistake
    private static void exportCardsInRunTime() {
        println("File name:");
        filePath = inputString();
        exportCards(filePath);
    }

    // method for export cards
    private static void exportCards(String filePath) {
        try (PrintWriter printWriter = new PrintWriter(new File(filePath))) {
            boolean isItFirstTimeWhenIWriteTo = true;
//                variable for number of card which will be loaded
            int numberOfCard = 0;
            for (var entry : flashCards.entrySet()) {
// we need save cards in any case even if file contains some cards
                if (!isItFirstTimeWhenIWriteTo) {
                    printWriter.write("\n");
                }
                printWriter.write(entry.getKey() + "\n");
                printWriter.write(entry.getValue() + "\n");
                printWriter.write(String.valueOf(mistakesInFlashCards.get(entry.getKey())));
                numberOfCard++;
                isItFirstTimeWhenIWriteTo = false;
            }
            println(numberOfCard + " cards have been saved.");
            flashCards.clear();
        } catch (FileNotFoundException e) {
            println("File not found. Export");
        }

    }

    //    import card in runtime
    private static void importCardsInRunTime() {
        println("File name:");
        filePath = inputString();
        importCards(filePath);
    }

    //import cards, definitions and number of mistake from some file
    private static void importCards(String filePath) {
        bufferMapForImport.clear();
        bufferMapForMistakes.clear();
        File file = new File(filePath);
        try (Scanner fileScanner = new Scanner(file)) {
            boolean isTurnOfKey = true;
            String key = "";
            String value; // number of mistake for certain key
            int numberOfMistake;
            while (fileScanner.hasNextLine()) {
                if (isTurnOfKey) {
                    isTurnOfKey = false;
                    key = fileScanner.nextLine();
                } else {
                    isTurnOfKey = true;
                    value = fileScanner.nextLine();
                    numberOfMistake = Integer.parseInt(fileScanner.nextLine());
                    bufferMapForImport.put(key, value);
                    bufferMapForMistakes.put(key, numberOfMistake);

                }
            }
        } catch (FileNotFoundException fnf) {
            println("File not found. File: " + filePath);
        }
//            variable contains number of imported card
        int numberOfCard = 0;
//            import cards and definition
        for (var entry : bufferMapForImport.entrySet()) {
            if (!flashCards.containsKey(entry.getKey())) {
                flashCards.put(entry.getKey(), entry.getValue());
                ++numberOfCard;
            } else if (!Objects.equals(entry.getValue(), flashCards.get(entry.getKey()))) {
                ++numberOfCard;
                flashCards.put(entry.getKey(), entry.getValue());
            }
        }
//            import cards and number of mistakes
        for (var entry : bufferMapForMistakes.entrySet()) {
            if (!mistakesInFlashCards.containsKey(entry.getKey())) {
                mistakesInFlashCards.put(entry.getKey(), entry.getValue());
            } else if (!Objects.equals(entry.getValue(), mistakesInFlashCards.get(entry.getKey()))) {
                mistakesInFlashCards.put(entry.getKey(), entry.getValue());
            }
        }
        println(numberOfCard + " cards have been loaded.");
    }

}
