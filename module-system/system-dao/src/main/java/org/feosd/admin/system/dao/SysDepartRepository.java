package org.feosd.admin.system.dao;


import org.feosd.admin.system.dao.domain.SysDepart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysDepartRepository extends JpaRepository<SysDepart,String>,SysDepartRepositoryCustom {
    // 该区域书写JPA可自动生成的查询。例如： findByName  countByName... 需要自定义复杂处理，请在Custom中定义接口，并在Impl中实现。

    List<SysDepart> findByParentIdIsNullOrderByOrgCodeDesc();

    List<SysDepart> findByParentIdOrderByOrgCodeDesc(String parentId);

    List<SysDepart> findByDelFlagOrderByOrgTypeAscSortOrderAsc(String delFlag);

    List<SysDepart> findByParentIdAndDelFlagOrderBySortOrderAsc(String parentId,String delFlag);

    List<SysDepart> findByOrgCodeLikeAndDelFlagOrderByOrgTypeAscSortOrderAsc(String orgCode,String delFlag);

    List<SysDepart> findByOrgCodeLike(String orgCode);

    SysDepart findByOrgCode(String orgCode);

    SysDepart findByDepartName(String departName);
}
