package com.bender.mpdlib.commands;

/**
 * todo: replace with documentation
 */
public class Tuple<K, V>
{
    private final K first;
    private final V second;

    public Tuple(K val1, V val2)
    {
        first = val1;
        second = val2;
    }

    public K first()
    {
        return first;
    }

    public V second()
    {
        return second;
    }
}
