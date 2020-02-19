package org.feosd.admin.system.service.api;

import org.feosd.admin.system.service.api.dto.SysAnnouncementDto;
import org.feosd.base.service.api.BaseService;
import org.feosd.base.service.api.query.PageQuery;
import org.springframework.data.domain.Page;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysAnnouncementService extends BaseService<SysAnnouncementDto, String> {
    //已具备基本的增删改查功能，自定义业务请在以下区域书写。

    Page<SysAnnouncementDto> getSysAnnouncementPage(PageQuery query);

    SysAnnouncementDto addOrUpdateSysAnnouncement(SysAnnouncementDto sysAnnouncementDto);

	SysAnnouncementDto getSysAnnouncement(String id);
}
