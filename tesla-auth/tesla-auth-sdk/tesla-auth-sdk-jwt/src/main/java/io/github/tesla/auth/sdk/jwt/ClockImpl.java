package io.github.tesla.auth.sdk.jwt;

import java.util.Date;

import io.github.tesla.auth.sdk.jwt.interfaces.Clock;

final class ClockImpl implements Clock {

    ClockImpl() {}

    @Override
    public Date getToday() {
        return new Date();
    }
}
