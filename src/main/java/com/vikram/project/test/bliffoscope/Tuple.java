package com.vikram.project.test.bliffoscope;

import java.util.Objects;

public class Tuple<X, Y> {
    public final X x;
    public final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(x, tuple.x) &&
                Objects.equals(y, tuple.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


    public static int compare(Tuple<Integer, Integer> a, Tuple<Integer, Integer> b) {
        if (a.x > (b.x) || a.x.equals(b.x) && a.y > b.y) return 1;
        else if (a.x.equals(b.x) && a.y.equals(b.y)) return 0;
        else return -1;
    }
}
