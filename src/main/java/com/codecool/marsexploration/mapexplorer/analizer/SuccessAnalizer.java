package com.codecool.marsexploration.mapexplorer.analizer;

import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;

import java.util.Set;

public class SuccessAnalizer implements OutcomeAnalyzer {

    private final int resourcesToSucsess;

    public SuccessAnalizer(int resourcesToSucsess) {
        this.resourcesToSucsess = resourcesToSucsess;
    }

    @Override
    public ExplorationOutcome analyze(Simulation simulation) {
        int foundedResources = simulation.rover().getResourcesPoints().values().stream().mapToInt(Set::size).sum();
        if (foundedResources >= resourcesToSucsess) {
            return ExplorationOutcome.COLONIZABLE;
        } else return null;
    }


}
