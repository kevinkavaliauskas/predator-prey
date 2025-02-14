import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Rabbit extends Prey
{

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    private String gender;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have random age.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Location location)
    {
        super(location, 5, 40, 0.3, 8); //Constructor of prey class for rabbit class
        this.gender = rand.nextBoolean() ? "female" : "male"; //Assigns a  random gender to the rabbit.
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
    }
    
    public String getGender(){
        return gender;
    }
    
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The field to update.
     */
    public void act(Field currentField, Field nextFieldState, Boolean isDay, String weather)
    {
        incrementAge();
        if(isAlive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, currentField, isDay);
            }
            // Try to move into a free location.
            if(!freeLocations.isEmpty()) {
                Location nextLocation = findFood(currentField);
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Entity animal = field.getAnimalAt(loc);
            if(animal instanceof Plant plant) {
                if(plant.isAlive()) {
                    plant.setDead();
                    foodLevel = plant.getHeight();
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    private boolean isMaleNearby(Field currentField){
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation());  //Gets all adjacent locations to check if there is a male rabbit nearby.
        //Checks each adjacent location if there is a rabbit in each one. If there is a male rabbit in one, then breeding can occur.
        for (Location location : adjacentLocations){
            Entity animal = currentField.getAnimalAt(location); //Gets the animal at this location.
            if (animal instanceof Rabbit && ((Rabbit) animal).getGender().equals("male")) {  //Checks if  the animal  is a male
                return true;
            }
        }
        return false;
    }
    
    private boolean isGenderFemale(){
        if(this.getGender().equals("female")){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Rabbit{" +
                "age=" + getAge() +
                ", gender=" + getGender() +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    @Override
    protected void incrementAge()
    {
        super.incrementAge();
        if(getAge() > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param nextFieldState The field to update.
     * @param freeLocations The locations that are free in the field.
     */

    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField, boolean isDay)
    {
        //If it is not daytime, rabbits cannot breed.
        if (!isDay){
            return;
        }
        
        //If the rabbits gender  is not female, it cannot breed.
        if (!isGenderFemale()){
            return;
        }

        // Check if there's a male rabbit nearby before attempting to breed
        if (!isMaleNearby(currentField)) {
            return; // No male nearby, so no breeding occurs
        }
        
        
        
        // New rabbits are born into adjacent locations.
        int births = breed(); //Triggers the breed method which checks if a rabbit can breed.
        if(births > 0) { //If the breed method has provided a number of births greater than 0
            for(int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Rabbit young = new Rabbit(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    @Override
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;  
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    @Override
    protected boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }
}
