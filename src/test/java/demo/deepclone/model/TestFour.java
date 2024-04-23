package demo.deepclone.model;

import java.util.List;

public class TestFour<T> {
    private List<T> list;

    public T getHead() {
        return head;
    }

    public void setHead(T head) {
        this.head = head;
    }

    private T head;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
