package com.example.kniffel.persistence;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import kniffel.KniffelFacade;
import kniffel.KniffelFacadeFactory;
import kniffel.data.ScoreTable;
import kniffel.data.ScoreTableRows;
import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;

public class SaveDataPersistenceTest {

    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void deleteTestFileExists() throws IOException {
        final String name = "testFile.sav";
        File path = tempFolder.newFolder();
        SaveDataPersistence persistence = new SaveDataPersistence(path);

        File file = new File(path, name);
        file.createNewFile();

        //File was properly created
        assert(file.exists());

        //Deletion was successful
        assert(persistence.deleteGame(name));

        //File was properly deleted
        assert(!file.exists());
    }

    @Test
    public void deleteTestFileDoesntExist() throws IOException {
        final String name = "testFile.sav";
        File path = tempFolder.newFolder();
        SaveDataPersistence persistence = new SaveDataPersistence(path);

        //Assert deletion was not successful
        assert(!persistence.deleteGame(name));
    }

    @Test
    public void getAllFileNamesTest() throws IOException {
        final String name1 = "testFile1.sav";
        final String name2 = "testFile2.sav";
        final String name3 = "testFile3.sav";
        File path = tempFolder.newFolder();
        SaveDataPersistence persistence = new SaveDataPersistence(path);

        File file;
        file = new File(path, name1);
        file.createNewFile();
        file = new File(path, name2);
        file.createNewFile();
        file = new File(path, name3);
        file.createNewFile();

        String[] filenames = persistence.getListofSavedGames();

        List<String> list = Arrays.asList(filenames);

        assert(list.contains(name1));
        assert(list.contains(name2));
        assert(list.contains(name3));
    }

    @Test
    public void getAllFileNamesEmptyDirectoryTest() throws IOException {
        File path = tempFolder.newFolder();
        SaveDataPersistence persistence = new SaveDataPersistence(path);

        String[] filenames = persistence.getListofSavedGames();

        assert(filenames.length == 0);
    }

    @Test
    public void SaveAndLoadTest() throws IOException, IllegalStateException, KniffelException, ClassNotFoundException {
        File path = tempFolder.newFolder();
        SaveDataPersistence persistence = new SaveDataPersistence(path);

        int numberOfPlayers = 3;
        String[] playerNames =  {"Karl", "Bob", "Hans"};
        int ownPlayerID = 1;
        KniffelFacade facade = KniffelFacadeFactory.produceKniffelFacade(numberOfPlayers, playerNames, ownPlayerID, new DataOutputStream[] {new DataOutputStream(new ByteArrayOutputStream()), new DataOutputStream(new ByteArrayOutputStream())},new DataInputStream[] {new DataInputStream(new ByteArrayInputStream(new byte[0])), new DataInputStream(new ByteArrayInputStream(new byte[0]))});

        facade.rollDice();
        facade.endTurn(ScoreTableRows.CHANCE);

        String saveName = "testSave";

        persistence.saveGame(facade, saveName);

        ScoreTable loadedTable = persistence.loadGame(saveName);

        //Check if values match up
        assert(loadedTable.getScore(ScoreTableRows.CHANCE, ownPlayerID) > 4);
        assert(Arrays.equals(playerNames, loadedTable.getPlayerNames()));
        assert(ownPlayerID == loadedTable.getOwnPlayerID());
        assert(numberOfPlayers == loadedTable.getNumberOfPlayers());
    }
}
