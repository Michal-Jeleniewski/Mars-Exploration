package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;

public class SimulationStepsLogging {
    private final Rover rover;
    private final Simulation simulation;
    private final Logger logger;
    private final ExplorationSimulator explorationSimulator;

    public SimulationStepsLogging(Rover rover, Simulation simulation, Logger logger, ExplorationSimulator explorationSimulator) {
        this.rover = rover;
        this.simulation = simulation;
        this.logger = logger;
        this.explorationSimulator = explorationSimulator;
    }

    public void logSteps() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("STEP ").append(simulation.numberOfSteps()).append(";");

        if (explorationSimulator.getAllOutcomeAnalyzer() == null) {
            stringBuilder.append("EVENT position");
        } else {
            stringBuilder.append("EVENT OUTCOME").append(explorationSimulator.getAllOutcomeAnalyzer());
        }

        stringBuilder.append("UNIT rover-" ).append(simulation.rover().getId());
        stringBuilder.append("POSITION ").append(simulation.rover().getPosition());
        logger.log(stringBuilder.toString());
    }
}
