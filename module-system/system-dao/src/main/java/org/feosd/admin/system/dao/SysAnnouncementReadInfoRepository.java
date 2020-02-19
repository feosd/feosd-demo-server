package org.feosd.admin.system.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import org.feosd.admin.system.dao.domain.SysAnnouncementReadInfo;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysAnnouncementReadInfoRepository extends JpaRepository<SysAnnouncementReadInfo,String>,SysAnnouncementReadInfoRepositoryCustom {
    // 该区域书写JPA可自动生成的查询。例如： findByName  countByName... 需要自定义复杂处理，请在Custom中定义接口，并在Impl中实现。

}
