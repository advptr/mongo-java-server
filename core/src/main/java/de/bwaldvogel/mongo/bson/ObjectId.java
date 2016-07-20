package de.bwaldvogel.mongo.bson;

import static de.bwaldvogel.mongo.wire.BsonConstants.LENGTH_OBJECTID;

import java.security.SecureRandom;
import java.util.Arrays;

public class ObjectId implements Bson, Comparable<ObjectId> {

    private static final long serialVersionUID = 1L;

    private final byte[] data = new byte[LENGTH_OBJECTID];

    public ObjectId() {
        SecureRandom random = new SecureRandom();
        random.nextBytes(data);
    }

    public ObjectId(byte[] data) {
        if (data.length != LENGTH_OBJECTID) {
            throw new IllegalArgumentException("Illegal data. Length must be " + LENGTH_OBJECTID + " but was " + data.length);
        }
        System.arraycopy(data, 0, this.data, 0, this.data.length);
    }

    public byte[] toByteArray() {
        return data;
    }

    @Override
    public int compareTo(final ObjectId other) {
        byte[] byteArray = toByteArray();
        byte[] otherByteArray = other.toByteArray();
        for (int i = 0; i < LENGTH_OBJECTID; i++) {
            if (byteArray[i] != otherByteArray[i]) {
                int thisByte = byteArray[i] & 0xFF;
                int otherByte = otherByteArray[i] & 0xFF;
                return (thisByte < otherByte) ? -1 : 1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ObjectId objectId = (ObjectId) o;

        return Arrays.equals(data, objectId.data);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        String s = getClass().getSimpleName() + "[";
        for (byte b : data) {
            s += String.format("%02x", b);
        }
        return s + "]";
    }
}