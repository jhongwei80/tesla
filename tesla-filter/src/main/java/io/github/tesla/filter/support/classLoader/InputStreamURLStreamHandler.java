package io.github.tesla.filter.support.classLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class InputStreamURLStreamHandler extends URLStreamHandler {

    private class InputStreamURLConnection extends URLConnection {
        private InputStream inStream;

        public InputStreamURLConnection(URL url, InputStream inStream) {
            super(url);
            this.inStream = inStream;
        }

        @Override
        public void connect() throws IOException {
            //
        }

        @Override
        public InputStream getInputStream() {
            return inStream;
        }
    }

    InputStream inputStream;

    public InputStreamURLStreamHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return new InputStreamURLConnection(u, inputStream);
    }
}
