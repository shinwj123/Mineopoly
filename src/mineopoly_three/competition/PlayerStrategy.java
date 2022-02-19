package mineopoly_three.competition;

import mineopoly_three.action.TurnAction;
import mineopoly_three.game.Economy;
import mineopoly_three.item.InventoryItem;
import mineopoly_three.item.ItemType;
import mineopoly_three.strategy.MinePlayerStrategy;
import mineopoly_three.strategy.PlayerBoardView;
import mineopoly_three.tiles.CrackedTile;
import mineopoly_three.tiles.TileType;
import mineopoly_three.util.DistanceUtil;

import java.awt.*;
import java.util.List;
import java.util.*;


public class PlayerStrategy implements MinePlayerStrategy {
    private Map<ItemType, Integer> resourcePrices;
    private Map<Point, List<InventoryItem>>  itemsOnBoard;
    private HashSet<Point> closestChargingStation = new HashSet<>();
    private HashSet<Point> closestMarket = new HashSet<>();
    private int boardSize;
    private int maxInventorySize;
    private Point userLocation;
    private int currentCharge;
    private int maxCharge;
    private int itemCount;
    private boolean isRedPlayer;
    private PlayerBoardView boardView;

    /**
     * Initialization of the parameters
     * @param boardSize The length and width of the square game board
     * @param maxInventorySize The maximum number of items that your player can carry at one time
     * @param maxCharge The amount of charge your robot starts with (number of tile moves before needing to recharge)
     * @param winningScore The first player to reach this score wins the round
     * @param startingBoard A view of the GameBoard at the start of the game. You can use this to pre-compute fixed
     *                       information, like the locations of market or recharge tiles
     * @param startTileLocation A Point representing your starting location in (x, y) coordinates
     *                              (0, 0) is the bottom left and (boardSize - 1, boardSize - 1) is the top right
     * @param isRedPlayer True if this strategy is the red player, false otherwise
     * @param random A random number generator, if your strategy needs random numbers you should use this.
     */
    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                           PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer, Random random) {

        if (startingBoard == null || startTileLocation == null || random == null) {
            throw new IllegalArgumentException("Initialization arguments cannot be null");
        }

        if (boardSize <= 0) {
            throw new IllegalArgumentException("Board size is non-positive");
        }

        if (maxInventorySize <= 0) {
            throw new IllegalArgumentException("Inventory size is non-positive");
        }

        if (maxCharge <= 0) {
            throw new IllegalArgumentException("Max charge is non-positive");
        }

        if (winningScore <= 0) {
            throw new IllegalArgumentException("Winning score is non-positive");
        }

        if (checkOutOfBounds(boardSize, startTileLocation)) {
            throw new IllegalArgumentException("Starting Point out of bounds");
        }

        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.maxCharge = maxCharge;
        this.isRedPlayer = isRedPlayer;
    }

    /**
     * makes a decision of the next move's turn action
     * @param boardView A PlayerBoardView object representing all the information about the board and the other player
     *                   that your strategy is allowed to access
     * @param economy The GameEngine's economy object which holds current prices for resources
     * @param currentCharge The amount of charge your robot has (number of tile moves before needing to recharge)
     * @param isRedTurn For use when two players attempt to move to the same spot on the same turn
     *                   If true: The red player will move to the spot, and the blue player will do nothing
     *                   If false: The blue player will move to the spot, and the red player will do nothing
     * @return the next move's turn action
     */
    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
        this.resourcePrices = economy.getCurrentPrices();
        this.itemsOnBoard = boardView.getItemsOnGround();
        this.userLocation = boardView.getYourLocation();
        this.boardView = boardView;

        Point closestRuby = findNearestJewel(TileType.RESOURCE_RUBY);
        Point closestEmerald = findNearestJewel(TileType.RESOURCE_EMERALD);
        Point closestDiamond = findNearestJewel(TileType.RESOURCE_DIAMOND);

        Point closestDroppedRuby = findNearestDroppedMineral(ItemType.RUBY);
        Point closestDroppedEmerald = findNearestDroppedMineral(ItemType.EMERALD);
        Point closestDroppedDiamond = findNearestDroppedMineral(ItemType.DIAMOND);

        Map.Entry<ItemType, Double> closestDistanceDroppedMineral = distanceDroppedMineral(closestDroppedRuby,
                closestDroppedEmerald, closestDroppedDiamond, economy);


        double droppedScore = 0.0;


        ItemType item = null;

        // checking if it is on charging station when low battery
        if (currentCharge/maxCharge < 0.5 && boardView.getTileTypeAtLocation(userLocation).equals(TileType.RECHARGE)) {
            return null;
        }

        // checking if the inventory has reached its maximum limit
        if (itemCount == maxInventorySize) {
            Point closestMarket = findNearestMarket(userLocation);
            return getDirection(closestMarket);
        }

        // checking if there is an item at where player is standing
        if (!itemsOnBoard.get(userLocation).isEmpty() == true) {
            return TurnAction.PICK_UP_RESOURCE;
        }

        // checking if the player is standing on the block with the mineral / resources
        if (checkStandingOnResourceBlock()) {
            return TurnAction.MINE;
        }

        // Making a decision whether to go to the nearest charger or to the nearest jewel
        if (currentCharge <= maxCharge / 5) {
            Point closestCharger = findClosestChargingStation(userLocation);
            return getDirection(closestCharger);
        }

        TileType tileValue = distancePerPrice(closestRuby, closestEmerald,
                closestDiamond, pricePerMiningTime()).getKey().getResourceTileType();

        if (closestDistanceDroppedMineral != null) {
            droppedScore = closestDistanceDroppedMineral.getValue();
            item = closestDistanceDroppedMineral.getKey();
        }

        double miningScore = distancePerPrice(closestRuby, closestEmerald,
                closestDiamond, pricePerMiningTime()).getValue();

        if (droppedScore < miningScore) {
            return (getDirection(findNearestJewel(tileValue)));
        }

        return (getDirection(findNearestDroppedMineral(item)));

    }

    /**
     * increase the count of the item when the item is received
     * @param itemReceived The item received from the player's TurnAction on their last turn
     */
    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        itemCount++;
    }

    /**
     * resets the count of the item when item is sold
     * @param totalSellPrice The combined sell price for all items in your strategy's inventory
     */
    @Override
    public void onSoldInventory(int totalSellPrice) {
        itemCount = 0;
    }

    /**
     * Gets player name
     * @return
     */
    @Override
    public String getName() {
        return "Jeff";
    }

    /**
     * reset the parameter
     * @param totalRedPoints
     * @param totalBluePoints
     */
    @Override
    public void endRound(int totalRedPoints, int totalBluePoints) {
        itemCount = 0;
    }

    /**
     * if the point is out of bounds
     * @param boardSize
     * @param point
     * @return boolean
     */
    private boolean checkOutOfBounds(int boardSize, Point point) {
        boolean outOfBoundsChecker;

        outOfBoundsChecker = point.x < 0 || point.x >= boardSize || point.y < 0 || point.y >=boardSize;
        return outOfBoundsChecker;
    }

    /**
     * Finding the jewel with the lowest distance
     * @param Jewel
     * @return nearest jewel
     */
    public Point findNearestJewel(TileType Jewel){
        int minimumDistanceToJewel = boardSize ^ 2;
        Point closestJewel = null;

        for (int boardLength = 0; boardLength < boardSize; boardLength++) {
            for (int boardHeight = 0; boardHeight < boardSize; boardHeight++) {
                Point mineralLocation = new Point(boardLength, boardHeight);
                if (boardView.getTileTypeAtLocation(mineralLocation).equals(Jewel)) {
                    if(checkMinimumDistanceJewel(mineralLocation, minimumDistanceToJewel)) {
                        DistanceUtil.getManhattanDistance(userLocation,mineralLocation);
                        closestJewel = mineralLocation;
                    }
                }
            }
        }

        return closestJewel;
    }

    /**
     * finding the mineral / resource with the lowest distance
     * @param jewel
     * @return closest mineral
     */
    private Point findNearestDroppedMineral(ItemType jewel) {
        int minimumDistanceToJewel = boardSize ^ 2;
        Point closestJewel = null;

        for (int boardLength = 0; boardLength < boardSize; boardLength++) {
            for (int boardHeight = 0; boardHeight < boardSize; boardHeight++) {
                Point mineralLocation = new Point(boardLength, boardHeight);
                if (itemsOnBoard.get(mineralLocation).contains(jewel)) {
                    if(checkMinimumDistanceJewel(mineralLocation, minimumDistanceToJewel)) {
                        DistanceUtil.getManhattanDistance(userLocation,mineralLocation);
                        closestJewel = mineralLocation;
                    }
                }
            }
        }

        return closestJewel;
    }

    /**
     * checking if the distance to the mineral / resource is the lowest
     * ----Helper Function for findNearestDroppedMineral()----
     * @param mineralLocation
     * @param minimumDistanceToJewel
     * @return boolean
     */
    private boolean checkMinimumDistanceJewel(Point mineralLocation, int minimumDistanceToJewel) {
        if (DistanceUtil.getManhattanDistance(userLocation,mineralLocation) < minimumDistanceToJewel){
            return true;
        }

        return false;
    }

    /**
     * Finding the nearest charging station
     * @param origin
     * @return nearest charging station
     */
    // Code below derived from:
    // https://stackoverflow.com/questions/13318733/get-closest-value-to-a-number-in-array
    private Point findClosestChargingStation(Point origin) {
        return closestChargingStation
                .stream()
                .min(Comparator.comparingInt(point ->
                        DistanceUtil.getManhattanDistance(origin, point)))
                .orElseThrow(() -> new IllegalArgumentException("There are no existing charging stations"));
    }

    /**
     * Finding the nearest Market
     * @param origin
     * @return nearest Market
     */
    // Code below derived from:
    // https://stackoverflow.com/questions/13318733/get-closest-value-to-a-number-in-array
    private Point findNearestMarket(Point origin) {
        return closestMarket
                .stream()
                .min(Comparator.comparingInt(point ->
                        DistanceUtil.getManhattanDistance(origin, point)))
                .orElseThrow(() -> new IllegalArgumentException("There are no existing markets"));
    }

    /**
     * getting direction to the player based on the location of the target
     * @param target
     * @return direction for the turn action
     */
    private TurnAction getDirection(Point target) {
        int xTarget = target.x;
        int yTarget = target.y;
        TurnAction turnAction = null;

        if (target == null) {
            return null;
        }

        if (xTarget > userLocation.x) {
            turnAction = TurnAction.MOVE_RIGHT;
        } else if (xTarget < userLocation.x) {
            turnAction = TurnAction.MOVE_LEFT;
        } else {
            if (yTarget > userLocation.y) {
                turnAction = TurnAction.MOVE_UP;
            } else if (yTarget < userLocation.y) {
                turnAction = TurnAction.MOVE_DOWN;
            }
        }

        return turnAction;
    }

    /**
     * check if the player is standing on the block with the mineral / resources
     * @return boolean
     */
    private boolean checkStandingOnResourceBlock() {

        if (boardView.getTileTypeAtLocation(userLocation).equals(CrackedTile.class) ||
                boardView.getTileTypeAtLocation(userLocation).equals(TileType.RESOURCE_DIAMOND) ||
                boardView.getTileTypeAtLocation(userLocation).equals(TileType.RESOURCE_EMERALD) ||
                boardView.getTileTypeAtLocation(userLocation).equals(TileType.RESOURCE_RUBY)) {
            return true;
        }

        return false;
    }

    /**
     * returns the price based on the mining time
     * @return
     */
    private Map<ItemType, Double> pricePerMiningTime() {
        double diamondPrice = resourcePrices.get(ItemType.DIAMOND)/3;
        double emeraldPrice = resourcePrices.get(ItemType.EMERALD)/2;
        double rubyPrice = resourcePrices.get(ItemType.RUBY)/1;
        Map<ItemType, Double> calculatedPrice = new HashMap<>();

        calculatedPrice.put(ItemType.DIAMOND, diamondPrice);
        calculatedPrice.put(ItemType.EMERALD, emeraldPrice);
        calculatedPrice.put(ItemType.RUBY, rubyPrice);

        return calculatedPrice;
    }

    /**
     * calculates the distance between the player and the item / resource / mineral per price
     * @param closestDroppedRuby
     * @param closestDroppedEmerald
     * @param closestDroppedDiamond
     * @param economy
     * @return the most valuable mineral / resource and its value
     */
    private Map.Entry<ItemType, Double> distanceDroppedMineral(Point closestDroppedRuby,
                                                               Point closestDroppedEmerald, Point closestDroppedDiamond,
                                                               Economy economy) {
        double droppedRubyDistance = boardSize ^ 2;
        double droppedEmeraldDistance = boardSize ^ 2;
        double droppedDiamondDistance = boardSize ^ 2;
        Map<ItemType, Double> mineralValues = new HashMap<>();
        Map.Entry<ItemType, Double> maximum = null;

        if (closestDroppedRuby == null && closestDroppedDiamond == null && closestDroppedEmerald == null) {
            return null;
        }

        droppedRubyDistance = DistanceUtil.getManhattanDistance(userLocation, closestDroppedRuby);
        droppedEmeraldDistance = DistanceUtil.getManhattanDistance(userLocation, closestDroppedEmerald);
        droppedDiamondDistance = DistanceUtil.getManhattanDistance(userLocation, closestDroppedDiamond);

        double rubyValue = economy.getCurrentPrices().get(ItemType.RUBY)/droppedRubyDistance;
        double emeraldValue = economy.getCurrentPrices().get(ItemType.EMERALD)/droppedEmeraldDistance;
        double diamondValue = economy.getCurrentPrices().get(ItemType.DIAMOND)/droppedDiamondDistance;

        mineralValues.put(ItemType.RUBY, rubyValue);
        mineralValues.put(ItemType.EMERALD, emeraldValue);
        mineralValues.put(ItemType.DIAMOND, diamondValue);


        for (Map.Entry<ItemType, Double> entry : mineralValues.entrySet()) {
            if (maximum == null || entry.getValue() > maximum.getValue()) {
                maximum = entry;
            }
        }

        return maximum;
    }

    /**
     * calculates the distance between the player and the block per price
     * @param closestRuby
     * @param closestEmerald
     * @param closestDiamond
     * @param adjustedEconomy
     * @return the most valuable block and its value
     */
    private Map.Entry<ItemType, Double> distancePerPrice(Point closestRuby, Point closestEmerald, Point closestDiamond,
                                                         Map<ItemType, Double> adjustedEconomy) {
        double rubyDistance = boardSize ^ 2;
        double emeraldDistance = boardSize ^ 2;
        double diamondDistance = boardSize ^ 2;
        Map<ItemType, Double> mineralValues = new HashMap<>();
        Map.Entry<ItemType, Double> maximum = null;


        rubyDistance = DistanceUtil.getManhattanDistance(userLocation, closestRuby);
        emeraldDistance = DistanceUtil.getManhattanDistance(userLocation, closestEmerald);
        diamondDistance = DistanceUtil.getManhattanDistance(userLocation, closestDiamond);

        double rubyValue = adjustedEconomy.get(ItemType.RUBY)/rubyDistance;
        double emeraldValue = adjustedEconomy.get(ItemType.EMERALD)/emeraldDistance;
        double diamondValue = adjustedEconomy.get(ItemType.DIAMOND)/diamondDistance;

        mineralValues.put(ItemType.RUBY, rubyValue);
        mineralValues.put(ItemType.EMERALD, emeraldValue);
        mineralValues.put(ItemType.DIAMOND, diamondValue);


        for (Map.Entry<ItemType, Double> entry : mineralValues.entrySet()) {
            if (maximum == null || entry.getValue() > maximum.getValue()) {
                maximum = entry;
            }
        }

        return maximum;
    }


}