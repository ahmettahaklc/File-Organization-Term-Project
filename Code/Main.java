import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    static HashSet<String> passwordHash = new HashSet<>();
    static HashMap<Integer, Long> lineCount = new HashMap<>();
    static HashMap<String, BufferedWriter> indexWriters = new HashMap<>();
    static File file;
    static BufferedReader bufferedReader;
    static BufferedWriter bufferedWriter;


    public static void main(String[] args) throws Exception {

        createFolder("Index");
        createFolder("Processed");

        file = createFile("Processed/passwords.txt");
        bufferedReader = new BufferedReader(new FileReader(file));
        bufferedWriter = new BufferedWriter(new FileWriter(file,true));

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
                    appendToFile(new Password(line, fileName).toString(), letter, fileNum);
                }
            }
        }

        ArrayList<String> arrayList = new ArrayList<>(passwordHash);

        System.out.println("Please enter password");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();

        long startTime = System.nanoTime();
        String stringLine = searchAlgorithm(answer);
        long endTime = System.nanoTime();
        System.out.println("Searching time: "+((endTime-startTime)/1000000.0));
        System.out.println("Password was found: " + stringLine);

        double totalTime = 0;
        for (int i = 0; i < 1000; i++) {
            int a = ThreadLocalRandom.current().nextInt(0,arrayList.size());
            String pass = arrayList.get(a);
            long now = System.nanoTime();
            searchAlgorithm(pass);
            totalTime += System.nanoTime()-now;
        }
        System.out.println(totalTime/1000000.0/1000);
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

    public static String searchAlgorithm(String password) throws IOException {
        int letter = password.charAt(0);
        String line;
        List<String> indexTxtFiles = textFiles("Index/"+letter);
        for(String s:indexTxtFiles){
            BufferedReader reader = new BufferedReader(new FileReader("Index/"+letter+"/"+s));
            while ((line = reader.readLine())!=null){
                String[] tokens = line.split("\\|");
                if(tokens[0].equals(password)){
                    return line;
                }
            }
        }
        Password pass = new Password(password, "search");
        if (passwordHash.add(password)) {
            int fileNum = (int) (lineCount.getOrDefault(letter, 0L) / 10000);
            lineCount.put(letter, lineCount.getOrDefault(letter, 0L) + 1);
            appendToFile(password, bufferedWriter);
            appendToFile(pass.toString(), letter, fileNum);
        }
        return pass.toString();
    }
}
