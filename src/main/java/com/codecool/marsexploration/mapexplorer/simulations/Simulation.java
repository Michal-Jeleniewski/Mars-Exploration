package com.codecool.marsexploration.mapexplorer.simulations;


import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.Spaceship;

import java.util.List;

public record Simulation(int numberOfSteps, int stepsToTimeout, Rover rover, Spaceship spaceship, String[][] map,
                         List<String> resourcesToMonitor, String explorationOutcome) {
}
