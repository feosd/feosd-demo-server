package org.feosd.admin.system.dao;


import org.feosd.admin.system.dao.domain.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysUserRepository extends JpaRepository<SysUser,Long>,SysUserRepositoryCustom {
    // 该区域书写JPA可自动生成的查询。例如： findByName  countByName... 需要自定义复杂处理，请在Custom中定义接口，并在Impl中实现。

    List<SysUser> findBySysDepart_OrgCodeLikeOrderBySysDepartAscSortOrderAscIdAsc(String orgCode);

    SysUser findByUsername(String username);

    List<SysUser> findBySysDepart_IdOrderBySortOrderAscIdAsc(String departId);
}
