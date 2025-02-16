import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Fox extends Animal
{
    

    
    
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 20;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Location location)
    {
        super(location, 5, 150, 1, 8, false);
        
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
        
        foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
    }
    
    protected void spreadDisease(Field currentField, boolean isDay){
        int x = 1;
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    protected void act(Field currentField, Field nextFieldState, Boolean isDay, String weather)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation(), 1);
            if(! freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, currentField, isDay);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField, isDay, weather);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if(nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
    

        


    @Override
    public String toString() {
        return "Fox{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }
    
    
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(Field field, boolean isDay, String weather)
    {
        List<Location> adjacent;
        Location foodLocation;
        if (foodLevel <15){
            if (!isDay && (weather.equals("sunny"))){ //Foxes can check 2 steps if it is day and if the weather is good.
                adjacent = field.getAdjacentLocations(getLocation(), 2); //Foxes can check for food 2 steps at night.
            }
            else{
                adjacent = field.getAdjacentLocations(getLocation(), 1); //During day they can only check 1 step.
            }
            Iterator<Location> it = adjacent.iterator();
            foodLocation = null;
            while(foodLocation == null && it.hasNext()) {
                Location loc = it.next();
                Entity animal = field.getAnimalAt(loc);
                if(animal instanceof Rabbit rabbit) {
                    if(rabbit.isAlive()) {
                        rabbit.setDead();
                        foodLevel = RABBIT_FOOD_VALUE;
                        foodLocation = loc;
                    }
                }
            }
        }
        //Will move to a random location if above the 17 food level
        else{

            List<Location> freeAdjacent = field.getFreeAdjacentLocations(getLocation(), 1);
            if (!freeAdjacent.isEmpty()) {
                foodLocation = freeAdjacent.get(rand.nextInt(freeAdjacent.size())); // Pick a random free location.
            } 
            else {
                foodLocation = getLocation(); // Stay in the same place if surrounded.
            } 
        }

        
        
        return foodLocation;
    }
    
    /**
     * Check whether this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField, boolean isDay) {
        // If the Foxes gender is not female, it cannot breed.
        if (!isGenderFemale()) {
            return;
        }

        // Check if there's a male Foxes nearby before attempting to breed
        if (!isMaleNearby(currentField)) {
            return; // No male nearby, so no breeding occurs
        }
        
        if (isDay) {
            return; //Foxes can only breed at night.
        }

        // New rabbits are born into adjacent locations.
        int births = breed(); // Triggers the breed method which checks if a Fox can breed.
        if (births > 0) { // If the breed method has provided a number of births greater than 0
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Fox young = new Fox(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
    
    protected boolean isMaleNearby(Field currentField) {
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation(), 1); // Gets all adjacent
                                                                                             // locations to check if
                                                                                             // there is a male rabbit
                                                                                             // nearby.
        // Checks each adjacent location if there is a rabbit in each one. If there is a
        // male rabbit in one, then breeding can occur.
        for (Location location : adjacentLocations) {
            Entity animal = currentField.getAnimalAt(location); // Gets the animal at this location.
            if (animal instanceof Fox && ((Fox) animal).getGender().equals("male")) { // Checks if the animal is a
                                                                                            // male
                return true;
            }
        }
        return false;
    }
    
    
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
