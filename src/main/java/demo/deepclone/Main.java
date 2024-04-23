package demo.deepclone;

import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Man man = new Man("Alex", 27, List.of("3-body problem", "Expanse"));
        Man copyMan = CopyUtils.deepCopy(man);
        System.out.println(man);
        man.setAge(13);
        man.setName(null);
        man.setFavoriteBooks(Collections.emptyList());
        System.out.println(copyMan);
    }
}