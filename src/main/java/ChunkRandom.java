import java.util.Random;

public class ChunkRandom extends Random {

    private final XoroRandom baseRandom;

    public ChunkRandom(XoroRandom baseRandom) {
        super(0L);
        this.baseRandom = baseRandom;
    }

    @Override
    public int next(int count) {
        return (int)(this.baseRandom.nextLong() >>> 64 - count);
    }

    @Override
    public synchronized void setSeed(long l) {
        if (this.baseRandom == null) {
            return;
        }
        this.baseRandom.setSeed(l);
    }

    /**
     * Seeds the randomizer to create population features such as decorators and animals.
     *
     * <p>This method takes in the world seed and the negative-most block coordinates of the
     * chunk. The coordinate pair provided is equivalent to (chunkX * 16, chunkZ * 16). The
     * three values are mixed together through some layers of hashing to produce the
     * population seed.
     *
     * <p>This function has been proved to be reversible through some exploitation of the underlying
     * nextLong() weaknesses. It is also important to remember that since setSeed()
     * truncates the 16 upper bits of world seed, only the 48 lowest bits affect the population
     * seed output.
     */
    public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong() | 1L;
        long m = this.nextLong() | 1L;
        long n = (long)blockX * l + (long)blockZ * m ^ worldSeed;
        this.setSeed(n);
        return n;
    }

    /**
     * Seeds the randomizer to generate a given feature.
     *
     * The salt, in the form of {@code index + 10000 * step} assures that each feature is seeded
     * differently, making the decoration feel more random. Even though it does a good job
     * at doing so, many entropy issues arise from the salt being so small and result in
     * weird alignments between features that have an index close apart.
     *
     * @param index the index of the feature in the feature list
     * @param step the generation step's ordinal for this feature
     * @param populationSeed the population seed computed in {@link #setPopulationSeed(long, int, int)}
     */
    public void setDecoratorSeed(long populationSeed, int index, int step) {
        long l = populationSeed + (long)index + (long)(10000 * step);
        this.setSeed(l);
    }
}
