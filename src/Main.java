import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Main {

    public static void saveGame (String path, GameProgress save, ArrayList<String> saves) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path))) {
            objectOutputStream.writeObject(save);
            saves.add(path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void zipFile (String path, ArrayList<String> list) {
        try (ZipOutputStream zop = new ZipOutputStream(new FileOutputStream(path))) {
            for (String savePath : list) {
                ZipEntry entry = new ZipEntry(savePath.substring(savePath.lastIndexOf("/") + 1));
                zop.putNextEntry(entry);
                try (FileInputStream fis = new FileInputStream(savePath)) {
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zop.write(buffer);
                    zop.closeEntry();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        deleteSave(list);
    }

    public static void deleteSave (ArrayList<String> saves) {
        for (String path : saves) {
            new File(path).delete();
        }
    }

    public static void openZip (String pathToFile, String pathToFolder) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pathToFile))){
            ZipEntry entry;
            String name;
            while ((entry = zis.getNextEntry()) != null) {
                name = entry.getName();
                try (FileOutputStream fos = new FileOutputStream(pathToFolder + name)) {
                    for (int i = zis.read(); i != -1; i = zis.read()) {
                        fos.write(i);
                    }
                    fos.flush();
                    zis.closeEntry();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static GameProgress openProgress (String path) {
        GameProgress gameProgress = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return gameProgress;
    }

    public static void main(String[] args) {
        ArrayList<String> saves = new ArrayList<>();

        GameProgress save1 = new GameProgress(100, 1, 1, 93.2);
        GameProgress save2 = new GameProgress(95, 2, 5, 223.7);
        GameProgress save3 = new GameProgress(15, 5, 19, 1121.2);

        saveGame("/Users/macbook/IdeaProjects/Games/savegames/save1.dat", save1, saves);
        saveGame("/Users/macbook/IdeaProjects/Games/savegames/save2.dat", save2, saves);
        saveGame("/Users/macbook/IdeaProjects/Games/savegames/save3.dat", save3, saves);

        zipFile("/Users/macbook/IdeaProjects/Games/savegames/zip.zip", saves);

        // Для разнообразия разархивировал в другую папку (не в ту, в которой был архив)
        openZip("/Users/macbook/IdeaProjects/Games/savegames/zip.zip",
                "/Users/macbook/IdeaProjects/Games/savegames/rezip/");

        System.out.println(openProgress("/Users/macbook/IdeaProjects/Games/savegames/rezip/save2.dat"));
    }
}