public class XoroRandom {
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

    public void copySeedTo(XoroRandom random) {
        random.implementation.seedHi = this.implementation.seedHi;
        random.implementation.seedLo = this.implementation.seedLo;
    }

    public void setSeed(long l) {
        this.implementation = new XoroImpl(createXoroshiroSeed(l));
    }

    public long nextLong() {
        return this.implementation.next();
    }


    public void skip(int count) {
        for (int i = 0; i < count; ++i) {
            this.implementation.next();
        }
    }
}