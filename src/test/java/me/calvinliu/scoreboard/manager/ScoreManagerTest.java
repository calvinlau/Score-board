/*
 * Creator: Calvin Liu
 */
package me.calvinliu.scoreboard.manager;

import me.calvinliu.scoreboard.model.UserScore;
import me.calvinliu.scoreboard.util.TestUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

//@RunWith(MultiThreadedRunner.class)
public class ScoreManagerTest {

    public static final int DEFAULT_LIMIT = PropertiesManager.getInstance().getHighScoresLimit();
    private ScoreManager scoreManager;


    @Before
    public void setUp() {
        scoreManager = ScoreManager.getInstance();
    }

    @Test
    public void testSingletonInstance() {
        assertSame(ScoreManager.getInstance(), ScoreManager.getInstance());
    }

    @Test
    public void testPostScore_SameLevel_DiffUser() {
        // Add a level with a user score before the call
        Integer levelId = TestUtils.getRandomLevelId();
        UserScore oldUserScore = new UserScore(TestUtils.getRandomUserId(), TestUtils.getRandomScore());
        scoreManager.postScore(levelId, oldUserScore);
        // Call with a new user score
        UserScore newUserScore = new UserScore(TestUtils.getRandomUserId(), TestUtils.getRandomScore());
        scoreManager.postScore(levelId, newUserScore);

        // Asserts that given levelId is contained in the set
        assertTrue(scoreManager.getUserScores().containsKey(levelId));
        // Asserts that the given and returned set objects are the same
        NavigableSet<UserScore> set = scoreManager.getUserScores().get(levelId);
        // Assert that all the given user scores still exist in the set
        assertTrue(set.contains(oldUserScore));
        assertTrue(set.contains(newUserScore));
        // Assert High Score List
        assertHighScoreList(levelId, DEFAULT_LIMIT, 2);

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testPostScore_SameLevel_SameUser() {
        // Add a level with a user score before the call
        Integer levelId = TestUtils.getRandomLevelId();
        Integer userId = TestUtils.getRandomUserId();
        UserScore oldUserScore = new UserScore(userId, TestUtils.getRandomScore());
        scoreManager.postScore(levelId, oldUserScore);
        // Call with a new user score
        UserScore newUserScore = new UserScore(userId, TestUtils.getRandomScore());
        scoreManager.postScore(levelId, newUserScore);

        // Asserts that given levelId is contained in the set
        assertTrue(scoreManager.getUserScores().containsKey(levelId));
        // Assert High Score List
        assertHighScoreList(levelId, DEFAULT_LIMIT, 1);

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testPostScore_LevelNotExisted() {
        Integer levelId = TestUtils.getRandomLevelId();
        UserScore userScore = new UserScore(TestUtils.getRandomUserId(), TestUtils.getRandomScore());
        scoreManager.postScore(levelId, userScore);
        assertTrue(scoreManager.getUserScores().containsKey(levelId));

        assertOrder(scoreManager.getHighScoreList(levelId, DEFAULT_LIMIT));
        scoreManager.getUserScores().clear();
    }

    @Test
    public void testPostScoreAddUserScoresToOneExistedLevelAndManyOtherNonExistedLevelsMultipleTreads() {
        int NUMBER_OF_EXISTED_LEVELS = 1;
        int NUMBER_OF_EXISTED_SCORES = 5;
        int NUMBER_OF_NEW_SCORES_IN_EXISTED_LEVEL = 5;
        int NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL = 5;

        Integer existedLevelId = TestUtils.getRandomLevelId();
        UserScore[] existedUserScores = new UserScore[NUMBER_OF_EXISTED_SCORES];

        // temp test map
        Map<Integer, UserScore> mapValues = new HashMap<>();

        // add a level with 5 scores
        ConcurrentSkipListSet<UserScore> existedConcurrentSkipListSet = new ConcurrentSkipListSet<>();
        for (int i = 0; i < NUMBER_OF_EXISTED_SCORES; i++) {
            existedUserScores[i] = new UserScore(TestUtils.getRandomUserId(), TestUtils.getRandomScore());
            // Stores values for later testing
            mapValues.put(existedLevelId, existedUserScores[i]);
            // Stores values in the existed set
            existedConcurrentSkipListSet.add(existedUserScores[i]);
        }
        // Put the set in the map
        scoreManager.getUserScores().put(existedLevelId, existedConcurrentSkipListSet);

        // threads run in parallel and do the call
        runThreads(mapValues, NUMBER_OF_NEW_SCORES_IN_EXISTED_LEVEL, NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL, existedLevelId);

        // Assert that all levels are contained in the map associated with the correct user scores
        for (Integer levelId : mapValues.keySet()) {
            assertTrue("All keys are contained in the map", scoreManager.getUserScores().containsKey(levelId));
            assertTrue("All user-scores are contained in the map", scoreManager.getUserScores().get(levelId).contains(mapValues.get(levelId)));
        }

        // Assert that the map has the correct size
        assertEquals("Map has the correct size", NUMBER_OF_EXISTED_LEVELS + NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL, scoreManager.getUserScores().size());

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testPostScoreLevelsNotExistedMultipleTreads() {
        int NUMBER_OF_THREADS = 10;
        Map<Integer, UserScore> mapValues = new HashMap<>();

        // Initiates and runs threads in parallel
        ScoreManagerThread[] threads = runThreads(mapValues, NUMBER_OF_THREADS, 0, null);

        // Assert that all levels are contained in the map associated with the correct user scores
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            assertTrue("Map contains key", scoreManager.getUserScores().containsKey(threads[i].getLevelId()));
            assertTrue("Key is associated with the correct user score", scoreManager.getUserScores().get(threads[i].getLevelId()).contains(mapValues.get(threads[i].getLevelId())));
        }

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testGetHighScoreListLevelHasScoresMoreThanLimit() {
        //do assertion for the limit and response
        Integer levelId = TestUtils.getRandomLevelId();
        int SCORES_LIMIT = 10;
        int ADDED_SCORES = 15000;

        // add more user scores than the limit
        Map<Integer, Integer> mapValues = new HashMap<>();
        postScores(mapValues, levelId, ADDED_SCORES, false);

        String highScoreList = scoreManager.getHighScoreList(levelId, SCORES_LIMIT);

        assertOrder(highScoreList);

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testGetHighScoreListLevelHasScoresWithSameUserId() {
        //do assertion for the limit and response
        Integer levelId = TestUtils.getRandomLevelId();
        int SCORES_LIMIT = 10;
        int ADDED_SCORES = 15;

        // add more user scores than the limit
        Map<Integer, Integer> mapValues = new HashMap<>();

        postScores(mapValues, levelId, ADDED_SCORES, true);
        postScores(mapValues, levelId, ADDED_SCORES, true);

        String highScoreList = scoreManager.getHighScoreList(levelId, SCORES_LIMIT);
        assertOrder(highScoreList);

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testGetHighScoreListLevelHasScoresLessThanLimit() {
        Integer levelId = TestUtils.getRandomLevelId();
        int SCORES_LIMIT = 10;
        int ADDED_SCORES = 5;

        // add more user scores than the limit
        Map<Integer, Integer> mapValues = new HashMap<>();
        postScores(mapValues, levelId, ADDED_SCORES, false);

        String highScoreList = scoreManager.getHighScoreList(levelId, SCORES_LIMIT);

        for (Integer userId : mapValues.keySet()) {
            Integer score = mapValues.get(userId);
            assertTrue("Each userId-score exists in the string", highScoreList.contains(userId + "=" + score));
        }

        assertEquals("HighScoreList string size", ADDED_SCORES, highScoreList.split(",").length);

        String[] subStrings = highScoreList.split(",");
        for (int i = 0; i < subStrings.length - 1; i++) {
            assertTrue("Pair score ordering ascending", Integer.parseInt(subStrings[i].split("=")[1]) > Integer.parseInt(subStrings[i + 1].split("=")[1]));
        }

        scoreManager.getUserScores().clear();
    }

    @Test
    public void testGetHighScoreListLevelHasNoScores() {
        Integer levelId = TestUtils.getRandomLevelId();
        assertEquals("", scoreManager.getHighScoreList(levelId, DEFAULT_LIMIT));
    }

    /**
     * Adds a number of user scores to a specific levelId and stores the values in a temporary given map
     *
     * @param mapValues    map Values
     * @param levelId      level Id
     * @param ADDED_SCORES ADDED_SCORES
     */
    public void postScores(Map<Integer, Integer> mapValues, Integer levelId, final int ADDED_SCORES, boolean sameUserId) {
        int userId = TestUtils.getRandomUserId();
        for (int i = 0; i < ADDED_SCORES; i++) {
            userId = sameUserId ? userId : TestUtils.getRandomUserId();
            UserScore userScore = new UserScore(userId, TestUtils.getRandomScore());
            mapValues.put(userScore.getUserId(), userScore.getScore().get());
            scoreManager.postScore(levelId, userScore);
        }
    }

    private ScoreManagerThread[] runThreads(Map<Integer, UserScore> mapValues, final int NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL, final int NUMBER_OF_NEW_SCORES_IN_EXISTED_LEVEL, Integer existedLevelId) {
        ScoreManagerThread[] threads = new ScoreManagerThread[NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL + NUMBER_OF_NEW_SCORES_IN_EXISTED_LEVEL];

        startThreads(mapValues, threads, NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL, null);
        startThreads(mapValues, threads, NUMBER_OF_NEW_SCORES_IN_EXISTED_LEVEL, existedLevelId);

        try {
            for (int i = 0; i < NUMBER_OF_NEW_SCORES_IN_NON_EXISTED_LEVEL; i++) {
                threads[i].join();
            }
        } catch (InterruptedException ie) {
            System.err.println(ie.getCause());
        }
        return threads;
    }

    /**
     * Starts the threads.
     * If the existedLevelId is null, it generates random level ids
     */
    private void startThreads(Map<Integer, UserScore> mapValues, ScoreManagerThread[] threads, final int NUMBER_OF_SCORES, Integer existedLevelId) {
        for (int i = 0; i < NUMBER_OF_SCORES; i++) {
            Integer levelId = existedLevelId != null ? existedLevelId : TestUtils.getRandomLevelId();
            UserScore userScore = new UserScore(TestUtils.getRandomUserId(), TestUtils.getRandomScore());
            // Store values for later testing
            mapValues.put(levelId, userScore);

            // Create thread
            threads[i] = new ScoreManagerThread(scoreManager, levelId, userScore);
            threads[i].start();
        }
    }

    private void assertHighScoreList(Integer levelId, int limit, int size) {
        String highScoreList = scoreManager.getHighScoreList(levelId, limit);
        assertSize(highScoreList, size);
        assertOrder(highScoreList);
    }

    private void assertOrder(String highScoreList) {
        if (highScoreList == null) {
            return;
        }
        String[] scores = highScoreList.split(",");
        for (int i = 0; i < scores.length - 1; i++) {
            assertTrue("Assert Ordering ascending", Integer.parseInt(scores[i].split("=")[1]) > Integer.parseInt(scores[i + 1].split("=")[1]));
        }
        System.out.println(highScoreList);
    }

    private void assertSize(String highScoreList, int target) {
        assertEquals("Assert Size", highScoreList.split(",").length, target);
    }
}