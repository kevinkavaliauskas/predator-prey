import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Testing git repo collaboration
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.06;
    // The probability that a rabbit will be created in any given position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.16;
    // The probability that a plant will be created in any given position.
    private static final double PLANT_CREATION_PROBABILITY = 0.20;
    
    // The Current State of Day/Night. Changes To true if day, changes 
    // to false if night.
    private Boolean isDay;

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // A weather object for the world
    private Weather weather;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);
        weather = new Weather();
        weather.setRandomWeather();
        
        isDay = true; //Sets the simulator to start during the day.

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        
        isDay = !isDay; //Flips the day/night state.
        
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());
        
        if (step % 5 == 0) {
            weather.setRandomWeather();
        }
        

        List<Entity> animals = field.getAnimals();
        for (Entity anAnimal : animals) {
            anAnimal.act(field, nextFieldState, isDay, weather.getWeather());
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field, weather);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field, weather);
    }
    
    /**
     * Randomly populate the field with foxes, rabbits and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {

                double probability = rand.nextDouble(); //Generate one random number
                Location location = new Location(row, col);
                
                if(probability <= FOX_CREATION_PROBABILITY) {
                    Fox fox = new Fox(true, location);
                    field.placeAnimal(fox, location);
                }
                else if(probability <= (FOX_CREATION_PROBABILITY + RABBIT_CREATION_PROBABILITY)) {
                    Rabbit rabbit = new Rabbit(true, location);
                    field.placeAnimal(rabbit, location);
                }
                else if(probability <= (FOX_CREATION_PROBABILITY + RABBIT_CREATION_PROBABILITY + PLANT_CREATION_PROBABILITY)) {
                    Plant plant = new Plant(true, location);
                    field.placeAnimal(plant, location);
                    // else leave the location empty.
                    
                }
            }
        }
    }

    /**
     * Report on the number of each type of animal in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
}
