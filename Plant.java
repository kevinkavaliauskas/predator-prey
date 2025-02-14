
/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Plant extends Entity
{
    // Whether the plant is alive or not.
    private boolean alive;
    // The plant's position.
    private int height;
    // Number of steps since last rain weather
    private int lastRain = 0;

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Plant(boolean alive, Location location)
    {
        super(location);
        this.alive = true;
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    public void act(Field currentField, Field nextFieldState, Boolean isDay, String weather)
    {

        if (weather.equals("rain")) {
            lastRain = 0;
        }
        
        if (weather.equals("tornado") || weather.equals("lightning")) {
            // 10% chance of a plant dying due to extreme weather
            if (Math.random() < 0.1) {
                setDead();
                return;
            }
        }
        else if (weather.equals("sunny") || weather.equals("rain") && lastRain < 100){
            // Plants can only grow in sun or rain
            grow();
        }
        
        // Plants stay in the same location
        nextFieldState.placeAnimal(this, this.getLocation());
        
        lastRain++;
    }
    
    private void grow() {
        height += 5;
        if (height >= 25) {
            height = 25;
        }
    }
    


    public String toString() {
        return "Rabbit{" +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

}
