package com.example.kniffel.GUI.helper;

import java.util.HashMap;

import kniffel.KniffelFacade;
import kniffel.data.ScoreTable;

public class EngineStorage {

    public static HashMap<String, KniffelFacade> facadeStorage = new HashMap();
    public static ScoreTable scoreTable = null;
    public static boolean gameloaded = false;
}
