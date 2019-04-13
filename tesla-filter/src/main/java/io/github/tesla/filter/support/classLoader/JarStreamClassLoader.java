package io.github.tesla.filter.support.classLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarStreamClassLoader extends ByteArrayClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JarStreamClassLoader.class);

    /**
     * Constructor.
     */
    public JarStreamClassLoader() {}

    /**
     * Constructor.
     * 
     * @param jarInStream
     *            the stream to read from
     * @throws IOException
     */
    public JarStreamClassLoader(JarInputStream jarInStream) throws IOException {
        addClassesFromStream(jarInStream);
    }

    /**
     * <p>
     * Adds all classes and resources from a given jar input stream. Jar entries that are jar files will be recusrively
     * read and all contained classes snd resources will be loaded.
     * </p>
     * <p>
     * <b>Warning:</b> This method is not well tested and not all packages can be loaded!
     * </p>
     * 
     * @param jarInStream
     *            the stream to read from
     * @return a list containing all classes of the stream that could be loaded
     * @throws IOException
     */
    public ArrayList<Class<?>> addClassesFromStream(JarInputStream jarInStream) throws IOException {
        ArrayList<Class<?>> streamClasses = new ArrayList<Class<?>>();

        // the current jar entry
        JarEntry entry = jarInStream.getNextJarEntry();

        // iterate through all entries
        while (entry != null) {
            String name = entry.getName();

            // indicates whether the current entry is in folder or if it is
            // in the root folder of the jar
            // boolean isInDirectory = name.lastIndexOf("/") > 0;

            // indicates whether the entry is a file
            boolean isFile = !entry.isDirectory();

            // indicates whether the entry is a class file
            boolean isClassFile = isFile && name.endsWith(".class");

            // indicates whether the entry is a jar file
            boolean isJarFile = isFile && name.endsWith(".jar");

            if (isClassFile) {
                String className = VJarUtil.pathToClassName(name);
                byte[] classData = VJarUtil.readCurrentJarEntry(jarInStream);
                Class<?> c = addClass(className, classData);
                if (c != null) {
                    streamClasses.add(c);
                }
            } else if (isJarFile) {
                LOGGER.info(">> entering \"" + name + "\":");
                // loads the jar file in a byte array, converts it to a
                // jar input stream and recursively calls this loadClasses
                // method
                byte[] jarData = VJarUtil.readCurrentJarEntry(jarInStream);
                ByteArrayInputStream byteStream = new ByteArrayInputStream(jarData);
                JarInputStream jarStream = new JarInputStream(byteStream);
                addClassesFromStream(jarStream);
            } else if (isFile) {
                byte[] fileData = VJarUtil.readCurrentJarEntry(jarInStream);
                addResource(name, fileData);
            }

            entry = jarInStream.getNextJarEntry();
        }

        int numberOfResolvedClasses = Integer.MAX_VALUE;

        // resolves class dependencies
        while (numberOfResolvedClasses > 0) {
            ArrayList<Class<?>> resolvedClasses = resolveClasses();
            numberOfResolvedClasses = resolvedClasses.size();
            streamClasses.addAll(resolvedClasses);
        }

        LOGGER.info(">> Class Resolution finished!");

        jarInStream.close();

        return streamClasses;
    }

}
