import java.io.*;
import java.util.*;

public class Main {

    static HashSet<String> passwordHash = new HashSet<>();
    static HashMap<Integer, Long> lineCount = new HashMap<>();
    static HashMap<Integer, BufferedWriter> indexWriters = new HashMap<>();

    public static void main(String[] args) throws Exception {
        File file = createFile("Processed/passwords.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        String line;
        Scanner scanner = new Scanner(System.in);

        passwordHash = new HashSet<>(bufferedReader.lines().toList());

        List<String> filesName = textFiles("Unprocessed-Passwords");

        for (String fileName : filesName) {
            BufferedReader br = new BufferedReader(new FileReader("Unprocessed-Passwords/" + fileName));
            while ((line = br.readLine()) != null) {
                int letter = line.charAt(0);
                createFile("Index/" + letter + "/passwords.txt");
                if (!passwordHash.contains(line)) {
                    passwordHash.add(line);
                    appendToFile(line, bufferedWriter);
                    appendToFile(new Password(line).toString(), letter);
                }
            }
        }
    }

    public static File createFile(String... path) throws Exception {
        File file = new File(String.join("/", path));
        if (!file.exists()) {
            if (file.getParentFile().mkdirs() || file.createNewFile())
                return file;
            throw new Exception("file could not be created!");
        }
        return file;
    }

    public static File createFolder(String... path) throws Exception {
        File folder = new File(String.join("/", path));
        if (!folder.exists()) {
            if (folder.mkdirs())
                return folder;
            throw new Exception("folder could not be created!");
        }
        return folder;
    }

    public static List<String> textFiles(String directory) {
        List<String> textFiles = new ArrayList<>();
        File dir = new File(directory);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith((".txt"))) {
                textFiles.add(file.getName());
            }
        }
        return textFiles;
    }

    public static List<String> textIndexFiles(String directory) {
        List<String> textFiless = new ArrayList<>();
        File dir = new File(directory);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            textFiless.add(file.getName());
        }
        return textFiless;
    }


    public static void appendToFile(String content, int letter) throws IOException {
        if (!indexWriters.containsKey(letter)) {
            File indexFile = new File("Index/" + letter + "/passwords.txt");
            indexWriters.put(letter, new BufferedWriter(new FileWriter(indexFile)));
        }
        appendToFile(content, indexWriters.get(letter));
    }

    public static void appendToFile(String content, int letter, int lineCount) throws IOException {
        if (!indexWriters.containsKey(letter)) {
            File indexFile = new File("Index/" + letter + "/" + lineCount + ".txt");
            indexWriters.put(letter, new BufferedWriter(new FileWriter(indexFile)));
        }
        appendToFile(content, indexWriters.get(letter));
    }

    public static void appendToFile(String content, BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.append(content);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }


    public static long calculateLineNumber(String fileName) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(fileName))) {
            lineNumberReader.skip(Long.MAX_VALUE);
            return lineNumberReader.getLineNumber();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
