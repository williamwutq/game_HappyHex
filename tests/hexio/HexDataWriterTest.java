package hexio;

import hexio.hexdata.HexDataReader;
import hexio.hexdata.HexDataWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class HexDataWriterTest {
    private HexDataWriter writer;
    private static final String TEST_FILE_PATH = "test";
    private static final String TEST_SUFFIX = "bin";

    @BeforeEach
    void setUp() {
        writer = new HexDataWriter(TEST_FILE_PATH, TEST_SUFFIX);
        writer.clear();
    }

    @Test
    void testAddLong() {
        writer.add(255L);
        assertEquals("00000000000000FF", writer.toString());
    }

    @Test
    void testAddInt() {
        writer.add(255);
        assertEquals("000000FF", writer.toString());
    }

    @Test
    void testAddShort() {
        writer.add((short)-45);
        assertEquals("FFD3", writer.toString());
    }

    @Test
    void testAddChar() {
        writer.add('A');
        assertEquals("0041", writer.toString());
    }

    @Test
    void testAddByte() {
        writer.add((byte) 255);
        assertEquals("FF", writer.toString());
    }

    @Test
    void testAddString() {
        writer.add("Hi");
        assertEquals("00480069", writer.toString());
    }

    @Test
    void testAddLongArray() {
        writer.add(new long[]{255L, 256L});
        assertEquals("00000000000000FF0000000000000100", writer.toString());
    }

    @Test
    void testAddIntArray() {
        writer.add(new int[]{255, 256});
        assertEquals("000000FF00000100", writer.toString());
    }

    @Test
    void testAddShortArray() {
        writer.add(new short[]{255, -256});
        assertEquals("00FFFF00", writer.toString());
    }

    @Test
    void testAddCharArray() {
        writer.add(new char[]{'A', 'B'});
        assertEquals("00410042", writer.toString());
    }

    @Test
    void testAddByteArray() {
        writer.add(new byte[]{(byte) 255, (byte) 128});
        assertEquals("FF80", writer.toString());
    }

    @Test
    void testAddStringArray() {
        writer.add(new String[]{"A", "B"});
        assertEquals("00410042", writer.toString());
    }

    @Test
    void testAddDouble() {
        writer.add(1.0);
        assertEquals("3FF0000000000000", writer.toString());
    }

    @Test
    void testAddFloat() {
        writer.add(1.0f);
        assertEquals("3F800000", writer.toString());
    }

    @Test
    void testAddDoubleArray() {
        writer.add(new double[]{1.0, 2.0});
        assertEquals("3FF00000000000004000000000000000", writer.toString());
    }

    @Test
    void testAddFloatArray() {
        writer.add(new float[]{1.0f, 2.0f});
        assertEquals("3F80000040000000", writer.toString());
    }

    @Test
    void testAddBooleanTrue() {
        writer.add(true);
        assertEquals("F", writer.toString());
    }

    @Test
    void testAddBooleanFalse() {
        writer.add(false);
        assertEquals("0", writer.toString());
    }

    @Test
    void testAddBooleanArray() {
        writer.add(new boolean[]{true, false, true, false});
        assertEquals("5", writer.toString());
    }

    @Test
    void testAddBooleanArrayWithPadding() {
        writer.add(new boolean[]{true, false, true});
        assertEquals("5", writer.toString());
    }

    @Test
    void testAddHexString() {
        writer.addHex("A56C");
        assertEquals("A56C", writer.toString());
    }

    @Test
    void testAddHexStringArray() {
        writer.addHex(new String[]{"A5", "6C"});
        assertEquals("A56C", writer.toString());
    }

    @Test
    void testAddHexCharArray() {
        writer.addHex(new char[]{'A', '5', '6', 'C'});
        assertEquals("A56C", writer.toString());
    }

    @Test
    void testAddSpace() {
        writer.addSpace(3);
        assertEquals("000", writer.toString());
    }

    @Test
    void testAddDivider() {
        writer.addDivider(2);
        assertEquals("FFFF", writer.toString());
    }

    @Test
    void testRemove() {
        writer.addHex("A56C");
        writer.remove(2);
        assertEquals("A5", writer.toString());
    }

    @Test
    void testRemoveAll() {
        writer.addHex("A56C");
        writer.remove(4);
        assertEquals("", writer.toString());
    }

    @Test
    void testWriteAsBinary() throws IOException {
        writer.addHex("A56C");
        writer.writeAsBinary();
        byte[] expected = {(byte) 0xA5, (byte) 0x6C};
        byte[] actual = Files.readAllBytes(Path.of(writer.getFullPath()));
        assertArrayEquals(expected, actual);
    }

    @Test
    void testWriteAsText() throws IOException {
        writer.addHex("A56C");
        writer.writeAsText();
        String actual = Files.readString(Path.of(writer.getFullPath() + ".txt"));
        assertEquals("A56C", actual);
    }

    @Test
    void testClear() {
        writer.addHex("A56C");
        writer.clear();
        assertEquals("", writer.toString());
    }

    @Test
    void testSetFile() {
        writer.setFile("newpath", "txt");
        assertEquals("newpath.txt", writer.getFullPath());
    }

    @Test
    void testChangeFile() {
        writer.changeFile("newpath");
        assertEquals("newpath.bin", writer.getFullPath());
    }

    @Test
    void testGetHexDataReader() {
        writer.addHex("A56C");
        HexDataReader reader = writer.getHexDataReader();
        assertEquals("A56C", reader.getData());
        assertEquals(writer.getFullPath(), reader.getFullPath());
    }

    @Test
    void testContainSameData() {
        HexDataWriter other = new HexDataWriter();
        writer.addHex("A56C");
        other.addHex("A56C");
        assertTrue(writer.containSameData(other));
    }

    @Test
    void testWriteToSameFile() {
        HexDataWriter other = new HexDataWriter(TEST_FILE_PATH, TEST_SUFFIX);
        assertTrue(writer.writeToSameFile(other));
    }

    @Test
    void testEquals() {
        HexDataWriter other = new HexDataWriter(TEST_FILE_PATH, TEST_SUFFIX);
        writer.addHex("A56C");
        other.addHex("A56C");
        assertTrue(writer.equals(other));
    }

    @Test
    void testToString() {
        writer.addHex("A56C");
        assertEquals("A56C", writer.toString());
    }
}
