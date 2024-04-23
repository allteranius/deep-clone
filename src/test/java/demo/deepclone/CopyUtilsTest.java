package demo.deepclone;

import demo.deepclone.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class CopyUtilsTest {

    @Test
    void simpleClass() {
        var origin = new TestOne();
        origin.setA(new InnerClass1());
        origin.setB(new InnerClass1());
        origin.setC(new InnerClass1());
        origin.setD(new InnerClass1());
        origin.getA().setA(1);
        origin.getA().setB(2);
        origin.getB().setA(3);
        origin.getB().setB(4);
        origin.getC().setA(5);
        origin.getC().setB(6);
        origin.getD().setA(7);
        origin.getD().setB(8);
        origin.setSelf(origin);
        var copy = CopyUtils.deepCopy(origin);
        origin.getA().setA(10);
        origin.getA().setB(20);
        origin.getB().setA(30);
        origin.getB().setB(40);
        origin.getC().setA(50);
        origin.getC().setB(60);
        origin.getD().setA(70);
        origin.getD().setB(80);
        Assertions.assertNotSame(origin, copy);
        Assertions.assertEquals(1, copy.getA().getA());
        Assertions.assertEquals(2, copy.getA().getB());
        Assertions.assertEquals(3, copy.getB().getA());
        Assertions.assertEquals(4, copy.getB().getB());
        Assertions.assertEquals(5, copy.getC().getA());
        Assertions.assertEquals(6, copy.getC().getB());
        Assertions.assertEquals(7, copy.getD().getA());
        Assertions.assertEquals(8, copy.getD().getB());
        Assertions.assertSame(copy, copy.getSelf());
    }

    @Test
    void testRecursion() {
        TestTwo origin = new TestTwo();
        var head = new Head();
        var body = new Body();
        var tail = new Tail();
        head.setBody(body);
        body.setTail(tail);
        tail.setHead(head);
        origin.setHead(head);
        var copy = CopyUtils.deepCopy(origin);
        Assertions.assertSame(copy.getHead(), copy.getHead().getBody().getTail().getHead());
    }

    @Test
    void testCollections() {
        var origin = new TestThree();
        origin.setNumbers(new ArrayList<>());
        origin.getNumbers().add(1);
        origin.getNumbers().add(2);
        origin.getNumbers().add(3);
        origin.setMap(new HashMap<>());
        origin.getMap().put(1, "1");
        origin.getMap().put(2, "2");
        var copy = CopyUtils.deepCopy(origin);
        origin.getMap().clear();
        origin.getNumbers().clear();
        Assertions.assertEquals(3, copy.getNumbers().size());
        Assertions.assertEquals(1, copy.getNumbers().get(0));
        Assertions.assertEquals(2, copy.getNumbers().get(1));
        Assertions.assertEquals(3, copy.getNumbers().get(2));
        Assertions.assertEquals("1", copy.getMap().get(1));
        Assertions.assertEquals("2", copy.getMap().get(2));
    }

    @Test
    void testGeneric() {
        var intOrigin = new TestFour<Integer>();
        intOrigin.setList(new ArrayList<>());
        intOrigin.setHead(1);
        intOrigin.getList().add(2);
        intOrigin.getList().add(3);
        var stringOrigin = new TestFour<String>();
        stringOrigin.setList(new ArrayList<>());
        stringOrigin.setHead("1");
        stringOrigin.getList().add("2");
        stringOrigin.getList().add("3");
        var copyInt = CopyUtils.deepCopy(intOrigin);
        var copyString = CopyUtils.deepCopy(stringOrigin);
        Assertions.assertEquals(1, copyInt.getHead());
        Assertions.assertEquals("1", copyString.getHead());
    }

    @Test
    void testRecord() {
        var origin = new TestFive();
        origin.setRecordTest(new RecordTest(1, new InnerClass1()));
        origin.getRecordTest().b().setB(2);
        CopyUtils.deepCopy(origin);

    }
}
