package fr.en0ri4n.pickthemall.utils;

import java.util.List;
import java.util.Random;

public class Randomizer
{
    private static final Randomizer INSTANCE = new Randomizer();

    private final Random random = new Random();

    public Random getRand()
    {
        random.setSeed(random.nextLong());
        return random;
    }

    public static int randomInt(Integer[] range)
    {
        return INSTANCE.getRand().nextInt(range[0], range[1]);
    }

    public static <T> T random(List<T> list)
    {
        return list.get(INSTANCE.getRand().nextInt(list.size()));
    }
    public static Integer randomRange(List<Integer> list)
    {
        return INSTANCE.getRand().nextInt(list.get(0), list.get(1));
    }

    public static <T> T random(T[] list)
    {
        return list[INSTANCE.getRand().nextInt(list.length)];
    }

    public static Random getRandom()
    {
        return INSTANCE.getRand();
    }
}
