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
    private Map<ItemType, Double> mineralValues = new HashMap<>();
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

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
        this.resourcePrices = economy.getCurrentPrices();
        this.itemsOnBoard = boardView.getItemsOnGround();
        this.userLocation = boardView.getYourLocation();
        this.currentCharge = currentCharge;
        this.boardView = boardView;

        Point closestRuby = findNearestJewel(TileType.RESOURCE_RUBY);
        Point closestEmerald = findNearestJewel(TileType.RESOURCE_EMERALD);
        Point closestDiamond = findNearestJewel(TileType.RESOURCE_DIAMOND);

        Point closestDroppedRuby = findNearestDroppedMineral(ItemType.RUBY);
        Point closestDroppedEmerald = findNearestDroppedMineral(ItemType.EMERALD);
        Point closestDroppedDiamond = findNearestDroppedMineral(ItemType.DIAMOND);

        double miningScore = distancePerPrice(closestRuby, closestEmerald, closestDiamond,
                pricePerMiningTime()).getValue();
        double droppedScore = 0.0;

        TileType tileValue = distancePerPrice(closestRuby, closestEmerald, closestDiamond,
                pricePerMiningTime()).getKey().getResourceTileType();
        ItemType item = null;

        if (currentCharge/maxCharge < 0.5 && boardView.getTileTypeAtLocation(userLocation).equals(TileType.RECHARGE)) {
            return null;
        }

        if (itemCount == maxInventorySize) {
            Point closestMarket = findNearestMarket(userLocation);
            return getDirection(closestMarket);
        }

        if (!itemsOnBoard.get(userLocation).isEmpty()) {
            return TurnAction.PICK_UP_RESOURCE;
        }

        if (checkStandingOnMiningBlock()) {
            return TurnAction.MINE;
        }

        if (currentCharge <= maxCharge / 5) {
            Point closestCharger = findClosestChargingStation(userLocation);
            return getDirection(closestCharger);
        }

        if (distanceDroppedMineral(closestDroppedRuby, closestDroppedEmerald,
                closestDroppedDiamond,economy) == null) {
            if (droppedScore < miningScore) {
                return (getDirection(findNearestJewel(tileValue)));
            }
            return (getDirection(findNearestDroppedMineral(item)));
        }

        droppedScore = distanceDroppedMineral(closestDroppedRuby, closestDroppedEmerald, closestDroppedDiamond,
                economy).getValue();

        item = distanceDroppedMineral(closestDroppedRuby, closestDroppedEmerald, closestDroppedDiamond,
                economy).getKey();

        
    }

    @Override
    public void onReceiveItem(InventoryItem itemReceived) {
        itemCount++;
    }

    @Override
    public void onSoldInventory(int totalSellPrice) {
        itemCount = 0;
    }

    @Override
    public String getName() {
        return "Jeff";
    }

    @Override
    public void endRound(int totalRedPoints, int totalBluePoints) {
        itemCount = 0;
    }

    private boolean checkOutOfBounds(int boardSize, Point point) {
        boolean outOfBoundsChecker;

        outOfBoundsChecker = point.x < 0 || point.x >= boardSize || point.y < 0 || point.y >=boardSize;
        return outOfBoundsChecker;
    }

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

    private boolean checkMinimumDistanceJewel(Point mineralLocation, int minimumDistanceToJewel) {
        if (DistanceUtil.getManhattanDistance(userLocation,mineralLocation) < minimumDistanceToJewel){
            return true;
        }

        return false;
    }

    // Code below derived from:
    // https://stackoverflow.com/questions/13318733/get-closest-value-to-a-number-in-array
    private Point findClosestChargingStation(Point origin) {
        return closestChargingStation
                .stream()
                .min(Comparator.comparingInt(point ->
                        DistanceUtil.getManhattanDistance(origin, point)))
                .orElseThrow(() -> new IllegalArgumentException("There are no existing charging stations"));
    }

    // Code below derived from:
    // https://stackoverflow.com/questions/13318733/get-closest-value-to-a-number-in-array
    private Point findNearestMarket(Point origin) {
        return closestMarket
                .stream()
                .min(Comparator.comparingInt(point ->
                        DistanceUtil.getManhattanDistance(origin, point)))
                .orElseThrow(() -> new IllegalArgumentException("There are no existing markets"));
    }

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

    private boolean checkStandingOnMiningBlock() {

        if (boardView.getTileTypeAtLocation(userLocation).equals(CrackedTile.class) ||
                boardView.getTileTypeAtLocation(userLocation).equals(TileType.RESOURCE_DIAMOND) ||
                boardView.getTileTypeAtLocation(userLocation).equals(TileType.RESOURCE_EMERALD) ||
                boardView.getTileTypeAtLocation(userLocation).equals(TileType.RESOURCE_RUBY)) {
            return true;
        }

        return false;
    }

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

    private Map.Entry<ItemType, Double> distanceDroppedMineral(Point closestDroppedRuby,
                                                            Point closestDroppedEmerald, Point closestDroppedDiamond,
                                                            Economy economy) {
        double droppedRubyDistance = boardSize ^ 2;
        double droppedEmeraldDistance = boardSize ^ 2;
        double droppedDiamondDistance = boardSize ^ 2;
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

    private Map.Entry<ItemType, Double> distancePerPrice(Point closestRuby, Point closestEmerald, Point closestDiamond,
                                                               Economy economy) {
        double rubyDistance = boardSize ^ 2;
        double emeraldDistance = boardSize ^ 2;
        double diamondDistance = boardSize ^ 2;
        Map.Entry<ItemType, Double> maximum = null;


        rubyDistance = DistanceUtil.getManhattanDistance(userLocation, closestRuby);
        emeraldDistance = DistanceUtil.getManhattanDistance(userLocation, closestEmerald);
        diamondDistance = DistanceUtil.getManhattanDistance(userLocation, closestDiamond);

        double rubyValue = economy.getCurrentPrices().get(ItemType.RUBY)/rubyDistance;
        double emeraldValue = economy.getCurrentPrices().get(ItemType.EMERALD)/emeraldDistance;
        double diamondValue = economy.getCurrentPrices().get(ItemType.DIAMOND)/diamondDistance;

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
