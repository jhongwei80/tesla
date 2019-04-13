package io.github.tesla.auth.sdk.jwt.interfaces;

import java.util.Date;

/**
 * The Clock class is used to wrap calls to Date class.
 */
public interface Clock {

    /**
     * Returns a new Date representing Today's time.
     *
     * @return a new Date representing Today's time.
     */
    Date getToday();
}
