package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codecool.marsexploration.mapexplorer.commandCenter.BaseStatus.*;

public class CommandCenter {
    private static int numberOfBases = 1;
    private final String id;

    private final Coordinate commandCenterPosition;

    private BaseStatus baseStatus;

    private List<Coordinate> mineralPoints;

    private int mineralsOnStock;
    private final java.util.Map<String, Set<Coordinate>> objectsPoints;
    private final Set<Coordinate> scannedCoordinates;

    public CommandCenter(Coordinate commandCenterPosition, List<Coordinate> mineralPoints, Map<String, Set<Coordinate>> objectsPoints, Set<Coordinate> scannedCoordinates) {
        this.mineralPoints = mineralPoints;
        this.objectsPoints = objectsPoints;
        this.scannedCoordinates = scannedCoordinates;
        this.id = String.valueOf(numberOfBases);
        this.commandCenterPosition = commandCenterPosition;
        this.baseStatus = WAITING_FOR_RESOURCES;
        this.mineralsOnStock = 0;
        numberOfBases++;
    }

    public Coordinate getCommandCenterPosition() {
        return commandCenterPosition;
    }

    public int getMineralsOnStock() {
        return mineralsOnStock;
    }

    public void addMineral() {
        mineralsOnStock++;
    }

    public void decreaseMineralStock(int amount) {
        mineralsOnStock -= amount;
    }

    public List<Coordinate> getMineralPoints() {
        return mineralPoints;
    }

    public Map<String, Set<Coordinate>> getObjectsPoints() {
        return objectsPoints;
    }

    public Set<Coordinate> getScannedCoordinates() {
        return scannedCoordinates;
    }
}
