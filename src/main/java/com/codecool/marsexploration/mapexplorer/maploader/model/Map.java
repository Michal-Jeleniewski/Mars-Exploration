package com.codecool.marsexploration.mapexplorer.maploader.model;

public class Map {
    private final String[][] representation;
    private final boolean successfullyGenerated;

    public Map(String[][] representation, boolean successfullyGenerated) {
        this.representation = representation;
        this.successfullyGenerated = successfullyGenerated;
    }

    private static String createStringRepresentation(String[][] arr) {
        StringBuilder sb = new StringBuilder();

        for (String[] strings : arr) {
            StringBuilder s = new StringBuilder();
            for (String string : strings) {
                s.append(string == null ? " " : string);
            }

            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    public String[][] getRepresentation() {
        return representation;
    }

    public int getDimension() {
        return representation.length;
    }

    public String getByCoordinate(Coordinate coordinate) {
        return representation[coordinate.X()][coordinate.Y()];
    }

    public boolean isEmpty(Coordinate coordinate) {
        return representation[coordinate.X()][coordinate.Y()] == null
                || representation[coordinate.X()][coordinate.Y()].isEmpty()
                || representation[coordinate.X()][coordinate.Y()].equals(" ");
    }

    @Override
    public String toString() {
        return createStringRepresentation(representation);
    }
}
