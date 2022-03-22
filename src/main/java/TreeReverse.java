import java.util.ArrayList;
import java.util.List;

public class TreeReverse {

    public static void main(String[] args) {
        //step 9 index 20
        //10 tries, 10% -> 11 -> 1x nextint
        //loop:
        //  choose: BIRCH_BEES_0002_0.2F ; FANCY_OAK_BEES_0002_0.1F ; OAK_BEES_0002 -> 1-2x nextFloat
        //  some additional random calls
        //  get blockpos 2x nextint16
        //  generate: trunkheight(4-6) 1x nextint(3)
        //            leave 16x nextint(2)
        //  beehive decorator (it should fail with 1x nextfloat)
        List<Thread> list = new ArrayList<>();
        int part = Integer.MAX_VALUE/4;
        for (long i = -4; i < 4; i++) {

            long start = i * part;
            long end = i * part + part;

            System.out.println();
            System.out.println("kernel: " + (i + 4));
            System.out.println((int) start);
            System.out.println((int) end);
            list.add(new Thread(() -> {
                try {
                    submittable((int) start, (int) end);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }
        for (Thread thread : list) {
            thread.start();
        }
        for (Thread thread : list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void submittable(int start, int end) {
        long time = System.currentTimeMillis();
        //layout: chunkX, chunkZ, trunkHeight, leaves(1: leave, 0: air, -1: unknown)
        //leave layout: https://cdn.discordapp.com/attachments/766084065795244092/953750827544223794/unknown.png
        //wool is orientated like the f3 crosshair
        int[][] trees = {
                {3,2,  1, 0,-1,0,1,    1,-1,1,0,    0,-1,1,0},
                {0,5,  2, 1,-1,1,0,    0,-1,-1,-1,  0,-1,-1,-1},
                {4,8,  1, 0,-1,0,-1,   0,-1,1,-1,   -1,-1,1,1},
                {12,4, 2, 1,1,0,-1,    0,-1,0,-1,   1,-1,1,-1},
                {10,7, 0, 1,-1,-1,-1,  0,0,1,-1,    1,1,0,-1},
                {9,13, 1, 0,-1,1,-1,   0,-1,0,-1,   0,-1,1,-1}
        };

        List<Integer> defaultList = new ArrayList<>();
        for (int treeNum = 0; treeNum < trees.length; treeNum++) {
            defaultList.add(treeNum);
        }

        XoroRandom xoroRandom = new XoroRandom(1);
        ChunkRandom random = new ChunkRandom(xoroRandom);
        XoroRandom leaveXoroRandom = new XoroRandom(1);
        ChunkRandom leaveRandom = new ChunkRandom(leaveXoroRandom);

        seedloop:
        for (int intSeed = start; intSeed < end; intSeed++) {

            long seed = (long)intSeed;

            if (seed % 10000000 == 0) {
                if (start == 0) {
                    System.out.println(((float) seed * 100 / end) + "% at second " + ((System.currentTimeMillis() - time) / 1000));
                }
            }

            //the coords of the most negative corner of our chunk
            long popseed = random.setPopulationSeed(seed,64,-96);

            popseed += (9*10000)+20;
            random.setSeed(popseed);

            List<Integer> copyTreePositions = new ArrayList<>(defaultList);

            int x = random.nextInt(16);

            //placement attempts, loose bound
            for (int i = 0; i < 100; i++) {
                int z = random.nextInt(16);

                treeCheckLoop:
                for (int treeNum : copyTreePositions) {
                    int[] tree = trees[treeNum];
                    if (x == tree[0] && z == tree[1]) {
                        xoroRandom.copySeedTo(leaveXoroRandom);
                        leaveXoroRandom.skip(2);
                        if (leaveRandom.nextInt(3) == tree[2]) {
                            //1 burned call for second height & 4 for the upper leaves that never spawn
                            leaveXoroRandom.skip(5);
                            for (int leave = 3; leave < 15; leave++) {
                                int leaveData = tree[leave];
                                if (leaveData != -1 && leaveRandom.nextInt(2) != leaveData) {
                                    break treeCheckLoop;
                                }
                            }
                            leaveXoroRandom.copySeedTo(xoroRandom);
                            z = random.nextInt(16);
                            copyTreePositions.remove((Integer) treeNum);
                            if (copyTreePositions.size() < 2) {
                                System.out.println("seed: "+intSeed);
                                continue seedloop;
                            }
                            break;
                        }
                    }
                }
                x = z;
            }
        }
    }
}
