package org.feosd.admin.system.service;

import org.feosd.admin.common.util.ValidatorUtil;
import org.feosd.admin.system.dao.SysTestRepository;
import org.feosd.admin.system.dao.domain.SysTest;
import org.feosd.admin.system.service.api.SysTestService;
import org.feosd.admin.system.service.api.dto.SysTestDto;
import org.feosd.base.service.BaseServiceImpl;
import org.feosd.base.service.api.exception.ServiceException;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Valid // 如果需要自动数据校验，请添加本注解。
public class SysTestServiceImpl extends BaseServiceImpl<SysTest, SysTestDto, String>
		implements SysTestService {
	private final static Logger log = LoggerFactory.getLogger(SysTestServiceImpl.class);

	@Autowired
	SysTestRepository repository;

	// 内部需要，无需修改。
	@Override
	public SysTest newDomain() {
		return new SysTest();
	}

	// 内部需要，无需修改。
	@Override
	public SysTestDto newDTO() {
		return new SysTestDto();
	}

	// 内部需要，无需修改
	@Override
	public SysTestRepository repository() {
		return repository;
	}

	// 如果DTO和domain存在有不同的fieldName，请自定义转换实现。
	@Override
	public void d2DTO(SysTest domain, SysTestDto sysTestDto) {
		super.d2DTO(domain, sysTestDto);
	}

	// 如果DTO和domain?cap_first存在有不同的fieldName，请自定义转换实现。
	@Override
	public void dto2D(SysTestDto sysTestDto, SysTest domain) {
		super.dto2D(sysTestDto, domain);
	}
	// 请在以下区域书写自定义业务逻辑处理。

	@Override
	public Page<SysTestDto> getSysTestPage(PageQuery query) {
		// 先检查查询字段和排序字段
		checkQuery(SysTest.class, query.getQueryParams(), query.getSortParams());
		// 如果domain中含有byte、short、long、date类型字段需转换字段类型
		// toQueryParams(SysTest.class,query.getQueryParams());
		Pageable pageable = PageRequest.of(query.getPageIndex(), query.getPageSize(), Sort.by(query.sortOrder()));
		return toDTOPage(repository.searchForPage(SysTest.class, query.getQueryParams(), pageable));
	}

	@Override
	@Transactional
	public SysTestDto addOrUpdateSysTest(SysTestDto sysTestDto) {
		sysTestDto.setId(null);

		if(!ValidatorUtil.isMobile(sysTestDto.getMobile()))
			throw new ServiceException(400,"手机号格式错误！");

		if (sysTestDto.getId() == null) {
			return this.add(sysTestDto);
		} else {
			return this.update(sysTestDto.getId(), sysTestDto);
		}
	}
}
