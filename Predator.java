import java.util.List;

/**
 * Write a description of class Predator here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Predator extends Animal
{
    
    

    /**
     * Constructor for objects of class Predator
     */
    public Predator(Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE)
    {
        super(location, BREEDING_AGE, MAX_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        
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
        System.out.println("Fox dies of hunger");
    }
    
    protected abstract void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField, boolean isDay);

    
    protected abstract boolean canBreed();
    
    protected abstract int breed();
}
