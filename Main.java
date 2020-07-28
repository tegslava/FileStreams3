import filestreams.GameProgress;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Tegneryadnov_VI
 * @version 31
 * @Java Загрузка
 */

public class Main {
    private final static StringBuilder log = new StringBuilder();

    public static void main(String[] args) {
        final File storageFolder = new File("C:/temp/Game/savegames");
        try {
            openZip(String.format("%s\\zip.zip", storageFolder.getPath()), storageFolder.getPath());
            toLog(String.format("Разархивирован файл %s\\zip.zip", storageFolder.getPath()));
            for (File file : storageFolder.listFiles()) {
                if (file.getName().contains(".dat")) {
                    GameProgress gameProgress = openProgress(file.getAbsolutePath());
                    toLog(String.format("Состояние игры из файла %s", file.getAbsolutePath()));
                    toLog(gameProgress.toString());
                    System.out.println();
                }
            }
        } finally {
            logToFile("C:/temp/Game/temp/temp.txt");
        }
    }

    private static void openZip(String archFileName, String deployFolderName) {
        try (ZipInputStream zin = new ZipInputStream(new
                FileInputStream(archFileName))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName(); // получим название файла
                // распаковка
                FileOutputStream fout = new FileOutputStream(String.format("%s\\%s", deployFolderName, name));
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            toLog(ex.getMessage());
        }
    }

    private static GameProgress openProgress(String storageFileName) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(storageFileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // десериализуем объект и скастим его в класс
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }

    public static void toLog(String message) {
        System.out.println(message);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        log.append(String.format("%s: %s\n", LocalDateTime.now().format(formatter), message));
    }

    public static void logToFile(String fileName) {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(log.toString());
        } catch (IOException ex) {
            System.out.printf("Ошибка сохранения лога %s:", ex.getStackTrace());
        }
    }

}
