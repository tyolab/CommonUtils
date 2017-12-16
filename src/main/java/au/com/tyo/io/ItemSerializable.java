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

    private static final long serialVersionUID = 4642611318679780699L;

    public abstract void serialise(ObjectOutputStream stream) throws IOException;

    public abstract void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException;

    /**
     *
     * Has to be exact like this
     *
     * @param stream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        serialise(stream);
    }

    /**
     *
     * Has to be exact like this
     *
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        deserialise(stream);
    }
}
