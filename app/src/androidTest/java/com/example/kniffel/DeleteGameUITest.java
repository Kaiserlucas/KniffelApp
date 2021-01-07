package com.example.kniffel;

import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.kniffel.GUI.activities.MainActivity;
import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.persistence.SaveDataPersistence;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import kniffel.KniffelFacade;
import kniffel.KniffelFacadeFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DeleteGameUITest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void deleteSavedGame() throws IOException, ClassNotFoundException {
        String testName = "testName";
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SaveDataPersistence persistence = new SaveDataPersistence(appContext.getFilesDir());

        //Create dummy facade and save it to a file
        KniffelFacade facade = KniffelFacadeFactory.produceKniffelFacade(1,new String[0],1,
                new DataOutputStream[] {new DataOutputStream(new ByteArrayOutputStream())},
                new DataInputStream[] {new DataInputStream(new ByteArrayInputStream(new byte[0]))});
        persistence.saveGame(facade, testName);

        //Savegame should exist, therefore no exception should be thrown
        persistence.loadGame(testName);

        //Open savegame deletion menu and delete the test game
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withId(R.id.delete_game_recycler_view))
                .check(matches(hasDescendant(withText(testName)))).perform(click());

        try {
            persistence.loadGame(testName);
            fail();
        } catch(FileNotFoundException e) {
            //If the save file was deleted successfully this exception should be thrown
        }

    }
}