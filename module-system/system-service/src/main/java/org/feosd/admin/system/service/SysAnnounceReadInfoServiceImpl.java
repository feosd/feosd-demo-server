package org.feosd.admin.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.feosd.common.dict.service.api.DictItemService;
import org.feosd.common.dict.service.api.dto.DictItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import org.feosd.admin.system.dao.SysAnnouncementReadInfoRepository;
import org.feosd.admin.system.dao.domain.SysAnnouncementReadInfo;
import org.feosd.admin.system.service.api.SysAnnouncementReadInfoService;
import org.feosd.admin.system.service.api.dto.SysAnnounceUnReadDto;
import org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoDto;
import org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoQueryDto;
import org.feosd.base.service.BaseServiceImpl;
import org.feosd.base.service.api.validation.Valid;
import org.feosd.base.utils.StringUtils;

@Service
@Valid
public class SysAnnounceReadInfoServiceImpl
		extends BaseServiceImpl<SysAnnouncementReadInfo, SysAnnouncementReadInfoDto, String>
		implements SysAnnouncementReadInfoService {

	@Autowired
	private SysAnnouncementReadInfoRepository repository;
	@Autowired
	private DictItemService dictItemService;




	@Override
	public void d2DTO(SysAnnouncementReadInfo domain, SysAnnouncementReadInfoDto dto) {
		super.d2DTO(domain, dto);
		dto.setReadStr(Objects.equal(dto.getRead(), "1") ? "已读" : "未读");
	}

	@Override
	public SysAnnouncementReadInfo newDomain() {

		return new SysAnnouncementReadInfo();
	}


	@Override
	public SysAnnouncementReadInfoDto newDTO() {
		// TODO Auto-generated method stub
		return new SysAnnouncementReadInfoDto();
	}
	@Override
	public PagingAndSortingRepository<SysAnnouncementReadInfo, String> repository() {
		// TODO Auto-generated method stub
		return repository;
	}




	@Override
	public Page<SysAnnouncementReadInfoDto> getSysAnnouncementReadInfoPage(SysAnnouncementReadInfoQueryDto query) {
		Map<String, Object> param = new HashMap<>();
		Pageable pageable = PageRequest.of(query.getPageIndex(), query.getPageSize());
		StringBuffer hql = new StringBuffer();
		hql.append("select new org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoDto(");
		hql.append(" a.msgLevel,a.sender,a.senderName,r.id,a.title,a.msgCategory,a.msgContent,r.read,a.sendTime)");
		hql.append(" from SysAnnouncement a inner join SysAnnouncementReadInfo r on a.id = r.announceId");
		hql.append(" where a.sendStatus = 1 and r.userId = :userId");
		param.put("userId", query.getUserId());
		if (!StringUtils.isEmpty(query.getTitle())) {
			hql.append(" and a.title like :title");
			param.put("title", "%" + query.getTitle() + "%");
		}
		if (!StringUtils.isEmpty(query.getSenderName())) {
			hql.append(" and a.senderName like :senderName");
			param.put("senderName", "%" + query.getSenderName() + "%");
		}
		hql.append(" order by r.read,a.sendTime desc");
		Page<SysAnnouncementReadInfoDto> infoDtos = repository.query(hql.toString(), pageable, param);
		return infoDtos;
	}

	@Override
	@Transactional
	public SysAnnouncementReadInfoDto addOrUpdateSysAnnouncementReadInfo(
			SysAnnouncementReadInfoDto sysAnnounceReadInfoDto) {
		if (sysAnnounceReadInfoDto.getId() == null) {
			return super.add(sysAnnounceReadInfoDto);
		}
		return super.update(sysAnnounceReadInfoDto.getId(), sysAnnounceReadInfoDto);
	}

	@Override
	public SysAnnounceUnReadDto countAnnounceReadInfo(Long userId) {
		SysAnnounceUnReadDto readDto = new SysAnnounceUnReadDto();
		Map<String, Object> param = new HashMap<String, Object>();
		StringBuffer hql = new StringBuffer("select new org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoDto(");
		hql.append(" s.msgLevel,s.sender,s.senderName,r.id,s.title,s.msgCategory,s.msgContent,r.read,s.sendTime)");
		hql.append(" from SysAnnouncementReadInfo r inner join SysAnnouncement s");
		hql.append(" on r.announceId = s.id where r.userId = :userId and r.read = :read and s.sendStatus = 1");
		hql.append(" order by s.sendTime desc");
		param.put("userId", userId);
		param.put("read", "0");// 查询未读消息数
		List<SysAnnouncementReadInfoDto> annoces = repository.query(hql.toString(), param);
		if (annoces != null && annoces.size() > 0 ) {
			// 获取字典数据
			List<DictItemDto> items = dictItemService.findByDictCode("msgLevel");
			Map<String,String> itemMap = items.stream().collect(Collectors.toMap(DictItemDto::getItemValue,DictItemDto::getItemText));
			for(SysAnnouncementReadInfoDto info:annoces){
				info.setMsgLevelStr(itemMap.get(info.getMsgLevel()+""));
			}
		}
		Map<String, List<SysAnnouncementReadInfoDto>> pResult = annoces.stream()
				.collect(Collectors.groupingBy(SysAnnouncementReadInfoDto::getMsgCategory));
		List<SysAnnouncementReadInfoDto> mentDto = new ArrayList<SysAnnouncementReadInfoDto>();
		List<SysAnnouncementReadInfoDto> system = pResult.get("2");
		List<SysAnnouncementReadInfoDto> announce = pResult.get("1");
		readDto.setAnnounceTotal(announce == null ? 0 : announce.size());
		readDto.setSystemTotal(system == null ? 0 :system.size());
		readDto.setAnnounce(announce == null ? mentDto : announce);
		readDto.setSystem(system == null ? mentDto : system);

		return readDto;
	}

	@Override
	@Transactional
	public boolean readAll(Long usId) {
		Map<String, Object> param = new HashMap<>();
		StringBuffer hql = new StringBuffer();
		hql.append("update SysAnnouncementReadInfo set read = 1 where read = 0 and userId = :userId");
		param.put("userId",usId);
		int result = repository.update(hql.toString(), param);
		if (result > 0) {
			return true;
		}
		return false;
	}





}
