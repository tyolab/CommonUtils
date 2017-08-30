package au.com.tyo.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 29/8/17.
 */

public class ItemIndexable extends ItemSerializable implements Indexable {

    private int index;

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void serialise(ObjectOutputStream stream) throws IOException {
        stream.writeInt(index);
    }

    @Override
    public void deserialise(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        index = stream.readInt();
    }
}
