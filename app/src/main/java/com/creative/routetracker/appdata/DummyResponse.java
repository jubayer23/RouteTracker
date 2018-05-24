package com.creative.routetracker.appdata;

/**
 * Created by jubayer on 5/22/2018.
 */

public class DummyResponse {

    public static String getRoutes(){
        return "{\n" +
                "\t\"result\": 1,\n" +
                "\t\"routes\": [{\n" +
                "\t\t\t\"userId\": 1,\n" +
                "\t\t\t\"routeId\": 1,\n" +
                "\t\t\t\"startingPoint\": \"24.901045,91.854051\",\n" +
                "\t\t\t\"routeName\": \"testRoute01\",\n" +
                "\t\t\t\"areaType\": \"tracking\",\n" +
                "\t\t\t\"activityType\": \"walk\",\n" +
                "\t\t\t\"duration\": \"2 hours\",\n" +
                "\t\t\t\"fitness\": \"Extreme\",\n" +
                "\t\t\t\"access\": \"For-all\",\n" +
                "\t\t\t\"safetyNotes\": \"safe\",\n" +
                "\t\t\t\"averageRating\": 3.5\n" +
                "\n" +
                "\t\t}, {\n" +
                "\t\t\t\"userId\": 2,\n" +
                "\t\t\t\"routeId\": 2,\n" +
                "\t\t\t\"startingPoint\": \"24.898838,91.839287\",\n" +
                "\t\t\t\"routeName\": \"testRoute01\",\n" +
                "\t\t\t\"areaType\": \"tracking\",\n" +
                "\t\t\t\"activityType\": \"run\",\n" +
                "\t\t\t\"duration\": \"2 hours\",\n" +
                "\t\t\t\"fitness\": \"Extreme\",\n" +
                "\t\t\t\"access\": \"For-all\",\n" +
                "\t\t\t\"safetyNotes\": \"safe\",\n" +
                "\t\t\t\"averageRating\": 3.5\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
    }
}
