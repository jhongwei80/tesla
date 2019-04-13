/*
 * Copyright 2014-2017 the original author or authors.
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
package io.github.tesla.backend.dubbo.user.impl;

import org.springframework.beans.BeanUtils;

import com.alibaba.dubbo.config.annotation.Service;

import io.github.tesla.backend.dubbo.pojo.UserRequest;
import io.github.tesla.backend.dubbo.pojo.UserResponse;
import io.github.tesla.backend.dubbo.user.UserService;

/**
 * @author liushiming
 * @version UserServiceImpl.java, v 0.0.1 2018年5月17日 下午2:20:27 liushiming
 */
@Service(version = "1.0.0", group = "tesla")
public class UserServiceImpl implements UserService {

    @Override
    public UserResponse sayHello(UserRequest userrequest) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(userrequest, response);
        return response;
    }

}
