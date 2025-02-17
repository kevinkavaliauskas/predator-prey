import java.util.List;

/**
 * Write a description of class Predator here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public abstract class Animal extends Entity {
    // Characteristics shared by all prey (class variables)
    // The age of the prey
    protected int age;
    
    // The age to which a Prey can live.
    protected int MAX_AGE;
    
    // The likelihood of a Prey breeding.
    protected double BREEDING_PROBABILITY;
    
    // The age at which a Prey can start to breed.
    protected int BREEDING_AGE;
    
    // The maximum number of births.
    protected int MAX_LITTER_SIZE;
    
    //The Food level of this animal
    protected int foodLevel;
    
    //The gender of the animal.
    protected String gender;

    
    //The animal is infected with disease.
    protected boolean infected;

    
    /**
     * Create a new prey animal.
     * 
     * @param location The location within the field.
     * @param MAX_AGE The maximum age that this prey can live too.
     * @param BREEDING_PROBABILITY The probability of this type of prey breeding
     * @param BREEDING_AGE The age at which this type of prey can breed
     * @param MAX_LITTER_SIZE The maximum number of prey this type of prey will give birth too
     */
    public Animal(Location location, int BREEDING_AGE, int MAX_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE, boolean infected) {
        super(location);
        this.MAX_AGE = MAX_AGE;
        this.BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        this.BREEDING_AGE = BREEDING_AGE;
        this.MAX_LITTER_SIZE = MAX_LITTER_SIZE;
        this.gender = rand.nextBoolean() ? "female" : "male"; // Assigns a random gender to the Animal.
        this.infected = infected;
        age = 0;
    }

    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    protected void setBreedingProbability(double breedingProbability) {
        this.BREEDING_PROBABILITY = breedingProbability;
    }

    protected void setBreedingAge(int breedingAge) {
        this.BREEDING_AGE = breedingAge;
    }

    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    protected void setMaxLitterSize(int maxLitterSize) {
        this.MAX_LITTER_SIZE = maxLitterSize;
    }
    
    //100% chance of getting infected through spread
    protected void getInfected(){
        infected = true;
    }
    
    //1% of randomly getting infected by disease every step.
    protected void getInfectedPassive(){
        if (rand.nextDouble() <= 0.01){
            getInfected();  
        }
    }
    
    protected void getCured(){
        //20% chance every step of disease being cured.
        if(rand.nextDouble()<=0.2){
            infected = false;
        }
    }
    
    
    protected String getGender() {
        return gender;
    }
    
    protected abstract Location findFood(Field field, boolean isDay, String weather);
    
    
    protected boolean isGenderFemale() {
        if (this.getGender().equals("female")) {
            return true;
        }
        return false;
    }
    
    protected abstract Location isMaleNearby(Field currentField);

    
    /**
     * Increase the age of the prey.
     * Each prey type should implement their own death conditions.
     */
    protected void incrementAge() {
        age++;
        if (age>MAX_AGE){
            setDead();
        }
    }
    
    protected boolean getInfectedStatus(){
        return infected;
    }
    
    protected abstract void spreadDisease(Field currentField, boolean isDay);
    
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
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