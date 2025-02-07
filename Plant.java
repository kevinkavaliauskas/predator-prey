
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
    private Location location;
    // The plant's height
    private int height;
    // Number of steps since last rain weather
    private int lastRain;

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
            }
        }
        else if (weather.equals("sunny") || weather.equals("rain") && lastRain < 10){
            // Plants can only grow in sun or rain
            grow();
        }

        lastRain++;
    }
    
    private void grow() {
        height += 5;
        if (height >= 25) {
            height = 25;
        }
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the plant is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the plant's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
}
