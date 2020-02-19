package org.feosd.admin.system.service.api;

import org.springframework.data.domain.Page;

import org.feosd.admin.system.service.api.dto.SysAnnounceUnReadDto;
import org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoDto;
import org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoQueryDto;
import org.feosd.base.service.api.BaseService;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysAnnouncementReadInfoService extends BaseService<SysAnnouncementReadInfoDto, String> {

    //已具备基本的增删改查功能，自定义业务请在以下区域书写。

    Page<SysAnnouncementReadInfoDto> getSysAnnouncementReadInfoPage(SysAnnouncementReadInfoQueryDto query);

    SysAnnouncementReadInfoDto addOrUpdateSysAnnouncementReadInfo(SysAnnouncementReadInfoDto SysAnnounceReadInfoDto);

    SysAnnounceUnReadDto countAnnounceReadInfo(Long userId);

	boolean readAll(Long usId);
}
