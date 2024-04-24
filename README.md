# Intro
It is the solution for task Deep-clone, an initial task in Deep-clone.md file
# Solution
Reflection API is used to make a deep copy. А new object is created by this API and all field copy recursively.
But all abject is saving in cache during coping. If this object has been copied already, return its copy.
If it is a same object in an initial instance, it should be a same in copy.
If a field is a primitive type, or boxed primitives, or String method returns input object because this types is immutable or already written in new object.
# Tech stack
Java 21, Gradle, JUnit
# Limitations
Each class in copied object must have public constructor or be an Enum.
