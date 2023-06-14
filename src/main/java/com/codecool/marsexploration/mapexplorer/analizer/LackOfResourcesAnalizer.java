package com.codecool.marsexploration.mapexplorer.analizer;

import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.exploration.Simulation;

public class LackOfResourcesAnalizer implements OutcomeAnalyzer {

    private final double maxPercentageMapExploration;

    public LackOfResourcesAnalizer(double maxPercentageMapExploration) {
        this.maxPercentageMapExploration = maxPercentageMapExploration;
    }

    @Override
    public ExplorationOutcome analyze(Simulation simulation) {
        int dimension = simulation.map().getDimension();
        if(simulation.rover().getScannedCoordinations().size() >= (Math.pow(dimension , 2)) * maxPercentageMapExploration){
            return ExplorationOutcome.LACK_OF_RESOURCES;
        } else return null;
    }
}
