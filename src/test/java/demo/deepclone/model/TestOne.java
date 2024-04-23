package demo.deepclone.model;

public class TestOne {
    public InnerClass1 A;
    private InnerClass1 B;

    protected  InnerClass1 c;
    InnerClass1 d;

    TestOne self;

    public InnerClass1 getA() {
        return A;
    }

    public void setA(InnerClass1 a) {
        A = a;
    }

    public InnerClass1 getB() {
        return B;
    }

    public void setB(InnerClass1 b) {
        B = b;
    }

    public InnerClass1 getC() {
        return c;
    }

    public void setC(InnerClass1 c) {
        this.c = c;
    }

    public InnerClass1 getD() {
        return d;
    }

    public void setD(InnerClass1 d) {
        this.d = d;
    }

    public TestOne getSelf() {
        return self;
    }

    public void setSelf(TestOne self) {
        this.self = self;
    }
}
