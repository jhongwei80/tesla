package io.github.tesla.ops.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.ShiroUsersDO;

@Mapper
public interface AuthzUserDao {

    ShiroUsersDO findByUserId(Long userId);

    ShiroUsersDO findByUserNamed(String userName);

    List<String> findPermissonByUserId(Long userId);

    List<String> findRoleByUserId(Long userId);
}
