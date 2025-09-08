package util.io;

import java.io.OutputStream;
import java.io.IOException;

/**
 * An OutputStream that duplicates all data written to it to two separate OutputStreams.
 * <p>
 * This class is useful when you want to write the same data to multiple destinations
 * simultaneously, such as logging output to both a file and the console.
 * <p>
 * Note that this class does not handle synchronization for concurrent writes. If multiple
 * threads may write to the TeeStream concurrently, external synchronization is required.
 */
public class TeeStream extends OutputStream {
    private final OutputStream a;
    private final OutputStream b;
    /**
     * Creates a new TeeStream that duplicates all data written to it to the two provided
     * OutputStreams.
     *
     * @param a the first OutputStream
     * @param b the second OutputStream
     */
    public TeeStream(OutputStream a, OutputStream b) {
        this.a = a;
        this.b = b;
    }
    /** {@inheritDoc} */
    @Override
    public void write(int bVal) throws IOException {
        a.write(bVal);
        b.write(bVal);
    }
    /** {@inheritDoc} */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.a.write(b, off, len);
        this.b.write(b, off, len);
    }
    /** {@inheritDoc} */
    @Override
    public void flush() throws IOException {
        a.flush();
        b.flush();
    }
    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        a.close();
        b.close();
    }
}
