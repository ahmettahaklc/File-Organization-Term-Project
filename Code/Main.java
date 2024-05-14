import java.io.*;
import java.util.*;

public class Main {

    static HashSet<String> passwordHash = new HashSet<>();
    static HashMap<Integer, Long> lineCount = new HashMap<>();
    static HashMap<String, BufferedWriter> indexWriters = new HashMap<>();

    public static void main(String[] args) throws Exception {
        createFolder("Index");
        createFolder("Processed");

        File file = createFile("Processed/passwords.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

        passwordHash = new HashSet<>(bufferedReader.lines().toList());

        List<String> indexFolders = textFiles("Index");
        for (String folder : indexFolders) {
            List<String> indexFiles = textFiles("Index/" + folder);
            for (String iFile : indexFiles) {
                int letter = Integer.parseInt(iFile);
                lineCount.put(letter, lineCount.getOrDefault(letter, 0L) + calculateLineNumber(iFile));
            }
        }

        List<String> filesName = textFiles("Unprocessed-Passwords");
        for (String fileName : filesName) {
            BufferedReader br = new BufferedReader(new FileReader("Unprocessed-Passwords/" + fileName));

            String line;
            while ((line = br.readLine()) != null) {
                int letter = line.charAt(0);
                int fileNum = (int) (lineCount.getOrDefault(letter, 0L) / 10000);
                createFile("Index/" + letter + "/" + fileNum + ".txt");
                if (passwordHash.add(line)) {
                    lineCount.put(letter, lineCount.getOrDefault(letter, 0L) + 1);
                    appendToFile(line, bufferedWriter);
                    appendToFile(new Password(line).toString(), letter, fileNum);
                }
            }
        }


        Scanner scanner = new Scanner(System.in);

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
        List<String> textFile = new ArrayList<>();
        File dir = new File(directory);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            textFile.add(file.getName());
        }
        return textFile;
    }


    public static void appendToFile(String content, int letter, int fileNum) throws IOException {
        if (!indexWriters.containsKey(letter + "|" + fileNum)) {
            File indexFile = new File("Index/" + letter + "/" + fileNum + ".txt");
            indexWriters.put(letter + "|" + fileNum, new BufferedWriter(new FileWriter(indexFile)));
        }
        appendToFile(content, indexWriters.get(letter + "|" + fileNum));
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
