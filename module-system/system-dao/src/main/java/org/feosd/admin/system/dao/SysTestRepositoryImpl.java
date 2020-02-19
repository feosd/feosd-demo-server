package org.feosd.admin.system.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
 @Slf4j
public class SysTestRepositoryImpl implements SysTestRepositoryCustom {

    @Autowired
    EntityManager em;

    @Override
    public EntityManager getEm() {
        return em;
    }

    // AbstractRepository 已封装基本hql的分页查询、下拉更新查询 和 基于 QueryParam 纯代码的复杂查询，可根据习惯使用。具体请查阅AbstractRepository,或者咨询架构组同事。

}
