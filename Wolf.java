import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a Wolf.
 * Wolf age, move, eat Deer, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Wolf extends Animal
{
    

    
    
    // The food value of a single Deer and a single mouse. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 20;
    private static final int MOUSE_FOOD_VALUE = 10;

    /**
     * Create a wolf. A Wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Wolf will have random age and hunger level.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Location location, boolean infected)
    {
        super(location, 5, 150, 1, 8, infected);
        
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
        
        foodLevel = rand.nextInt(DEER_FOOD_VALUE);
    }
    
    protected void spreadDisease(Field currentField, boolean isDay){
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation(), 1); //Get all adjacent location to check if their are any of the same species within
                                                                                                       // the same radius. To see who disease can be spread to.
        // Checks each adjacent location if there is a Deer in each one. 
        for (Location location : adjacentLocations) {
            Entity animal = currentField.getAnimalAt(location); // Gets the animal at this location.
            //More likely to spread disease at day than night.
            if(isDay){
                if (animal instanceof Wolf && !((Wolf) animal).getInfectedStatus() && rand.nextDouble()<=0.01) { 
                    ((Animal)animal).getInfected();
                }
            }
            else{
                if (animal instanceof Wolf && !((Wolf) animal).getInfectedStatus() && rand.nextDouble()<=0.001) { 
                    ((Animal)animal).getInfected();
                }
            }
            
        }
                                                                                            
    }
    
    /**
     * This is what the Wolf does most of the time: it hunts for
     * Deer. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    protected void act(Field currentField, Field nextFieldState, Boolean isDay, String weather)
    {
        incrementAge();
        incrementHunger();
        
        
        //Can only get cured if infected, will always be infected for at least one step.
        if(infected){
            //Every turn infected, their max age will decrease by 10%.
            MAX_AGE = (int)(0.95 * MAX_AGE);
            //20% chance of getting cured every step,
            getCured(0.2);
            if (rand.nextDouble() <0.05){
                setDead();
            }
        }
        
        //Can only get infected if not already infected
         if(!infected){
             getInfectedPassive();
        }
        
        
        
        //Spread Disease to other members of the same species in adjacent tiles.
        if(infected){
            spreadDisease(currentField, isDay);
        }
        
        
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
        return "Wolf{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }
    
    
    
    /**
     * Look for Deer adjacent to the current location.
     * Only the first live Deer is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(Field field, boolean isDay, String weather)
    {
        List<Location> adjacent;
        Location foodLocation;
        if (foodLevel <15){
            if (!isDay && (weather.equals("sunny"))){ //Wolf can check 2 steps if it is day and if the weather is good.
                adjacent = field.getAdjacentLocations(getLocation(), 2); //Wolf can check for food 2 steps at night.
            }
            else{
                adjacent = field.getAdjacentLocations(getLocation(), 1); //During day they can only check 1 step.
            }
            Iterator<Location> it = adjacent.iterator();
            foodLocation = null;
            while(foodLocation == null && it.hasNext()) {
                Location loc = it.next();
                Entity animal = field.getAnimalAt(loc);
                    if(animal instanceof Deer deer) {
                    if(deer.isAlive()) {
                        deer.setDead();
                        foodLevel = DEER_FOOD_VALUE;
                        foodLocation = loc;
                    }
                    else if (animal instanceof Mouse mouse) {
                        if (mouse.isAlive()) {
                            //If an animal eats a mouse that is infected, it can get infected.
                            if(mouse.getInfectedStatus()){
                                this.getInfected();
                            }
                            mouse.setDead();
                            foodLevel = MOUSE_FOOD_VALUE;
                            foodLocation = loc;
                        }
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
    
    
    protected Location isMaleNearby(Field currentField) {
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation(), 1); // Gets all adjacent
                                                                                             // locations to check if
                                                                                             // there is a male Wolf
                                                                                             // nearby.
        // Checks each adjacent location if there is a Wolf in each one. If there is a
        // male Wolf in one, then breeding can occur.
        for (Location location : adjacentLocations) {
            Entity animal = currentField.getAnimalAt(location); // Gets the animal at this location.
            if (animal instanceof Wolf && ((Wolf) animal).getGender().equals("male")) { // Checks if the animal is a
                                                                                            // male
                return location;
            }
        }
        return null;
    }
    
    
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(boolean isDay)
    {
        int births;
        if(canBreed(isDay) && rand.nextDouble() <= BREEDING_PROBABILITY) {//Checks if the animal is capable of breeding
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A Wolf can breed if it has reached the breeding age and if the time of day currently is night.
     */
    protected boolean canBreed(boolean isDay)
    {
        if (age >= BREEDING_AGE && !isDay){ //Wolf can only breed at night
            return true;
        }
        else{
            return false;
        }
    }
    
    protected Animal createChild(Location loc, Location maleLocation, Field field) {
        // Get male Wolf
        Wolf male = null;
        Entity maleWolf = field.getAnimalAt(maleLocation);
        if (maleWolf instanceof Wolf) {
            male = (Wolf) maleWolf;
        }
        
        if (male == null) {
            return null;
        }
        
        // Calculate genetics from both parents
        double maleBreedingProbability = male.getBreedingProbability();
        int maleBreedingAge = male.getBreedingAge();
        int maleMaxLitterSize = male.getMaxLitterSize();
        
        double newBreedingProbability = (getBreedingProbability() + maleBreedingProbability) / 2;
        int newBreedingAge = (getBreedingAge() + maleBreedingAge) / 2;
        int newMaxLitterSize = (getMaxLitterSize() + maleMaxLitterSize) / 2;
        
        // Handle mutations
        double mutationChance = rand.nextDouble();
        if (mutationChance <= 0.02) {
            newBreedingProbability *= 3;
            newMaxLitterSize *= 3;
        } else if (mutationChance >= 0.98) {
            newBreedingProbability /= 3;
            newMaxLitterSize /= 3;
        }
        
        // Create young Wolf with calculated traits
        boolean childInfected = infected && rand.nextDouble() <= 0.2;
        Wolf child = new Wolf(false, loc, childInfected);
        child.setBreedingProbability(newBreedingProbability);
        child.setBreedingAge(newBreedingAge);
        child.setMaxLitterSize(newMaxLitterSize);
        
        return child;
    }
}
