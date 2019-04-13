package io.github.tesla.filter.support.classLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteArrayClassLoader extends ClassLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByteArrayClassLoader.class);

    private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private ArrayDeque<UnresolvedClass> unresolvedClasses = new ArrayDeque<UnresolvedClass>();
    private HashMap<String, URL> resources = new HashMap<String, URL>();

    /**
     * Constructor.
     */
    public ByteArrayClassLoader() {
        super(ByteArrayClassLoader.class.getClassLoader());
    }

    /**
     * Adds a class from byte array.
     * 
     * @param name
     *            the class name
     * @param data
     *            the class file as byte array
     * @return the class that has been added
     */
    public Class<?> addClass(String name, byte[] data) {
        Class<?> result = getClasses().get(name);
        if (result == null) {
            try {
                result = getParent().loadClass(name);
            } catch (ClassNotFoundException ex) {
                try {
                    Class<?> c = defineClass(name, data, 0, data.length);
                    classes.put(name, c);
                    result = c;
                } catch (NoClassDefFoundError e) {
                    unresolvedClasses.addFirst(new UnresolvedClass(name, data));

                }
            } catch (SecurityException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return result;
    }

    public void addResource(String name, byte[] data) {
        LOGGER.info(">> Adding resource \"" + name + "\"");
        try {

            URL url =
                new URL("inputstream", "", 0, name, new InputStreamURLStreamHandler(new ByteArrayInputStream(data)));

            resources.put(name, url);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String className) {
        Class<?> result = null;
        result = classes.get(className);
        if (result == null) {
            try {
                return findSystemClass(className);
            } catch (Exception e) {
            }

        }
        if (result == null) {
            try {
                return this.getParent().loadClass(className);
            } catch (Exception e) {
            }

        }
        return result;
    }

    /**
     * Returns the classes.
     * 
     * @return the classes
     */
    public HashMap<String, Class<?>> getClasses() {
        return classes;
    }

    @Override
    public URL getResource(String name) {
        System.out.println("Resource returned: " + name);
        return resources.get(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream result = null;
        URL url = getResource(name);
        if (url != null) {
            try {
                result = url.openStream();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    /**
     * Resolves class definition conflicts. This can occur if another class has to be defined before because the current
     * class depends on it. To be sure that all resolvable conflicts have been solved this method has to be called as
     * long as it returns <code>true</code> which means there are still resolvable conflicts left.
     * 
     * @return all classes that could be loaded after resolve
     */
    public ArrayList<Class<?>> resolveClasses() {

        ArrayList<Class<?>> result = new ArrayList<Class<?>>();

        int maxIter = unresolvedClasses.size();

        for (int i = 0; i < maxIter; i++) {
            UnresolvedClass uC = unresolvedClasses.pollLast();
            Class c = addClass(uC.getName(), uC.getData());
            if (c != null) {
                result.add(c);
            }
        }

        return result;
    }
}
