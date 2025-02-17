
/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Plant extends Entity
{
    // Whether the plant is alive or not.
    private boolean alive;
    // The plant's current food value..
    private int height;
    // Number of steps since last rain weather
    private int lastRain = 0;
    //The Max height a plant can grow too.
    private int maxHeight = 5;
    
    

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Plant(boolean alive, Location location, boolean randomHeight)
    {
        super(location);
        this.alive = true;
        if (randomHeight) {
            setHeight(rand.nextInt(maxHeight));
        }
        else{
            setHeight(0); //If not randomly spawned in, sets the height to 0.
        }
    }
    
    private void setHeight(int height){
        this.height = height;
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    public void act(Field currentField, Field nextFieldState, Boolean isDay, String weather)
    {

        if (weather.equals("rain")) {
            lastRain = 0;
        }
        
        //10% chance of entity dying to extreme weather.
        deathToExtremeWeather(weather);
        
        if (weather.equals("sunny") || weather.equals("rain") && lastRain < 100){
            // Plants can only grow in sun or rain
            grow();
        }
        
        if (this.getLocation() != null) {
            // Plants stay in the same location
            nextFieldState.placeAnimal(this, this.getLocation());
        }
        
        lastRain++;
    }
    
    private void grow() {
        height += 1;
        if (height >= maxHeight) {
            height = maxHeight;
        }
    }
    
    private int getMaxHeight(){
        return maxHeight;
    }
    
    public int getHeight(){
        return height;
    }
    


    public String toString() {
        return "Plant{" +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

}
