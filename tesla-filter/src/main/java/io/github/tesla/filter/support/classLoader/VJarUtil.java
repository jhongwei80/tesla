package io.github.tesla.filter.support.classLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VJarUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(VJarUtil.class);

    /**
     * Returns the name of all classes in the specified stream.
     *
     * @param jarInStream
     *            the stream to read
     * @return a list containing the names of all classes
     * @throws IOException
     */
    public static List<String> getClassNamesFromStream(JarInputStream jarInStream) throws IOException {
        ArrayList<String> result = new ArrayList<String>();

        // the current jar entry
        JarEntry entry = jarInStream.getNextJarEntry();

        // iterate through all entries
        while (entry != null) {
            String name = entry.getName();

            // indicates whether the current entry is in folder or if it is
            // in the root folder of the jar
            boolean isInDirectory = name.lastIndexOf("/") > 0;

            // indicates that the entry is a class file
            boolean isClassFile = name.endsWith(".class");

            if (isInDirectory && isClassFile) {
                String className = pathToClassName(name);
                result.add(className);
            }

            entry = jarInStream.getNextJarEntry();
        }

        jarInStream.close();

        return result;
    }

    /**
     * Loads all classes of the specified jar archive. If errors occure while loading a class it will be silently
     * ignored.
     *
     * @param f
     *            jar file
     * @return all classes of the specified jar archive
     */
    public static Collection<Class<?>> loadClasses(File f, ClassLoader loader) {

        List<String> classNames = null;

        try {
            classNames = VJarUtil.getClassNamesFromStream(new JarInputStream(new FileInputStream(f)));
            if (loader == null) {
                loader = new URLClassLoader(new URL[] {f.toURI().toURL()});
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        for (String n : classNames) {
            try {
                classes.add(loader.loadClass(n));

            } catch (NoClassDefFoundError ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (IncompatibleClassChangeError ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (Throwable tr) {
                LOGGER.error(tr.getMessage(), tr);
            }
        }

        return classes;
    }

    /**
     * Converts a path to class name, i.e., replaces "/" by "." and removes the ".class" extension.
     *
     * @param path
     *            the path to convert
     * @return the class name
     */
    public static String pathToClassName(String path) {
        return path.substring(0, path.length() - 6).replace("/", ".");
    }

    /**
     * Reads the contents of the current jar entry of the specified jar input stream and stores it in a byte array.
     *
     * @param jarInStream
     *            the stream to read from
     * @return the contents of the current jar entry
     * @throws IOException
     */
    public static byte[] readCurrentJarEntry(JarInputStream jarInStream) throws IOException {
        // read the whole contents of the
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        while ((len = jarInStream.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

    // no instanciation allowed
    private VJarUtil() {
        throw new AssertionError(); // not in this class either!
    }

}
