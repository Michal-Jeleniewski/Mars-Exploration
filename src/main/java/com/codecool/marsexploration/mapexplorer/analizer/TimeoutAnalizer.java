package com.codecool.marsexploration.mapexplorer.analizer;

import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;

public class TimeoutAnalizer implements OutcomeAnalyzer {
    @Override
    public ExplorationOutcome analize(Simulation simulation) {
        if (simulation.numberOfSteps() >= simulation.stepsToTimeout()) {
            return ExplorationOutcome.TIMEOUT;
        } else return null;
    }
}
