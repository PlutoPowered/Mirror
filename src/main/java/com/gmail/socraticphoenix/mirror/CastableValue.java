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

import java.util.Optional;

public class CastableValue {
    protected Object value;
    protected Class type;

    protected CastableValue(Object value) {
        this.value = value;
        this.type = value == null ? Void.class : value.getClass();
    }

    public static CastableValue of(Object value) {
        return new CastableValue(value);
    }

    public Class type() {
        return this.type;
    }

    public Object rawValue() {
        return this.value;
    }

    public String rawToString() {
        return String.valueOf(this.value);
    }

    public <T> Optional<T> directCast(Class<T> type) {
        if (this.isNull()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(type.cast(this.value));
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }
    }

    public <T> Optional<T> cast(Class<T> type) {
        if (this.isNull()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(Reflections.deepCast(type, this.value));
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }
    }

    public <T> Optional<T> getAs(Class<T> type) {
        return this.cast(type);
    }

    public <T> T getAs(Class<T> type, T def) {
        return this.cast(type).orElse(def);
    }

    public <T> T getAsOrNull(Class<T> type) {
        return this.cast(type).orElse(null);
    }

    public Optional<Byte> getAsByte() {
        return this.getAs(Byte.class);
    }

    public Byte getAsByte(Byte def) {
        return this.getAs(Byte.class, def);
    }

    public Byte getAsByteOrNull() {
        return this.getAsOrNull(Byte.class);
    }

    public Optional<Short> getAsShort() {
        return this.getAs(Short.class);
    }

    public Short getAsShort(Short def) {
        return this.getAs(Short.class, def);
    }

    public Short getAsShortOrNull() {
        return this.getAsOrNull(Short.class);
    }

    public Optional<Character> getAsCharacter() {
        return this.getAs(Character.class);
    }

    public Character getAsCharacter(Character def) {
        return this.getAs(Character.class, def);
    }

    public Character getAsCharacterOrNull() {
        return this.getAsOrNull(Character.class);
    }

    public Optional<Integer> getAsInteger() {
        return this.getAs(Integer.class);
    }

    public Integer getAsInteger(Integer def) {
        return this.getAs(Integer.class, def);
    }

    public Integer getAsIntegerOrNull() {
        return this.getAsOrNull(Integer.class);
    }

    public Optional<Long> getAsLong() {
        return this.getAs(Long.class);
    }

    public Long getAsLong(Long def) {
        return this.getAs(Long.class, def);
    }

    public Long getAsLongOrNull() {
        return this.getAsOrNull(Long.class);
    }

    public Optional<Float> getAsFloat() {
        return this.getAs(Float.class);
    }

    public Float getAsFloat(Float def) {
        return this.getAs(Float.class, def);
    }

    public Float getAsFloatOrNull() {
        return this.getAsOrNull(Float.class);
    }

    public Optional<Double> getAsDouble() {
        return this.getAs(Double.class);
    }

    public Double getAsDouble(Double def) {
        return this.getAs(Double.class, def);
    }

    public Double getAsDoubleOrNull() {
        return this.getAsOrNull(Double.class);
    }

    public Optional<Boolean> getAsBoolean() {
        return this.getAs(Boolean.class);
    }

    public Boolean getAsBoolean(Boolean def) {
        return this.getAs(Boolean.class, def);
    }

    public Boolean getAsBooleanOrNull() {
        return this.getAsOrNull(Boolean.class);
    }

    public Optional<String> getAsString() {
        return this.getAs(String.class);
    }

    public String getAsString(String def) {
        return this.getAs(String.class, def);
    }

    public String getAsStringOrNull() {
        return this.getAsOrNull(String.class);
    }

    public boolean isNull() {
        return this.value == null;
    }


}
