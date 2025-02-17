import java.util.Random;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling and Aashwin Eldo and Kevin
 *         Kavaliauskas
 * @version 7.0
 */
public abstract class Entity {
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;

    protected static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Entity.
     * 
     * @param location The entity's location.
     */
    public Entity(Location location) {
        this.alive = true;
        this.location = location;
    }

    /**
     * 
     * @param weather The weather of the current step.
     */
    protected void deathToExtremeWeather(String weather) {
        if (weather.equals("tornado") || weather.equals("lightning") || weather.equals("snow")) {
            // 10% chance of an entity dying due to extreme weather
            if (Math.random() < 0.1) {
                setDead();
                return;
            }
        }
    }

    /**
     * Act method for the entity which is called on every step of the simulation.
     * 
     * @param currentField   The current state of the field.
     * @param nextFieldState The new state being built.
     * @param isDay          Whether it is day or night.
     * @param weather        The weather of the current step.
     */
    abstract protected void act(Field currentField, Field nextFieldState, Boolean isDay, String weather);

    /**
     * Check whether the animal is alive or not.
     * 
     * @return true if the animal is still alive.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead() {
        alive = false;
        location = null;
    }

    /**
     * Return the animal's location.
     * 
     * @return The animal's location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the entity's location.
     * 
     * @param location The new location.
     */
    protected void setLocation(Location location) {
        this.location = location;
    }
}
