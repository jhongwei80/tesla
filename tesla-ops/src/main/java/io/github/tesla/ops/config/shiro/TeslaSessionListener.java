package io.github.tesla.ops.config.shiro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

public class TeslaSessionListener implements SessionListener {

    private final AtomicInteger sessionCount = new AtomicInteger(0);

    public int getSessionCount() {
        return sessionCount.get();
    }

    @Override
    public void onExpiration(Session session) {
        sessionCount.decrementAndGet();
    }

    @Override
    public void onStart(Session session) {
        sessionCount.incrementAndGet();
    }

    @Override
    public void onStop(Session session) {
        sessionCount.decrementAndGet();
    }

}
