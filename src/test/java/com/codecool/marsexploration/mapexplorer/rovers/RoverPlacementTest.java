package com.codecool.marsexploration.mapexplorer.rovers;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.maploader.model.Map;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RoverPlacementTest {

    @Test
    public void testGenerateRandomCoordinateForRover(){
        Map map = new Map(new String[5][5], true);
        RoverPlacement roverPlacement = new RoverPlacement(map);
        Coordinate coordinate = roverPlacement.generateRandomCoordinateForRover();

        assertNotNull(coordinate);
        assertTrue(coordinate.Y() >= 0 && coordinate.Y() < map.getDimension());
        assertTrue(coordinate.X() >= 0 && coordinate.X() < map.getDimension());
    }
}