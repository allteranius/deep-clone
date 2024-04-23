# Intro
It is solution for task Deep-clone, initial task in Deep-clone.md file

# Solution
Reflection api is used to make deep copy. New object is created by this api and all field copy recursively.
But all abject is saving in cache during coping. 
If it is same object in initial instance, it should be same in copy.
If field is primitive type, or boxed primitives, or String method return input object because this types is immutable or already written in new object.

# Limitations
Each class in copied object must have zero-parameter constructor. Because of this record isn't supported by this version.
