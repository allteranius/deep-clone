package demo.deepclone.model;

import java.util.Objects;

public class InnerClass1 {
    int a;
    float b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InnerClass1 that)) return false;
        return a == that.a && Float.compare(b, that.b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
