/*
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.github.tesla.filter.utils;

import java.util.Map;

import com.google.common.collect.Maps;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.netty.handler.codec.http.HttpObject;

public class GroovyCompiler {

    private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    private static final Map<String, Class> GROOVY_CLASS_CACHE = Maps.newConcurrentMap();

    public static Class compile(String sCode) {
        Class clazz = GROOVY_CLASS_CACHE.get(sCode);
        if (clazz != null) {
            return clazz;
        } else {
            clazz = groovyClassLoader.parseClass(sCode);
            GROOVY_CLASS_CACHE.put(sCode, clazz);
            return clazz;
        }
    }

    public static Boolean shouldRunUserFilter(String groovyScript, NettyHttpServletRequest servletRequest,
        HttpObject httpObject) {
        Boolean shouldRunFilter = true;
        try {
            if (groovyScript != null) {
                Class<?> clazz = GroovyCompiler.compile(groovyScript);
                GroovyObject groovyObject = (GroovyObject)clazz.getDeclaredConstructor().newInstance();
                Object[] objects = new Object[] {servletRequest, httpObject};
                shouldRunFilter = (Boolean)groovyObject.invokeMethod("runFilter", objects);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return shouldRunFilter;
    }

}
