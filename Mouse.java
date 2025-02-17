import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a Mouse.
 * Mouse age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Mouse extends Animal {


    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Mouse will have random age.
     * @param location  The location within the field.
     */
    public Mouse(boolean randomAge, Location location, boolean infected) {
        super(location, 5, 20, 0.4, 12, infected); // Constructor of Animal class for rabbit class
        
        if (randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
        foodLevel = rand.nextInt(20);
    }
    
    

    
    /**
     * This is what the rabbit does most of the time - it runs
     * around. Sometimes it will breed or die of old age.
     * 
     * @param currentField   The field currently occupied.
     * @param nextFieldState The field to update.
     */
    public void act(Field currentField, Field nextFieldState, Boolean isDay, String weather) {
        incrementAge();
        incrementHunger();
        
        
        //Can only get cured if infected, will always be infected for at least one step.
        if(infected){
            //Every turn infected, their max age will decrease by 10%.
            MAX_AGE = (int)(0.9 * MAX_AGE);
            //20% chance of getting cured every step.d
            getCured(0.2);
            if (rand.nextDouble() <0.05){
                setDead();
            }
        }
        
        //Can only get infected if not already infected
        if(!infected){
             getInfectedPassive();
        }
        
        //10% chance of mouse dying to extreme weather.
        deathToExtremeWeather(weather);
        
        
        //Spread Disease to other members of the same species in adjacent tiles if already infected.
        if(infected){
            spreadDisease(currentField, isDay);
        }
        
        
        
        if (isAlive()) {
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation(), 2);
            if (!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, currentField, isDay);
            }
            // Move towards a source of food if found.
            
            Location nextLocation;
            //Mouse can only find food during the Night.
            if(!isDay){
                nextLocation = findFood(currentField, isDay, weather);
            }
            else{
                nextLocation = getLocation();
            }
            
            if (nextLocation == null && !freeLocations.isEmpty()) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if (nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            } 
                
            else {
                // Overcrowding.
                setDead();
            }
        }
        
    }

    protected Location findFood(Field field, boolean isDay, String weather) {
        List<Location> adjacent;
        if(isDay){
            adjacent = field.getAdjacentLocations(getLocation(), 2);
        }
        else{
            adjacent = field.getAdjacentLocations(getLocation(), 1);
        }
        
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while (foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Entity animal = field.getAnimalAt(loc);
            if (animal instanceof Plant plant) {
                if (plant.isAlive() && plant.getHeight()<=3)  { //Mouse can only eat small plants.
                    plant.setDead();
                    foodLevel = foodLevel + plant.getHeight(); //Food level stacks of mice.
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    protected Location isMaleNearby(Field currentField) {
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation(), 1); // Gets all adjacent
                                                                                             // locations to check if
                                                                                             // there is a male rabbit
                                                                                             // nearby.
        // Checks each adjacent location if there is a rabbit in each one. If there is a
        // male rabbit in one, then breeding can occur.
        for (Location location : adjacentLocations) {
            Entity animal = currentField.getAnimalAt(location); // Gets the animal at this location.
            if (animal instanceof Mouse && ((Mouse) animal).getGender().equals("male")) { // Checks if the animal is a male
                return location;
            }
        }
        return null;
    }

    protected void spreadDisease(Field currentField, boolean isDay){
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation(), 1); //Get all adjacent location to check if their are any of the same species within
                                                                                                       // the same radius. To see who disease can be spread to.
        // Checks each adjacent location if there is an animal in each one. Mice can spread disease to all animals of all types.
        for (Location location : adjacentLocations) {
            Entity entity = currentField.getAnimalAt(location); // Gets the animal at this location.
            if (entity instanceof Animal animal && !animal.getInfectedStatus()) {
                double infectionChance = isDay ? 0.01 : 0.001; // More likely to spread during the day.
                if (rand.nextDouble() <= infectionChance) { 
                    animal.getInfected();
                }
            }
            
        }
                                                                                            
    }
    
    @Override
    public String toString() {
        return "Mouse{" +
                "age=" + getAge() +
                ", gender=" + getGender() +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the Mouse's death.
     */
    @Override
    protected void incrementAge() {
        super.incrementAge();
        if (getAge() > MAX_AGE) {
            setDead();
        }
    }



    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
     * @return The number of births (may be zero).
     */
    @Override
    protected int breed(boolean isDay) {
        int births = 0;
        if (canBreed(isDay) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Mouse can breed if it has reached the breeding age.
     * 
     * @return true if the Mouse can breed, false otherwise.
     */
    @Override
    protected boolean canBreed(boolean isDay)
    {
        if (age >= BREEDING_AGE && isDay){ //Mouse can only breed at night
            return true;
        }
        else{
            return false;
        }
    }
    
     /**
     * Creates a new young Mouse
     * @param loc The location for the new Mouse
     * @param maleLocation Location of the male parent
     * @param field The current field
     * @return A new young Mouse
     */
    @Override
    protected Animal createChild(Location loc, Location maleLocation, Field field) {
        // Get male Mouse
        Mouse male = null;
        Entity maleMouse = field.getAnimalAt(maleLocation);
        if (maleMouse instanceof Mouse) {
            male = (Mouse) maleMouse;
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
        
        // Create young rabbit with calculated traits
        boolean childInfected = infected && rand.nextDouble() <= 0.2;
        Mouse child = new Mouse(false, loc, childInfected);
        child.setBreedingProbability(newBreedingProbability);
        child.setBreedingAge(newBreedingAge);
        child.setMaxLitterSize(newMaxLitterSize);
        
        return child;
    }
}
