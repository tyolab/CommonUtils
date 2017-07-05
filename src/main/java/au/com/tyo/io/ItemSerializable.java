package au.com.tyo.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @FILE ${FILE_NAME}
 * <p>
 * Created by Eric Tang (eric.tang@tyo.com.au) on 30/5/17.
 */

public abstract class ItemSerializable implements Serializable {

    public abstract void serialise(ObjectOutputStream stream) throws IOException;

    public abstract void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException;

    public void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        serialise(stream);
    }

    public void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        deserialise(stream);
    }
}
