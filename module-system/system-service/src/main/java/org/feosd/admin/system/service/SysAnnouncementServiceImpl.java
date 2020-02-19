package org.feosd.admin.system.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.feosd.admin.system.dao.SysAnnouncementReadInfoRepository;
import org.feosd.admin.system.dao.SysAnnouncementRepository;
import org.feosd.admin.system.dao.SysUserRepository;
import org.feosd.admin.system.dao.domain.SysAnnouncement;
import org.feosd.admin.system.dao.domain.SysAnnouncementReadInfo;
import org.feosd.admin.system.dao.domain.SysUser;
import org.feosd.admin.system.service.api.SysAnnouncementService;
import org.feosd.admin.system.service.api.dto.SysAnnouncementDto;
import org.feosd.admin.system.service.api.dto.SysUserDto;
import org.feosd.base.service.BaseServiceImpl;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.validation.Valid;

/**
 * code generator author: Steven version: 1.0.0
 */
@Service
@Valid // 如果需要自动数据校验，请添加本注解。
public class SysAnnouncementServiceImpl extends BaseServiceImpl<SysAnnouncement, SysAnnouncementDto, String>
		implements SysAnnouncementService {
	private final static Logger log = LoggerFactory.getLogger(SysAnnouncementServiceImpl.class);

	@Autowired
	SysAnnouncementRepository repository;
	@Autowired
	SysAnnouncementReadInfoRepository readInforRepository;
	@Autowired
	SysUserRepository sysUserRepository;

	// 内部需要，无需修改。
	@Override
	public SysAnnouncement newDomain() {
		return new SysAnnouncement();
	}

	// 内部需要，无需修改。
	@Override
	public SysAnnouncementDto newDTO() {
		return new SysAnnouncementDto();
	}

	// 内部需要，无需修改
	@Override
	public SysAnnouncementRepository repository() {
		return repository;
	}

	// 如果DTO和domain存在有不同的fieldName，请自定义转换实现。
	@Override
	public void d2DTO(SysAnnouncement domain, SysAnnouncementDto sysAnnouncementDto) {
		super.d2DTO(domain, sysAnnouncementDto);
	}

	// 如果DTO和domain?cap_first存在有不同的fieldName，请自定义转换实现。
	@Override
	public void dto2D(SysAnnouncementDto sysAnnouncementDto, SysAnnouncement domain) {
		super.dto2D(sysAnnouncementDto, domain);
	}
	// 请在以下区域书写自定义业务逻辑处理。

	@Override
	public Page<SysAnnouncementDto> getSysAnnouncementPage(PageQuery query) {
		// 先检查查询字段和排序字段
		checkQuery(SysAnnouncement.class, query.getQueryParams(), query.getSortParams());
		Pageable pageable = PageRequest.of(query.getPageIndex(), query.getPageSize(), Sort.by(query.sortOrder()));

		return toDTOPage(repository.searchForPage(SysAnnouncement.class, query.getQueryParams(), pageable));
	}

	@Override
	@Transactional
	public SysAnnouncementDto addOrUpdateSysAnnouncement(SysAnnouncementDto sysAnnouncementDto) {
		boolean sendFlag = false;
		SysAnnouncementDto result = new SysAnnouncementDto();
		if (Objects.equals("1", sysAnnouncementDto.getSendStatus())) {
			sysAnnouncementDto.setSendTime(LocalDateTime.now());
			sendFlag = true;
		}
		if (sysAnnouncementDto.getId() == null) {
			result = this.add(sysAnnouncementDto);
		} else {
			result = this.update(sysAnnouncementDto.getId(), sysAnnouncementDto);
		}
		if (sendFlag) {
			List<Long> userIds = sysAnnouncementDto.getUserIds();
			// 通知发布后 默认添加通知查看情况到 查看表
			List<SysAnnouncementReadInfo> readInfos = new ArrayList<SysAnnouncementReadInfo>();
			SysAnnouncementReadInfo info = null;
			for (Long userId : userIds) {
				info = new SysAnnouncementReadInfo();
				info.setAnnounceId(result.getId());
				info.setUserId(userId);
				info.setRead("0"); // 默认未读
				readInfos.add(info);
			}
			readInforRepository.saveAll(readInfos);
		}
		return result;

	}

	@Override
	public SysAnnouncementDto getSysAnnouncement(String id) {
		SysAnnouncementDto dto = super.get(id);
		List<SysUser> users = sysUserRepository.findAllById(dto.getUserIds());
		List<SysUserDto> userDtos = new ArrayList<>();
		SysUserDto   userDto = null;
		for (SysUser user : users) {
			userDto = new SysUserDto();
			userDto.setId(user.getId());
			userDto.setName(user.getName());
			userDtos.add(userDto);
		}
		dto.setUserDtos(userDtos);
		return dto;
	}

}
