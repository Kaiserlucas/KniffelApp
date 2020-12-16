package com.example.kniffel.GUI.helper;

import java.util.HashMap;

import kniffel.KniffelFacade;
import kniffel.data.ScoreTable;

public class EngineStorage {

    //TODO: Change data type
    public static HashMap<String, KniffelFacade> facadeStorage = new HashMap();
    public static ScoreTable scoreTable = null;
}
