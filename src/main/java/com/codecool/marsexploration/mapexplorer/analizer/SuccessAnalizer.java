package com.codecool.marsexploration.mapexplorer.analizer;

import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;

public class SuccessAnalizer implements OutcomeAnalyzer {

    private final int resourcesToSucsess;

    public SuccessAnalizer(int resourcesToSucsess) {
        this.resourcesToSucsess = resourcesToSucsess;
    }

    @Override
    public ExplorationOutcome analyze(Simulation simulation) {
        if (simulation.rover().getResourcesPoints().size() >= resourcesToSucsess) {
            return ExplorationOutcome.COLONIZABLE;
        } else return null;
    }


}
