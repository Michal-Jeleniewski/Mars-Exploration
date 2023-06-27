package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codecool.marsexploration.mapexplorer.commandCenter.BaseStatus.*;

public class CommandCenter {
    private static int numberOfBases = 1;
    private final String id;

    private final Coordinate commandCenterPosition;

    private BaseStatus baseStatus;

    private List<Coordinate> mineralPoints;

    private int mineralsOnStock;

    public CommandCenter(Coordinate commandCenterPosition, List<Coordinate> mineralPoints) {
        this.mineralPoints = mineralPoints;
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
}
