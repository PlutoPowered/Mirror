/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.mirror;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Reflections {
    private static Map<String, Class> primitivesByName;
    private static Map<Class, Class> primitivesByBox;
    private static Map<Class, Class> boxByPrimitives;

    static {
        Reflections.primitivesByName = new HashMap<>();
        Reflections.primitivesByBox = new HashMap<>();
        Reflections.boxByPrimitives = new HashMap<>();

        Reflections.primitivesByName.put("byte", byte.class);
        Reflections.primitivesByName.put("short", short.class);
        Reflections.primitivesByName.put("char", char.class);
        Reflections.primitivesByName.put("int", int.class);
        Reflections.primitivesByName.put("long", long.class);
        Reflections.primitivesByName.put("float", float.class);
        Reflections.primitivesByName.put("double", double.class);
        Reflections.primitivesByName.put("boolean", boolean.class);
        Reflections.primitivesByName.put("void", void.class);

        Reflections.primitivesByBox.put(Byte.class, byte.class);
        Reflections.primitivesByBox.put(Short.class, short.class);
        Reflections.primitivesByBox.put(Character.class, char.class);
        Reflections.primitivesByBox.put(Integer.class, int.class);
        Reflections.primitivesByBox.put(Long.class, long.class);
        Reflections.primitivesByBox.put(Float.class, float.class);
        Reflections.primitivesByBox.put(Double.class, double.class);
        Reflections.primitivesByBox.put(Boolean.class, boolean.class);
        Reflections.primitivesByBox.put(Void.class, void.class);

        Reflections.boxByPrimitives.put(byte.class, Byte.class);
        Reflections.boxByPrimitives.put(short.class, Short.class);
        Reflections.boxByPrimitives.put(char.class, Character.class);
        Reflections.boxByPrimitives.put(int.class, Integer.class);
        Reflections.boxByPrimitives.put(long.class, Long.class);
        Reflections.boxByPrimitives.put(float.class, Float.class);
        Reflections.boxByPrimitives.put(double.class, Double.class);
        Reflections.boxByPrimitives.put(boolean.class, Boolean.class);
        Reflections.boxByPrimitives.put(void.class, Void.class);
    }

    public static Optional<Class> resolveClass(String name) {
        try {
            return Optional.of(Reflections.forName(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Class forName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
        return Reflections.primitivesByName.containsKey(name) ? Reflections.primitivesByName.get(name) : Class.forName(name, initialize, loader);
    }

    public static Class forName(String name) throws ClassNotFoundException {
        return Reflections.primitivesByName.containsKey(name) ? Reflections.primitivesByName.get(name) : Class.forName(name);
    }

    public static Class boxingType(Class primitive) {
        return Reflections.boxByPrimitives.containsKey(primitive) ? Reflections.boxByPrimitives.get(primitive) : primitive;
    }

    public static Class primitiveType(Class boxing) {
        return Reflections.primitivesByBox.containsKey(boxing) ? Reflections.primitivesByBox.get(boxing) : boxing;
    }

    public static boolean fieldExists(String clazz, String name) {
        try {
            Class target = Reflections.forName(clazz);
            target.getField(name);
            return true;
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean classExists(String name) {
        try {
            Reflections.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static <T> T deepCast(Class<T> type, Object obj) {
        if (obj == null || type == Object.class) {
            return type.cast(obj);
        } else if (Reflections.canCastNumber(obj, Reflections.boxingType(type))) {
            return (T) Reflections.castNumber(obj, Reflections.boxingType(type));
        } else if (!obj.getClass().isArray()) {
            return (T) Reflections.boxingType(type).cast(obj);
        } else if (obj.getClass().getComponentType().isAssignableFrom(Object[].class)) {
            try {
                Object[] arr = (Object[]) Reflections.deepArrayCast(type, obj, new IdentityHashMap<>());
                Reflections.transform(arr, a -> a instanceof Reference ? ((Reference) a).getVal() : a);
                return type.cast(arr);
            } catch (ArrayStoreException e) {
                throw new ClassCastException();
            }
        } else if (obj.getClass().isArray() && type.isArray()) {
            int len = Array.getLength(obj);
            Object newArr = Array.newInstance(type.getComponentType(), len);
            for (int i = 0; i < len; i++) {
                Array.set(newArr, i, Reflections.deepCast(type.getComponentType(), Array.get(obj, i)));
            }
            return type.cast(newArr);
        } else {
            return type.cast(obj);
        }
    }

    private static Object deepArrayCast(Class type, Object obj, Map<Object, Reference> seen) {
        if (obj == null || type == Object.class) {
            return obj;
        } else if (Reflections.canCastNumber(obj, Reflections.boxingType(type))) {
            return Reflections.castNumber(obj, Reflections.boxingType(type));
        } else if (Reflections.boxingType(type).isInstance(obj)) {
            return obj;
        } else if (!obj.getClass().isArray()) {
            return Reflections.boxingType(type).cast(obj);
        } else if (obj.getClass().getComponentType().isAssignableFrom(Object[].class)) {
            if (seen.containsKey(obj)) {
                return seen.get(obj);
            } else {
                int len = Array.getLength(obj);
                Object newArr = Array.newInstance(type.getComponentType(), len);
                Reference reference = new Reference(newArr);
                seen.put(obj, reference);
                for (int i = 0; i < len; i++) {
                    Array.set(newArr, i, Reflections.deepArrayCast(type.getComponentType(), Array.get(obj, i), seen));
                }
                return newArr;
            }
        } else {
            int len = Array.getLength(obj);
            Object newArr = Array.newInstance(type.getComponentType(), len);
            for (int i = 0; i < len; i++) {
                Array.set(newArr, i, Reflections.deepCast(type.getComponentType(), Array.get(obj, i)));
            }
            return newArr;
        }
    }

    private static Object castNumber(Object val, Class target) {
        if (target.isInstance(val)) {
            return val;
        }

        if (val instanceof Number) {
            Number number = (Number) val;
            if (target == Long.class) {
                return number.longValue();
            } else if (target == Integer.class) {
                return number.intValue();
            } else if (target == Short.class) {
                return number.shortValue();
            } else if (target == Byte.class) {
                return number.byteValue();
            } else if (target == Double.class) {
                return number.doubleValue();
            } else if (target == Float.class) {
                return number.floatValue();
            } else if (target == BigDecimal.class) {
                if (number instanceof BigInteger) {
                    return new BigDecimal((BigInteger) number);
                } else if (number.doubleValue() - ((int) number.doubleValue()) == 0) {
                    return BigDecimal.valueOf(number.longValue());
                } else {
                    return BigDecimal.valueOf(number.doubleValue());
                }
            } else if (target == BigInteger.class) {
                if (number instanceof BigDecimal) {
                    return ((BigDecimal) number).toBigInteger();
                } else {
                    return BigInteger.valueOf(number.longValue());
                }
            } else if (target == Boolean.class) {
                return number.intValue() > 0;
            }
            return number;
        } else {
            Boolean bool = (Boolean) val;
            return Reflections.castNumber(bool ? 1 : 0, target);
        }
    }

    private static boolean canCastNumber(Object a, Class target) {
        return (a instanceof Number && Number.class.isAssignableFrom(target)) ||
                (a instanceof Boolean && Number.class.isAssignableFrom(target)) ||
                (a instanceof Number && Boolean.class.isAssignableFrom(target)) ||
                (a instanceof Boolean && Boolean.class.isAssignableFrom(target));
    }

    private static class Reference {
        private Object val;

        public Reference(Object val) {
            this.val = val;
        }

        public Object getVal() {
            return this.val;
        }

        public void setVal(Object val) {
            this.val = val;
        }
    }

    private static void transform(Object[] array, Function<Object, Object> action) {
        Reflections.transform(array, action, new IdentityList<>());
    }

    private static void transform(Object[] array, Function<Object, Object> action, List<Object> seen) {
        if (!seen.contains(array)) {
            seen.add(array);
            for (int i = 0; i < array.length; i++) {
                Object obj = array[i];
                if (obj != null && obj.getClass().isArray() && obj.getClass().getComponentType().isAssignableFrom(Object[].class)) {
                    Reflections.transform((Object[]) obj, action, seen);
                } else {
                    array[i] = action.apply(obj);
                }
            }
        }
    }

    private static class IdentityList<E> extends ArrayList<E> {

        private static final long serialVersionUID = 1L;

        public IdentityList() {
            super();
        }

        public IdentityList(final Collection<? extends E> c) {
            super(c);
        }

        public IdentityList(final int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public boolean remove(final Object o) {
            return super.remove(this.indexOf(o)) != null;
        }

        @Override
        public boolean contains(final Object o) {
            return indexOf(o) >= 0;
        }

        @Override
        public int indexOf(final Object o) {
            for (int i = 0; i < size(); i++)
                if (o == get(i))
                    return i;
            return -1;
        }

        @Override
        public int lastIndexOf(final Object o) {
            for (int i = size() - 1; i >= 0; i--)
                if (o == get(i))
                    return i;
            return -1;
        }

    }

}
