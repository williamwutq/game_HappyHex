package hexio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import hexio.hexdata.*;
import java.io.IOException;

public class HexDataFactoryTest {
    private static final String TEST_FILE_PATH = "test";
    private static final String TEST_SUFFIX = "bin";
    private HexDataWriter writer;
    private HexDataReader reader;

    @BeforeEach
    void setUp() {
        writer = HexDataFactory.createWriter(TEST_FILE_PATH, TEST_SUFFIX);
        reader = HexDataFactory.createReader(TEST_FILE_PATH, TEST_SUFFIX);
        writer.clear();
        reader.clear();
    }

    @Test
    void testCreateWriter() {
        HexDataWriter newWriter = HexDataFactory.createWriter("newpath", "txt");
        assertEquals("newpath.txt", newWriter.getFullPath());
        assertEquals("", newWriter.toString());
    }

    @Test
    void testWriteWithWriter() {
        writer.addHex("A56C");
        assertTrue(HexDataFactory.write(writer));
        assertEquals("", writer.toString());
    }

    @Test
    void testWriteWithFilePathAndData() throws IOException {
        assertTrue(HexDataFactory.write("newpath", "txt", "A56C"));
        HexDataReader newReader = HexDataFactory.createReader("newpath", "txt");
        newReader.readBinary();
        assertEquals("A56C", newReader.getData());
    }

    @Test
    void testCreateReader() {
        HexDataReader newReader = HexDataFactory.createReader("newpath", "txt");
        assertEquals("newpath.txt", newReader.getFullPath());
        assertEquals("", newReader.getData());
    }

    @Test
    void testReadWithReader() throws Exception {
        writer.addHex("A56C");
        writer.writeAsBinary();
        assertTrue(HexDataFactory.read(reader));
        assertEquals("A56C", reader.getData());
    }

    @Test
    void testReadWithFilePath() throws Exception {
        writer.addHex("A56C");
        writer.writeAsBinary();
        HexDataReader newReader = HexDataFactory.read(TEST_FILE_PATH, TEST_SUFFIX);
        assertEquals("A56C", newReader.getData());
        assertEquals(TEST_FILE_PATH + "." + TEST_SUFFIX, newReader.getFullPath());
    }

    @Test
    void testUseSameFileWriterWriter() {
        HexDataWriter otherWriter = HexDataFactory.createWriter(TEST_FILE_PATH, TEST_SUFFIX);
        assertTrue(HexDataFactory.useSameFile(writer, otherWriter));
    }

    @Test
    void testUseSameFileReaderReader() {
        HexDataReader otherReader = HexDataFactory.createReader(TEST_FILE_PATH, TEST_SUFFIX);
        assertTrue(HexDataFactory.useSameFile(reader, otherReader));
    }

    @Test
    void testUseSameFileWriterReader() {
        assertTrue(HexDataFactory.useSameFile(writer, reader));
    }

    @Test
    void testUseSameFileReaderWriter() {
        assertTrue(HexDataFactory.useSameFile(reader, writer));
    }

    @Test
    void testUseSameFileWriterWriterDifferentPaths() {
        HexDataWriter otherWriter = HexDataFactory.createWriter("different", TEST_SUFFIX);
        assertFalse(HexDataFactory.useSameFile(writer, otherWriter));
    }

    @Test
    void testUseSameFileReaderReaderDifferentPaths() {
        HexDataReader otherReader = HexDataFactory.createReader("different", TEST_SUFFIX);
        assertFalse(HexDataFactory.useSameFile(reader, otherReader));
    }

    @Test
    void testUseSameFileWriterReaderDifferentPaths() {
        HexDataReader otherReader = HexDataFactory.createReader("different", TEST_SUFFIX);
        assertFalse(HexDataFactory.useSameFile(writer, otherReader));
    }
}
