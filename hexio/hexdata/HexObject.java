package hexio.hexdata;

public interface HexObject{
    static int length() {return 0;}
    String data();
    void decode(String data);
}
