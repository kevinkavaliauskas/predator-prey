import java.util.List;
/**
 * Write a description of class Predator here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Prey extends Animal {

    
    /**
     * Create a new prey animal.
     * 
     * @param location The location within the field.
     * @param MAX_AGE The maximum age that this prey can live too.
     * @param BREEDING_PROBABILITY The probability of this type of prey breeding
     * @param BREEDING_AGE The age at which this type of prey can breed
     * @param MAX_LITTER_SIZE The maximum number of prey this type of prey will give birth too
     */
    public Prey(Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
        super(location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        age = 0;
    }
    
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    
    /**
     * Increase the age of the prey.
     * Each prey type should implement their own death conditions.
     */
    protected void incrementAge() {
        age++;
    }
    
    /**
     * Get the current age of the prey.
     * @return The current age.
     */
    protected int getAge() {
        return age;
    }
    
    /**
     * Set the age of the prey.
     * @param age The new age.
     */
    protected void setAge(int age) {
        this.age = age;
    }
    
    /**
     * Check whether or not this prey can breed.
     * @return true if the prey can breed, false otherwise.
     */
    protected abstract boolean canBreed();
    
    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param nextFieldState The field to update.
     * @param freeLocations The locations that are free in the field.
     */
    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField, boolean isDay);


    /**
     * Generate a number representing the number of births.
     * @return The number of births (may be zero).
     */
    protected abstract int breed();
}