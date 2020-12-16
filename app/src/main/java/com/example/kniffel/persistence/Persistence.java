
package com.example.kniffel.persistence;

import java.io.File;
import java.io.IOException;

import kniffel.KniffelFacade;
import kniffel.data.ScoreTable;

public interface Persistence {

    /**
     * Saves the current state of the game in a file
     * Any rolls since the last turn change are lost
     * @param facade Facade of the game that is supposed to be saved
     * @param saveName Name of the file that is going to be created. Can't be equal to an already existing file
     */
    public boolean saveGame(KniffelFacade facade, String saveName) throws IOException;

    /**
     * Reads arguments of a saved game from a file and returns a facade using those arguments
     * @return Facade of the saved game
     */
    public ScoreTable loadGame(String saveName) throws IOException, ClassNotFoundException;

    /**
     * Deletes the specified saved game
     * @param savename Name of the file that is supposed to be deleted
     */
    public boolean deleteGame(String savename) throws IOException;

    /**
     * Returns the names of all saved games
     * @return names of saved games
     */
    public String[] getListofSavedGames() throws IOException;
}

