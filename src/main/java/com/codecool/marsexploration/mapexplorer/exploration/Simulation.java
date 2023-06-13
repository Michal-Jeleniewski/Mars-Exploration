package com.codecool.marsexploration.mapexplorer.exploration;


import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.Spaceship;

import java.util.List;
import java.util.Objects;

public class Simulation {
    private int numberOfSteps;
    private final int stepsToTimeout;
    private final Rover rover;
    private final Coordinate spaceshipCoordinate;
    private final Map map;
    private final List<String> resourcesToMonitor;
    private  ExplorationOutcome explorationOutcome;

    public Simulation(int numberOfSteps, int stepsToTimeout, Rover rover, Coordinate spaceshipCoordinate, Map map,
                      List<String> resourcesToMonitor, ExplorationOutcome explorationOutcome) {
        this.numberOfSteps = numberOfSteps;
        this.stepsToTimeout = stepsToTimeout;
        this.rover = rover;
        this.spaceshipCoordinate = spaceshipCoordinate;
        this.map = map;
        this.resourcesToMonitor = resourcesToMonitor;
        this.explorationOutcome = explorationOutcome;
    }

    public int numberOfSteps() {
        return numberOfSteps;
    }

    public int stepsToTimeout() {
        return stepsToTimeout;
    }

    public Rover rover() {
        return rover;
    }

    public Coordinate getSpaceshipCoordinate() {
        return spaceshipCoordinate;
    }

    public Map map() {
        return map;
    }

    public List<String> resourcesToMonitor() {
        return resourcesToMonitor;
    }

    public ExplorationOutcome explorationOutcome() {
        return explorationOutcome;
    }

    @Override
    public String toString() {
        return "Simulation[" +
                "numberOfSteps=" + numberOfSteps + ", " +
                "stepsToTimeout=" + stepsToTimeout + ", " +
                "rover=" + rover + ", " +
                "spaceship=" + spaceship + ", " +
                "map=" + map + ", " +
                "resourcesToMonitor=" + resourcesToMonitor + ", " +
                "explorationOutcome=" + explorationOutcome + ']';
    }

}
