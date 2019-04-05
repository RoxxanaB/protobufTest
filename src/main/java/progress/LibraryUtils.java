package progress;

import com.infinityworks.google.model.LibraryModel;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.util.Optional;

public class LibraryUtils {

    public static  Optional<LibraryModel.Library> deserialization(byte[] deserializationBytes) {
        // TODO throw runtime exception for this error
        try {
            ByteInputStream inputStream = new ByteInputStream();
            inputStream.setBuf(deserializationBytes);

            return Optional.of((LibraryModel.Library.parseFrom(inputStream)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public static byte[] serialization(LibraryModel.Library library) {
        return library.toByteArray();
    }


}
