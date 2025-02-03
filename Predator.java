import java.util.List;

/**
 * Write a description of class Predator here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Predator extends Animal
{
    // The Predator's age.
    protected int age;
    
    // Characteristics shared by all foxes (class variables).
    // The age at which a fox can start to breed.
    protected final int BREEDING_AGE;
    // The age to which a fox can live.
    protected final int MAX_AGE;
    // The likelihood of a fox breeding.
    protected final double BREEDING_PROBABILITY;
    // The maximum number of births.
    protected final int MAX_LITTER_SIZE;
    // The food level of this predator
    protected int foodLevel;
    

    /**
     * Constructor for objects of class Predator
     */
    public Predator(Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(location);
        this.BREEDING_AGE = BREEDING_AGE;
        this.MAX_AGE = MAX_AGE;
        this.BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        this.MAX_LITTER_SIZE = MAX_LITTER_SIZE;
        age = 0;
    }
    
    
    // Aging behavior for all predators
    protected void setAge(int age) {
        this.age = age;
    }
        
    protected void incrementAge() {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }

    protected int getAge() {
        return age;
    }
    
    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations);
    
    
    
    protected abstract boolean canBreed();
    
    protected abstract int breed();
}
