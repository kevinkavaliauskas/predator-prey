import java.util.Random;

/**
 * A class that generates a random weather from the available weather and
 * stores it in the currentWeather variable so that it can be accessed.
 *
 * @author Aashwin Eldo
 * @version 1.0
 */
public class Weather {
    private String currentWeather;

    /**
     * This generates a random weather from the available weather and
     * stores it in the currentWeather variable so that it can be accessed.
     */
    public void setRandomWeather() {
        Random rand = Randomizer.getRandom();
        int probabilityOfWeather = rand.nextInt(1000) + 1;

        if (probabilityOfWeather <= 450) {
            currentWeather = "rain";
        } else if (probabilityOfWeather <= 940) {
            currentWeather = "sunny";
        } else if (probabilityOfWeather <= 950) {
            currentWeather = "snow";
        } else if (probabilityOfWeather <= 975) {
            currentWeather = "lightning";
        } else {
            currentWeather = "tornado";
        }
    }

    public String getWeather() {
        return currentWeather;
    }
}
