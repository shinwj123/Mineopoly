package test;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.game.GameEngine;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.MinePlayerStrategy;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.strategy.PlayerStrategy;
import mineopoly_three.strategy.RandomStrategy;
import mineopoly_three.tiles.TileType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MineopolyTest {
    private ItemType[] itemTypes = new ItemType[]{ItemType.DIAMOND,ItemType.EMERALD,ItemType.RUBY,ItemType.AUTOMINER};
    private Economy economy = new Economy(itemTypes);
    private MinePlayerStrategy yourStrategy = new PlayerStrategy();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    //win percentage testing
    @Test
    public void testWinPercentageSize14() {
        final int numTotalRounds = 1000;
        int numRoundsWonByMinScore = 0;
        MinePlayerStrategy randomStrategy = new RandomStrategy();
        GameEngine gameEngine;

        for (int i = 0; i < numTotalRounds; i++) {
            long randomSeed = System.currentTimeMillis();
            gameEngine = new GameEngine(14, yourStrategy, randomStrategy, randomSeed);
            gameEngine.setGuiEnabled(false);
            gameEngine.runGame();

            if (gameEngine.getRedPlayer().getScore() > gameEngine.getMinScoreToWin()) {
                numRoundsWonByMinScore++;
            }
        }


        double winPercentage = ((double) numRoundsWonByMinScore) / numTotalRounds;
        assertTrue(winPercentage >= 0.99);
    }

    @Test
    public void testWinPercentageSize20() {
        final int numTotalRounds = 1000;
        int numRoundsWonByMinScore = 0;
        MinePlayerStrategy randomStrategy = new RandomStrategy();
        GameEngine gameEngine;

        for (int i = 0; i < numTotalRounds; i++) {
            long randomSeed = System.currentTimeMillis();
            gameEngine = new GameEngine(20, yourStrategy, randomStrategy, randomSeed);
            gameEngine.setGuiEnabled(false);
            gameEngine.runGame();

            if (gameEngine.getRedPlayer().getScore() > gameEngine.getMinScoreToWin()) {
                numRoundsWonByMinScore++;
            }
        }


        double winPercentage = ((double) numRoundsWonByMinScore) / numTotalRounds;
        assertTrue(winPercentage >= 0.99);
    }

    @Test
    public void testWinPercentageSize26() {
        final int numTotalRounds = 1000;
        int numRoundsWonByMinScore = 0;
        MinePlayerStrategy randomStrategy = new RandomStrategy();
        GameEngine gameEngine;

        for (int i = 0; i < numTotalRounds; i++) {
            long randomSeed = System.currentTimeMillis();
            gameEngine = new GameEngine(26, yourStrategy, randomStrategy, randomSeed);
            gameEngine.setGuiEnabled(false);
            gameEngine.runGame();

            if (gameEngine.getRedPlayer().getScore() > gameEngine.getMinScoreToWin()) {
                numRoundsWonByMinScore++;
            }
        }


        double winPercentage = ((double) numRoundsWonByMinScore) / numTotalRounds;
        assertTrue(winPercentage >= 0.99);
    }

    @Test
    public void testWinPercentageSize32() {
        final int numTotalRounds = 1000;
        int numRoundsWonByMinScore = 0;
        MinePlayerStrategy randomStrategy = new RandomStrategy();
        GameEngine gameEngine;

        for (int i = 0; i < numTotalRounds; i++) {
            long randomSeed = System.currentTimeMillis();
            gameEngine = new GameEngine(32, yourStrategy, randomStrategy, randomSeed);
            gameEngine.setGuiEnabled(false);
            gameEngine.runGame();

            if (gameEngine.getRedPlayer().getScore() > gameEngine.getMinScoreToWin()) {
                numRoundsWonByMinScore++;
            }
        }


        double winPercentage = ((double) numRoundsWonByMinScore) / numTotalRounds;
        assertTrue(winPercentage >= 0.99);
    }

    @Test
    public void testNearestchargingStation14() {
        yourStrategy.initialize(14,7, 100,30000, null,
                new Point(1,0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}
        };
        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(1,0), new Point(0,0), 0);
        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView,economy,1, true));
    }

    @Test
    public void testNearestchargingStation20() {
        yourStrategy.initialize(20,7, 100,30000, null,
                new Point(1,0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}
        };
        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(1,0), new Point(0,0), 0);
        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView,economy,1, true));
    }

    @Test
    public void testNearestchargingStation26() {
        yourStrategy.initialize(26,7, 100,30000, null,
                new Point(1,0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}
        };
        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(1,0), new Point(0,0), 0);
        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView,economy,1, true));
    }

    @Test
    public void testNearestchargingStation32() {
        yourStrategy.initialize(32,7, 100,30000, null,
                new Point(1,0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY}
        };
        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(), new Point(1,0), new Point(0,0), 0);
        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView,economy,1, true));
    }

    @Test
    public void testPlayerStandingOnMineral14() {
        yourStrategy.initialize(14, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MINE, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerStandingOnMineral20() {
        yourStrategy.initialize(20, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MINE, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerStandingOnMineral26() {
        yourStrategy.initialize(26, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MINE, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerStandingOnMineral32() {
        yourStrategy.initialize(32, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MINE, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerInventoryFull14() {
        yourStrategy.initialize(14, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        InventoryItem item = new InventoryItem(ItemType.RUBY);
        for (int i = 0; i < 5; i++) {
            yourStrategy.onReceiveItem(item);
        }

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerInventoryFull20() {
        yourStrategy.initialize(20, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        InventoryItem item = new InventoryItem(ItemType.RUBY);
        for (int i = 0; i < 5; i++) {
            yourStrategy.onReceiveItem(item);
        }

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerInventoryFull26() {
        yourStrategy.initialize(26, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        InventoryItem item = new InventoryItem(ItemType.RUBY);
        for (int i = 0; i < 5; i++) {
            yourStrategy.onReceiveItem(item);
        }

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @Test
    public void testPlayerInventoryFull32() {
        yourStrategy.initialize(32, 7, 100, 30000, null,
                new Point(1, 0), true, null);

        InventoryItem item = new InventoryItem(ItemType.RUBY);
        for (int i = 0; i < 5; i++) {
            yourStrategy.onReceiveItem(item);
        }

        TileType[][] boardTileTypes = new TileType[][]{
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.RECHARGE, TileType.RECHARGE, TileType.EMPTY},
                {TileType.EMPTY, TileType.EMPTY, TileType.EMPTY, TileType.RESOURCE_DIAMOND}
        };

        PlayerBoardView boardView = new PlayerBoardView(boardTileTypes, new HashMap<>(),
                new Point(3, 0), new Point(0, 0), 0);

        assertEquals(TurnAction.MOVE_UP, yourStrategy.getTurnAction(boardView, economy, 100, true));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
}
