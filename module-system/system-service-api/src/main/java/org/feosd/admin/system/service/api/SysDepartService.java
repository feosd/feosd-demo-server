package org.feosd.admin.system.service.api;

import org.feosd.admin.system.service.api.dto.SysDepartDto;
import org.feosd.base.service.api.BaseService;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.query.Query;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysDepartService extends BaseService<SysDepartDto, String> {
    //已具备基本的增删改查功能，自定义业务请在以下区域书写。

    Page<SysDepartDto> getSysDepartPage(PageQuery query);

    List<SysDepartDto> getSysDepartList(Query query);

    void importExcel(InputStream inputStream);

    SysDepartDto addOrUpdateSysDepart(SysDepartDto sysDepartDto);

    //获取所有部门
    List<SysDepartDto> findAll();

    //根据部门编码获取部门列表
    List<SysDepartDto> findByOrgCodeLike(String orgCode);

    //根据用户获取部门列表
    List<SysDepartDto> findByByUsId(Long usId);

    //获取全部的部门树
    List<SysDepartDto> getSysDepartTree();

    //根据父部门获取部门树
    List<SysDepartDto> getSysDepartTree(String parentId);

    //根据用户获取部门树
    List<SysDepartDto> getSysDepartTreeByUsId(Long usId);

}
