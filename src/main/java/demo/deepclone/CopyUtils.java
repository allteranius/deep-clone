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
        if (isImmutable(obj)) {
            return obj; // No need to copy immutable objects or primitive type
        }
        if (obj.getClass().isArray()) {
            return copyArray(obj, cloned);
        }
        if (obj instanceof Collection<?>) {
            return copyCollections((Collection) obj, cloned);
        }
        if (obj instanceof Map) {
            return copyMap((Map) obj, cloned);
        }
        var fieldsData = getFiledData(obj);
        T out = createInstance(obj, cloned, fieldsData.fields);
        copyFields(obj, cloned, fieldsData.fields, out);
        fieldsData.returnAccessFlag();
        return out;
    }

    private static FiledData getFiledData(Object obj) {
        var fields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())))
                .collect(Collectors.toList());
        var fieldAccessMap = new HashMap<Field, Boolean>();
        for (Field field : fields) {
            fieldAccessMap.put(field, field.canAccess(obj));
            field.setAccessible(true);
        }
        return new FiledData(fields, fieldAccessMap);
    }

    private static <T> void copyFields(T obj, Map<Integer, List<Cloned>> cloned, List<Field> fields, T out) {
        for (Field field : fields) {
            try {
                field.set(out, deepCopy(field.get(obj), cloned));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(" Error while get variable " + e.getMessage());
            }
        }
    }

    private static <T> T createInstance(T obj, Map<Integer, List<Cloned>> cloned, List<Field> fields) {
        try {
            T out;
            Constructor constructor = obj.getClass().getConstructors()[0];
            var parameters = constructor.getParameters();
            if (parameters.length > 0) {
                var args = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    findAndCopyArgs(obj, cloned, fields, parameters, i, args);
                }
                out = (T) constructor.newInstance(args);
            } else {
                out = (T) constructor.newInstance();
            }
            cloned.computeIfAbsent(obj.hashCode(), x -> new ArrayList<>()).add(new Cloned(obj, out));
            return out;
        } catch (Exception e) {
            throw new NoSuitableConstructorException("Unable to create instance of "
                    + obj.getClass() + " reason " + e.getMessage());
        }

    }

    private static <T> void findAndCopyArgs(T obj, Map<Integer, List<Cloned>> cloned, List<Field> fields, Parameter[] parameters, int i, Object[] args) {
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

    private static <T> T copyMap(Map obj, Map<Integer, List<Cloned>> cloned) {
        var map = obj;
        Map copyMap = null;
        try {
            copyMap = createInstance(map);
        } catch (Exception e) {
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

    private static <T> T copyCollections(Collection collections, Map<Integer, List<Cloned>> cloned) {
        Collection collectionsCopy;
        try {
            collectionsCopy = createInstance(collections);
        } catch (Exception e) {
            if (collections instanceof List<?>) {
                collectionsCopy = new ArrayList();
            } else if (collections instanceof Set) {
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

    private static <T> T copyArray(T obj, Map<Integer, List<Cloned>> cloned) {
        var size = Array.getLength(obj);
        var copyArray = Array.newInstance(obj.getClass().getComponentType(), size);
        for (int i = 0; i < size; i++) {
            var element = Array.get(obj, i);
            var elementCopy = deepCopy(element, cloned);
            Array.set(copyArray, i, elementCopy);
        }
        return (T) copyArray;
    }

    private static <T> boolean isImmutable(T obj) {
        return obj.getClass().isPrimitive()
                || obj.getClass().isEnum()
                || obj instanceof Number
                || obj instanceof Character
                || obj instanceof String
                || obj instanceof Boolean;
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

    private static <T> T createInstance(T obj) {
        try {
            return (T) obj.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new NoSuitableConstructorException("Unable to create instance of "
                    + obj.getClass() + " reason " + e.getMessage());
        }

    }

    private record Cloned(Object origin, Object cloned) {
    }

    private record FiledData(List<Field> fields, HashMap<Field, Boolean> fieldAccessMap) {
        public void returnAccessFlag() {
            for (Map.Entry<Field, Boolean> entry : fieldAccessMap.entrySet()) {
                entry.getKey().setAccessible(entry.getValue());
            }
        }
    }
}
