import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * Write a description of class Weather here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Weather
{
    private String currentWeather;

    /**
     * This generates a random weather from the available weather and
     * stores it in the currentWeather variable so that it can be accessed.
     */
    public void setRandomWeather(){
        Random rand = Randomizer.getRandom();
        int probabilityOfWeather = rand.nextInt(1000) + 1;
        
        if (probabilityOfWeather <= 450){
            currentWeather = "rain";
        }
        else if (probabilityOfWeather <= 450){
            currentWeather = "sunny";
        }
        else if (probabilityOfWeather <= 950){
            currentWeather = "snow";
        }
        else if (probabilityOfWeather <= 975){
            currentWeather = "lightning";
        }
        else{
            currentWeather = "tornado";
        }
    }
    
    public String getRandomWeather(){
        return currentWeather;
    }
}
