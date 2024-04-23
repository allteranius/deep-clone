package demo.deepclone;

import demo.deepclone.exceptions.NoSuitableConstructorException;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class CopyUtils {

    public static <T> T deepCopy(T obj) {
        return deepCopy(obj, new HashMap<>());

    }

    private static <T> T deepCopy(T obj, Map<Integer, List<Cloned>> cloned) {
        if (obj == null) {
            return null;
        }
        var cached = checkObjectInCache(obj, cloned);
        if (cached != null) {
            return cached;
        }
        if (obj.getClass().isPrimitive()
                || obj.getClass().isEnum()
                || obj instanceof Number
                || obj instanceof Character
                || obj instanceof String
                || obj instanceof Boolean) {
            return obj; // No need to copy immutable objects or primitive type
        }
        if (obj.getClass().isArray()) {
            var size = Array.getLength(obj);
            var copyArray = Array.newInstance(obj.getClass().getComponentType(), size);
            for (int i = 0; i < size; i++) {
                var element = Array.get(obj, i);
                var elementCopy = deepCopy(element, cloned);
                Array.set(copyArray, i, elementCopy);
            }
            return (T) copyArray;
        }
        if (obj instanceof Collection<?>) {
            var collections = (Collection) obj;
            Collection collectionsCopy = null;
            try {
                collectionsCopy =  createInstance(collections, null);
            } catch (Exception e){
                if(collections instanceof List<?>){
                    collectionsCopy = new ArrayList();
                } else if(collections instanceof Set){
                    collectionsCopy = new HashSet();
                } else {
                    throw e;
                }
            }
            for (Object element : collections) {
                var elementCopy = deepCopy(element, cloned);
                collectionsCopy.add(elementCopy);
            }
            return (T) collectionsCopy;
        }
        if (obj instanceof Map) {
            var map = (Map) obj;

            Map copyMap = null;
            try{
                copyMap = createInstance(map, null);
            } catch (Exception e){
                copyMap = new HashMap();
            }
            Set<Map.Entry> set = map.entrySet();
            for (Map.Entry entry : set) {
                var keyCopy = deepCopy(entry.getKey(), cloned);
                var valueCopy = deepCopy(entry.getValue(), cloned);
                copyMap.put(keyCopy, valueCopy);
            }
            return (T) copyMap;
        }

        Constructor constructor = null;
        T out = null;
        try {
            constructor = obj.getClass().getConstructors()[0];
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuitableConstructorException("Unable to create instance of "
                    + obj.getClass() + " reason " + e.getMessage());
        }
        var fields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())))
                .collect(Collectors.toList());
        var fieldAccessMap = new HashMap<Field, Boolean>();
        for (Field field : fields) {
            fieldAccessMap.put(field, field.canAccess(obj));
            field.setAccessible(true);
        }
        var parameters = constructor.getParameters();
        var args = new Object[parameters.length];
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                var parameter = parameters[i];
                var field = fields.stream()
                        .filter(x -> parameter.getType().isAssignableFrom(x.getType()))
                        .findFirst();
                if (field.isPresent()) {
                    try {
                        var copy = deepCopy(field.get().get(obj), cloned);
                        args[i] = copy;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(" Error while get variable " + e.getMessage());
                    }
                    fields.remove(field.get());
                }
            }
        }
        try {
            if (args.length > 0) {
                out = (T) constructor.newInstance(args);
            } else {
                out = (T) constructor.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoSuitableConstructorException("Unable to create instance of "
                    + obj.getClass() + " reason " + e.getMessage());
        }

        cloned.computeIfAbsent(obj.hashCode(), x -> new ArrayList<>()).add(new Cloned(obj, out));

        for (Field field : fields) {
            try {
                field.set(out, deepCopy(field.get(obj), cloned));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(" Error while get variable " + e.getMessage());
            }
        }
        for (Map.Entry<Field, Boolean> entry : fieldAccessMap.entrySet()) {
            entry.getKey().setAccessible(entry.getValue());
        }
        return out;
    }

    private static <T> T checkObjectInCache(T obj, Map<Integer, List<Cloned>> cloned) {
        var hashCode = obj.hashCode();
        if (cloned.containsKey(hashCode)) {
            var candidate = cloned.get(hashCode);
            for (Cloned o : candidate) {
                if (o.origin == obj) {
                    return (T) o.cloned;
                }
            }
        }
        return null;
    }

    private static <T> T createInstance(T obj, Object[] arg) {
        try {
            if (arg != null && arg.length > 0) {
                return (T) obj.getClass().getDeclaredConstructor().newInstance(arg);
            }
            return (T) obj.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new NoSuitableConstructorException("Unable to create instance of "
                    + obj.getClass() + " reason " + e.getMessage());
        }

    }

    private record Cloned(Object origin, Object cloned) {
    }
}
