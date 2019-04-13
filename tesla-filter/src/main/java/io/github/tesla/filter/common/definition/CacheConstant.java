package io.github.tesla.filter.common.definition;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface CacheConstant {

    String CACHE_REFRESH_TIME = "CACHE_REFRESH_TIME";

    String WAF_REQUEST_CACHE_LIST = "WAF_REQUEST_CACHE_LIST";

    String WAF_RESPONSE_CACHE_LIST = "WAF_RESPONSE_CACHE_LIST";

    String API_CACHE_LIST = "API_CACHE_LIST";

    String APP_KEY_CACHE_MAP = "APP_KEY_CACHE_MAP";

    String FILE_CACHE_MAP = "FILE_CACHE_MAP";

    String CACHE_RESULT_MAP = "CACHE_RESULT_MAP";

    String CACHE_REFRESH_LOCK = "CACHE_REFRESH_LOCK";

    ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

}
