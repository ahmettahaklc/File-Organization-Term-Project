import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        createFolder("Index");
        createFolder("Unprocessed-Passwords");
        createFolder("Processed");
        createFile("Processed/passwords.txt");

        HashSet<String> passwordHash = new HashSet<>();
        List<String> filesName = textFiles("Unprocessed-Passwords");
        String line;
        HashMap<Integer, BufferedReader> passwordMap = new HashMap<>();
        Scanner scanner = new Scanner(System.in);


            BufferedReader br = new BufferedReader(new FileReader("Processed/passwords.txt"));
            while((line=br.readLine())!=null){
                int letter = line.charAt(0);
                BufferedReader bufferedReader = new BufferedReader(new FileReader("Index/"+letter));
                passwordMap.put(letter,bufferedReader);
            }

            System.out.println("Please enter the password what you want to search");
            String searchPassword = scanner.nextLine();

            int key = searchPassword.charAt(0);
            BufferedReader brs = passwordMap.get(key);

            while((line=brs.readLine()).equals(searchPassword)){

            }
        /*
        ----this loop for write on Processed folder----
        for (String fileName : filesName) {
            BufferedReader br = new BufferedReader(new FileReader("Unprocessed-Passwords/" + fileName));
            while ((line = br.readLine()) != null) {
                if (!passwordHash.contains(line)) {
                    passwordHash.add(line);
                    appendToFile("Processed/passwords.txt", line);
                }
                System.out.println("King Hakan");
            }
        }


        ----this loop for create Index Folder----
        for(String fileName:filesName){
            BufferedReader br = new BufferedReader(new FileReader("Unprocessed-Passwords/"+fileName));
            while((line=br.readLine())!=null){
                int letter = line.charAt(0);
                String folderName = "Index/" + letter;
                createFolder(folderName);
                createFile(folderName+"/passwords.txt");
            }
        }

        Password password = new Password("");
        BufferedReader br = new BufferedReader(new FileReader("Processed/passwords.txt"));
        while((line=br.readLine())!=null){
            password.setPassword(line);
            int letter = line.charAt(0);
            String pathFile = "Index/"+letter+"/passwords.txt";
            appendToFile(pathFile,password.toString());
        }
        */

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
        List<String> textFiles = new ArrayList<String>();
        File dir = new File(directory);
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith((".txt"))) {
                textFiles.add(file.getName());
            }
        }
        return textFiles;
    }

    public static void appendToFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.append(content);
        writer.newLine();
        writer.close();
    }


}
