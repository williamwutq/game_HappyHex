package hexio.bin;

public class HexDataWriter {
    private String data;
    private String filePath;
    private String suffix;

    /**
     * Create an empty {@code HexDataWriter} that can be used to record and write data.
     */
    public HexDataWriter(){
        data = "";
        filePath = "data";
        suffix = "";
    }
    /**
     * Create a {@code HexDataWriter} with the specific file path and file suffix.
     * that can be used to record and write data to the specific file.
     * @param filePath the relative path to the file to write to.
     * @param suffix the suffix of the file to write to.
     */
    public HexDataWriter(String filePath, String suffix){
        data = "";
        setFile(filePath, suffix);
    }

    /**
     * Change the file path and file suffix of the {@code HexDataWriter}.
     * @param filePath the new relative path to the file to write to.
     * @param suffix the new suffix of the file to write to.
     */
    public void setFile(String filePath, String suffix){
        this.filePath = filePath;
        this.suffix = suffix;
    }
    /**
     * Change the file path of the {@code HexDataWriter}.
     * @param filePath the new relative path to the file to write to.
     */
    public void changeFile(String filePath){
        this.filePath = filePath;
    }
    /**
     * Change the file path of the {@code HexDataWriter}.
     * @return the full file path of the file written to, include the suffix.
     */
    public String getFullPath(){
        if (suffix.isEmpty()) {
            return filePath;
        } else return filePath + "." + suffix;
    }

    /**
     * Return whether this {@code HexDataWriter} contains the same data as another writer.
     * @param other the other writer to compare to.
     * @return true if this writer contains the exact same data as the other writer; false otherwise.
     */
    public boolean containSameData(HexDataWriter other){
        return this.data.equals(other.data);
    }
    /**
     * Return whether this {@code HexDataWriter} writes to the same file as another writer.
     * @param other the other writer to compare to.
     * @return true if this writer writes to the exact same file as the other writer; false otherwise.
     * @see #getFullPath()
     */
    public boolean writeToSameFile(HexDataWriter other){
        return this.getFullPath().equals(other.getFullPath());
    }
    /**
     * Return whether this {@code HexDataWriter} is the same as another object.
     * @param other the other object to compare to.
     * @return true if this writer and the other writer are both {@code HexDataWriter}
     *         that {@link #containSameData contain the same data} and
     *         {@link #writeToSameFile write to the same file}; false otherwise.
     */
    public boolean equals(Object other){
        if (other instanceof HexDataWriter writer){
            return containSameData(writer) && writeToSameFile(writer);
        } else return false;
    }

    /**
     * Return the String representation of the data in hexadecimal format.
     * This string contains characters 0-9, A-F.
     * @return A String in hexadecimal format representing the data.
     */
    public String toString(){
        return data;
    }
}
