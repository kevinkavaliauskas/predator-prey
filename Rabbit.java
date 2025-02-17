import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Rabbit extends Animal {


    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have random age.
     * @param location  The location within the field.
     */
    public Rabbit(boolean randomAge, Location location, boolean infected) {
        super(location, 5, 20, 0.32, 12, infected); // Constructor of prey class for rabbit class
        
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
            getCured();
            if (rand.nextDouble() <0.05){
                setDead();
            }
        }
        
        //Can only get infected if not already infected
        // if(!infected){
        //     getInfected();
        // }
        
        
        
        //Spread Disease to other members of the same species in adjacent tiles.
        spreadDisease(currentField, isDay);
        
        
        
        if (isAlive()) {
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation(), 2);
            if (!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations, currentField, isDay);
            }
            // Move towards a source of food if found.
            
            Location nextLocation;
            //Rabbits can only find food during the day.
            if(isDay){
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
                if (plant.isAlive()) {
                    plant.setDead();
                    foodLevel = foodLevel + plant.getHeight();
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
            if (animal instanceof Rabbit && ((Rabbit) animal).getGender().equals("male")) { // Checks if the animal is a male
                return location;
            }
        }
        return null;
    }

    protected void spreadDisease(Field currentField, boolean isDay){
        List<Location> adjacentLocations = currentField.getAdjacentLocations(getLocation(), 1); //Get all adjacent location to check if their are any of the same species within
                                                                                                       // the same radius. To see who disease can be spread to.
        // Checks each adjacent location if there is a rabbit in each one. 
        for (Location location : adjacentLocations) {
            Entity animal = currentField.getAnimalAt(location); // Gets the animal at this location.
            //More likely to spread disease at day than night.
            if(isDay){
                if (animal instanceof Rabbit && !((Rabbit) animal).getInfectedStatus() && rand.nextDouble()<=0.01) { 
                    ((Animal)animal).getInfected();
                }
            }
            else{
                if (animal instanceof Rabbit && !((Rabbit) animal).getInfectedStatus() && rand.nextDouble()<=0.001) { 
                    ((Animal)animal).getInfected();
                }
            }
            
        }
                                                                                            
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
    protected void incrementAge() {
        super.incrementAge();
        if (getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param nextFieldState The field to update.
     * @param freeLocations  The locations that are free in the field.
     */

    protected void giveBirth(Field nextFieldState, List<Location> freeLocations, Field currentField, boolean isDay) {
        // If it is not daytime, rabbits cannot breed.
        if (!isDay) {
            return;
        }

        // If the rabbits gender is not female, it cannot breed.
        if (!isGenderFemale()) {
            return;
        }
        
        //If infected there is a 20% chance it cannot breed. 
        if (infected) {
            if (rand.nextDouble() < 0.2) {
                return;
            }
        }

        // Check if there's a male rabbit nearby before attempting to breed
        Location maleLocation = isMaleNearby(currentField);
        if (maleLocation == null) {
            return; // No male nearby, so no breeding occurs
        }

        // New rabbits are born into adjacent locations.
        int births = breed(); // Triggers the breed method which checks if a rabbit can breed.
        if (births > 0) { // If the breed method has provided a number of births greater than 0
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);

                // get male rabbit
                Rabbit male = null;
                Entity maleRabbit = currentField.getAnimalAt(maleLocation);
                if (maleRabbit instanceof Rabbit) {
                    male = (Rabbit) maleRabbit;
                }

                // get male rabbit breeding probability, age, litter size
                double maleBreedingProbability = male.getBreedingProbability();
                int maleBreedingAge = male.getBreedingAge();
                int maleMaxLitterSize = male.getMaxLitterSize();

                // get parent breeding probability, age, litter size
                double breedingProbability = getBreedingProbability();
                int breedingAge = getBreedingAge();
                int maxLitterSize = getMaxLitterSize();

                // calculate new breeding probability, age, litter size by averaging
                double newBreedingProbability = (breedingProbability + maleBreedingProbability) / 2;
                int newBreedingAge = (breedingAge + maleBreedingAge) / 2;
                int newMaxLitterSize = (maxLitterSize + maleMaxLitterSize) / 2;

                // 2% chance of having a mutation to increase breeding probability and litter size
                double mutationChance = rand.nextDouble();
                if (mutationChance <= 0.02) {
                    newBreedingProbability = newBreedingProbability * 3;
                    newMaxLitterSize = newMaxLitterSize * 3;
                }
                // 2% chance of having a mutation to decrease breeding probability and litter size
                else if (mutationChance >= 0.98 ) {
                    newBreedingProbability = newBreedingProbability / 3;
                    newMaxLitterSize = newMaxLitterSize / 3;
                }
                
                //If Infected, 20% chance that the children will also be infected
                if(infected && rand.nextDouble()<=0.2){
                    Rabbit young = new Rabbit(false, loc, true);
                    young.setBreedingProbability(newBreedingProbability);
                    young.setBreedingAge(newBreedingAge);
                    young.setMaxLitterSize(newMaxLitterSize);
                    nextFieldState.placeAnimal(young, loc);
                }
                else{
                    Rabbit young = new Rabbit(false, loc, false);
                    young.setBreedingProbability(newBreedingProbability);
                    young.setBreedingAge(newBreedingAge);
                    young.setMaxLitterSize(newMaxLitterSize);
                    nextFieldState.placeAnimal(young, loc);
                }
                
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
     * @return The number of births (may be zero).
     */
    @Override
    protected int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * 
     * @return true if the rabbit can breed, false otherwise.
     */
    @Override
    protected boolean canBreed() {
        return getAge() >= BREEDING_AGE;
    }
}
