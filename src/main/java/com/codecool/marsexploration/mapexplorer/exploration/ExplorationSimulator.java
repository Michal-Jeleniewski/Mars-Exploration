package com.codecool.marsexploration.mapexplorer.exploration;

import com.codecool.marsexploration.mapexplorer.analizer.AllOutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.analizer.OutcomeAnalyzer;
import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationParameters;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.rovers.Rover;
import com.codecool.marsexploration.mapexplorer.rovers.RoverPlacement;

import java.util.Set;

public class ExplorationSimulator {

    private ConfigurationParameters configurationParameters;
    private MapLoader mapLoader;

    // dodać Configuration validator
    private RoverPlacement roverPlacement;

    private Rover rover;

    private final AllOutcomeAnalyzer allOutcomeAnalyzer;

    public ExplorationSimulator(ConfigurationParameters configurationParameters, MapLoader mapLoader, RoverPlacement roverPlacement, AllOutcomeAnalyzer allOutcomeAnalyzer) {
        this.configurationParameters = configurationParameters;
        this.mapLoader = mapLoader;
        this.roverPlacement = roverPlacement;
        this.allOutcomeAnalyzer = allOutcomeAnalyzer;
    }


    public void runSimulation(ConfigurationParameters configurationParameters) {
        Simulation simulation = new Simulation(0, configurationParameters.maxSteps(), rover,
                configurationParameters.spaceshipLandingPoint(),
                mapLoader.load(configurationParameters.mapPath()), configurationParameters.symbols(), null);


        // IN LOOP

        //Movement. The rover needs to determine an adjacent empty spot of the chart to move

        // Scanning. The rover needs to scan the area for resources based on how far it can see (its sight).

        // Analysis. After the information is gathered, you need to determine whether an outcome is reached.

        // Log. Write the current state of events in the simulation to the log file.

        // Step increment. Increment the context step variable by one.


    }


}
