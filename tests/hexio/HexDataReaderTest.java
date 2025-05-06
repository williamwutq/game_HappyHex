package hexio;

import hexio.hexdata.HexDataReader;
import hexio.hexdata.HexDataWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class HexDataReaderTest {
    private HexDataReader reader;
    private HexDataWriter writer;
    private static final String TEST_FILE_PATH = "test";
    private static final String TEST_SUFFIX = "bin";

    @BeforeEach
    void setUp() throws IOException {
        writer = new HexDataWriter(TEST_FILE_PATH, TEST_SUFFIX);
        writer.clear();
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX);
        reader.clear();
    }

    @Test
    void testReadBinary() throws IOException {
        writer.addHex("A56C");
        writer.writeAsBinary();
        reader.readBinary();
        assertEquals("A56C", reader.getData());
    }

    @Test
    void testReadText() throws IOException {
        writer.addHex("A56C");
        writer.writeAsText();
        reader.readText();
        assertEquals("A56C", reader.getData());
    }

    @Test
    void testGetLong() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "00000000000000FF");
        assertEquals(255L, reader.getLong(0));
    }

    @Test
    void testGetInt() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "000000FF");
        assertEquals(255, reader.getInt(0));
    }

    @Test
    void testGetShort() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "FFFB");
        assertEquals(-5, reader.getShort(0));
    }

    @Test
    void testGetChar() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "0041");
        assertEquals('A', reader.getChar(0));
    }

    @Test
    void testGetByte() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "FF");
        assertEquals((byte) 255, reader.getByte(0));
    }

    @Test
    void testGetString() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "00480069");
        assertEquals("Hi", reader.getString(0, 2));
    }

    @Test
    void testGetDouble() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "3FF0000000000000");
        assertEquals(1.0, reader.getDouble(0));
    }

    @Test
    void testGetFloat() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "3F800000");
        assertEquals(1.0f, reader.getFloat(0));
    }

    @Test
    void testGetBooleanTrue() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "F");
        assertTrue(reader.getBoolean(0));
    }

    @Test
    void testGetBooleanFalse() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "0");
        assertFalse(reader.getBoolean(0));
    }

    @Test
    void testGetBooleanAtBit() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A");
        assertTrue(reader.getBooleanAtBit(0, 1));
        assertFalse(reader.getBooleanAtBit(0, 2));
    }

    @Test
    void testGetBooleanAtBitInvalidPosition() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A");
        assertThrows(IllegalArgumentException.class, () -> reader.getBooleanAtBit(0, 4));
    }

    @Test
    void testNext() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        assertEquals("A", reader.next());
        assertEquals(1, reader.pointer());
    }

    @Test
    void testNextLong() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "00000000000000FF");
        assertEquals(255L, reader.nextLong());
        assertEquals(16, reader.pointer());
    }

    @Test
    void testNextInt() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "000000FF");
        assertEquals(255, reader.nextInt());
        assertEquals(8, reader.pointer());
    }

    @Test
    void testNextShort() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "FFFB");
        assertEquals(-5, reader.nextShort());
        assertEquals(4, reader.pointer());
    }

    @Test
    void testNextChar() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "0041");
        assertEquals('A', reader.nextChar());
        assertEquals(4, reader.pointer());
    }

    @Test
    void testNextByte() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "FF");
        assertEquals((byte) 255, reader.nextByte());
        assertEquals(2, reader.pointer());
    }

    @Test
    void testNextString() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "00480069");
        assertEquals("Hi", reader.nextString(2));
        assertEquals(8, reader.pointer());
    }

    @Test
    void testNextDouble() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "3FF0000000000000");
        assertEquals(1.0, reader.nextDouble());
        assertEquals(16, reader.pointer());
    }

    @Test
    void testNextFloat() throws IOException {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "3F800000");
        assertEquals(1.0f, reader.nextFloat());
        assertEquals(8, reader.pointer());
    }

    @Test
    void testNextBoolean() throws IOException  {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "F");
        assertTrue(reader.nextBoolean());
        assertEquals(1, reader.pointer());
    }

    @Test
    void testAdvance() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        reader.advance();
        assertEquals(1, reader.pointer());
    }

    @Test
    void testAdvanceByLength() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        reader.advance(2);
        assertEquals(2, reader.pointer());
    }

    @Test
    void testClear() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        reader.clear();
        assertEquals("", reader.getData());
        assertEquals(0, reader.pointer());
    }

    @Test
    void testGetHexDataWriter() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        HexDataWriter newWriter = reader.getHexDataWriter();
        assertEquals("A56C", newWriter.toString());
        assertEquals(reader.getFullPath(), newWriter.getFullPath());
    }

    @Test
    void testGetData() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        assertEquals("A56C", reader.getData());
    }

    @Test
    void testGetSubstring() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        assertEquals("56", reader.get(1, 2));
    }

    @Test
    void testGetSubstringOutOfBounds() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A5");
        assertEquals("A500", reader.get(0, 4));
    }

    @Test
    void testGetSubstringNegativeIndex() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A5");
        assertEquals("00A5", reader.get(-2, 4));
    }

    @Test
    void testReadSameFile() {
        HexDataReader other = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX);
        assertTrue(reader.readSameFile(other));
    }

    @Test
    void testContainSameData() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        HexDataReader other = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        assertTrue(reader.containSameData(other));
    }

    @Test
    void testEquals() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        HexDataReader other = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        assertTrue(reader.equals(other));
    }

    @Test
    void testToString() {
        reader = new HexDataReader(TEST_FILE_PATH, TEST_SUFFIX, "A56C");
        assertEquals("A56C", reader.toString());
    }
}
