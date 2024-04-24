package demo.deepclone;

import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Man man = new Man("Alex", 27, List.of("3-body problem", "Expanse"));// create Object
        Man copyMan = CopyUtils.deepCopy(man); //Copy object
        System.out.println(man); // Print Initial Object
        man.setAge(13);
        man.setName(null);
        man.setFavoriteBooks(Collections.emptyList()); // Change Initial object
        System.out.println(copyMan); // Copy didn't change
    }
}