import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes and Michael Kölling and Aashwin Eldo and Kevin
 *         Kavaliauskas
 * @version 7.0
 */
public class Field {
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The dimensions of the field.
    private final int depth, width;
    // Animals mapped by location.
    private final Map<Location, Entity> field = new HashMap<>();
    // The animals.
    private final List<Entity> animals = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * 
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width) {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an entity at the given location.
     * If there is already an entity at the location it will
     * be lost.
     * 
     * @param anEntity The entity to be placed.
     * @param location Where to place the entity.
     */
    public void placeEntity(Entity anEntity, Location location) {
        assert location != null;
        Object other = field.get(location);
        if (other != null) {
            animals.remove(other);
        }
        field.put(location, anEntity);
        animals.add(anEntity);
    }

    /**
     * Return the entity at the given location, if any.
     * 
     * @param location Where in the field.
     * @return The entity at the given location, or null if there is none.
     */
    public Entity getEntityAt(Location location) {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * 
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location, int radius) {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location, radius);
        for (Location next : adjacent) {
            Entity anEntity = field.get(next);
            if (anEntity == null) {
                free.add(next);
            } else if (!anEntity.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * 
     * @param location The location from which to generate adjacencies.
     * @param radius   The number of tiles around that are to be checked.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location, int radius) {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if (location != null) {
            int row = location.row();
            int col = location.col();
            for (int roffset = -radius; roffset <= radius; roffset++) {
                int nextRow = row + roffset;
                if (nextRow >= 0 && nextRow < depth) {
                    for (int coffset = -radius; coffset <= radius; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print out the number of foxes and rabbits in the field.
     */
    public void fieldStats() {
        int numWolves = 0, numDeer = 0, numMouse = 0, numBear = 0, numPlants = 0;
        for (Entity anAnimal : field.values()) {
            if (anAnimal instanceof Wolf wolf) {
                if (wolf.isAlive()) {
                    numWolves++;
                }
            } else if (anAnimal instanceof Deer deer) {
                if (deer.isAlive()) {
                    numDeer++;
                }
            } else if (anAnimal instanceof Mouse mouse) {
                if (mouse.isAlive()) {
                    numMouse++;
                }
            } else if (anAnimal instanceof Bear bear) {
                if (bear.isAlive()) {
                    numBear++;
                }
            } else if (anAnimal instanceof Plant plant) {
                if (plant.isAlive()) {
                    numPlants++;
                }
            }
        }
        System.out.println("Deer: " + numDeer +
                " Wolves: " + numWolves +
                " Plants: " + numPlants +
                " Mice: " + numMouse +
                " Bear: " + numBear);
    }

    /**
     * Empty the field.
     */
    public void clear() {
        field.clear();
    }

    /**
     * Return whether there is at least one rabbit and one fox in the field.
     * 
     * @return true if there is at least one rabbit and one fox in the field.
     */
    public boolean isViable() {
        boolean deerFound = false;
        boolean wolfFound = false;
        boolean mouseFound = false;
        boolean bearFound = false;
        boolean plantFound = false;
        Iterator<Entity> it = animals.iterator();
        while (it.hasNext() && !(deerFound && wolfFound && mouseFound && bearFound && plantFound)) {
            Entity anAnimal = it.next();
            if (anAnimal instanceof Deer deer) {
                if (deer.isAlive()) {
                    deerFound = true;
                }
            } else if (anAnimal instanceof Wolf wolf) {
                if (wolf.isAlive()) {
                    wolfFound = true;
                }
            } else if (anAnimal instanceof Mouse mouse) {
                if (mouse.isAlive()) {
                    mouseFound = true;
                }
            } else if (anAnimal instanceof Bear bear) {
                if (bear.isAlive()) {
                    bearFound = true;
                }
            } else if (anAnimal instanceof Plant plant) {
                if (plant.isAlive()) {
                    plantFound = true;
                }
            }
        }
        return deerFound && wolfFound && mouseFound && bearFound && plantFound;
    }

    /**
     * Get the list of animals.
     */
    public List<Entity> getAnimals() {
        return animals;
    }

    /**
     * Return the depth of the field.
     * 
     * @return The depth of the field.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Return the width of the field.
     * 
     * @return The width of the field.
     */
    public int getWidth() {
        return width;
    }
}
