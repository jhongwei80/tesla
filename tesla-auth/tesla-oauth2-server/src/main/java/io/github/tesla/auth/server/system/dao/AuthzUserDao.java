package io.github.tesla.auth.server.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.auth.server.system.domain.ShiroUsersDO;

@Mapper
public interface AuthzUserDao {

    ShiroUsersDO findByUserId(Long userId);

    ShiroUsersDO findByUserNamed(String userName);

    List<String> findRoleByUserId(Long userId);

    List<String> findPermissonByUserId(Long userId);
}
