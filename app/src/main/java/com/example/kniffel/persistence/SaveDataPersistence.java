package com.example.kniffel.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import kniffel.KniffelFacade;
import kniffel.data.ScoreTable;

public class SaveDataPersistence implements Persistence {

    private final File path;

    public SaveDataPersistence(File path) {
        this.path = path;
    }

    @Override
    public boolean saveGame(KniffelFacade facade, String saveName) throws IOException {
        File file = new File(path, saveName);
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);

        ScoreTable scoreTable = facade.getScoreTable();

        try {
            out.writeObject(scoreTable);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ScoreTable loadGame(String saveName) throws IOException, ClassNotFoundException {
        File file = new File(path, saveName);
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fileIn);

        ScoreTable scoreTable = (ScoreTable) in.readObject();
        return scoreTable;
    }

    @Override
    public boolean deleteGame(String savename) throws IOException {
        File file = new File(path, savename);
        return file.delete();
    }

    @Override
    public String[] getListofSavedGames() throws IOException {

        File folder = new File(path.getAbsolutePath());
        File[] listOfFiles = folder.listFiles();

        String[] filenames = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            filenames[i] = listOfFiles[i].getName();
        }

        return filenames;
    }

}
