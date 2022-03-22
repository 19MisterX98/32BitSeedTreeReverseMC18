public class XoroRandom {
    private static final float FLOAT_MULTIPLIER = 5.9604645E-8f;
    private static final double DOUBLE_MULTIPLIER = (double)1.110223E-16f;
    private XoroImpl implementation;

    public XoroRandom(long seed) {
        this.implementation = new XoroImpl(createXoroshiroSeed(seed));
    }

    public static long nextSplitMix64Int(long seed) {
        seed = (seed ^ seed >>> 30) * -4658895280553007687L;
        seed = (seed ^ seed >>> 27) * -7723592293110705685L;
        return seed ^ seed >>> 31;
    }

    public static XoroshiroSeed createXoroshiroSeed(long seed) {
        long l = seed ^ 0x6A09E667F3BCC909L;
        long m = l + -7046029254386353131L;
        return new XoroshiroSeed(nextSplitMix64Int(l), nextSplitMix64Int(m));
    }
    public record XoroshiroSeed(long seedLo, long seedHi) {
    }

    public XoroRandom(long seedLo, long seedHi) {
        this.implementation = new XoroImpl(seedLo, seedHi);
    }

    public void copySeedTo(XoroRandom random) {
        random.implementation.seedHi = this.implementation.seedHi;
        random.implementation.seedLo = this.implementation.seedLo;
    }

    public void setSeed(long l) {
        this.implementation = new XoroImpl(createXoroshiroSeed(l));
    }

    public int nextInt() {
        return (int)this.implementation.next();
    }

    public int nextInt(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        long l = Integer.toUnsignedLong(this.nextInt());
        long m = l * (long)i;
        long n = m & 0xFFFFFFFFL;
        if (n < (long)i) {
            int j = Integer.remainderUnsigned(~i + 1, i);
            while (n < (long)j) {
                l = Integer.toUnsignedLong(this.nextInt());
                m = l * (long)i;
                n = m & 0xFFFFFFFFL;
            }
        }
        long j = m >> 32;
        return (int)j;
    }

    public float nextFloat() {
        return (float)this.next(24) * 5.9604645E-8f;
    }

    public long nextLong() {
        return this.implementation.next();
    }


    public void skip(int count) {
        for (int i = 0; i < count; ++i) {
            this.implementation.next();
        }
    }

    /**
     * {@return {@code bits} upper bits of random value}
     *
     * @implNote In Xoroshiro128++, the lower bits have to be discarded in order
     * to ensure proper randomness. For example, to obtain a double, the upper 53
     * bits, instead of the lower 53 bits.
     */
    private long next(int bits) {
        return this.implementation.next() >>> 64 - bits;
    }
}