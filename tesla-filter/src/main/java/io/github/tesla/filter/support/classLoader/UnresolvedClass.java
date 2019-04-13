package io.github.tesla.filter.support.classLoader;

public class UnresolvedClass {
    private String name;
    private byte[] data;

    /**
     * Constructor.
     * 
     * @param name
     *            the class name
     * @param data
     *            the contents of the class file
     */
    public UnresolvedClass(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    /**
     * Returns the data of this unresolved class.
     * 
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Returns the name of this unresolved class.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Defines the data of this unresolved class.
     * 
     * @param data
     *            the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Defines the name of this unresolved class.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
