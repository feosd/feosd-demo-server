package org.feosd.admin.system.web.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.feosd.admin.common.SwaggerTagConstants;
import org.feosd.admin.system.service.api.SysAnnouncementReadInfoService;
import org.feosd.admin.system.service.api.dto.SysAnnounceUnReadDto;
import org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoDto;
import org.feosd.admin.system.service.api.dto.SysAnnouncementReadInfoQueryDto;
import org.feosd.auth.client.Authorization;
import org.feosd.base.utils.StringUtils;
import org.feosd.base.web.api.jersey.IResponse;
import org.feosd.base.web.api.jersey.security.ISecurityContext;
import org.feosd.base.web.api.response.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * code generator author: Steven version: 1.0.0
 */
@Path("sysAnnounceReadInfo")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Api(tags = "") // swagger 生成的tag标签，用于前端代码分类生成。建议在common中统一定义常量。
@RolesAllowed("*") // 大用户分类，比如一个货车匹配项目，具备四类用户：匿名（所有人都可访问）、 司机、货主、平台管理员。
					// 每一类用户的接口相关隔离，非该用户身份不能访问。该注解可用于方法上。建议在common中统一定义常量。
//@Authorization(permission = {"sysAnnouncementReadInfo:list", "sysAnnouncementReadInfo:update", "sysAnnouncementReadInfo:delete", "sysAnnouncementReadInfo:add"}, logic = Logic.OR)// 权限管理。 需要具备的权限，logic.And 标识必须具备所有权限才可访问，logic.Or标识具备其中一个即可访问。 该注解可用于方法上。
@Slf4j
public class SysAnnouncementReadInfoApi implements ISecurityContext, IResponse {

	@Autowired
	private SysAnnouncementReadInfoService readInfoService;

	@ApiOperation(value = "通告查看情况-获取分页", tags = { SwaggerTagConstants.ADMIN })
	@POST
	@Path("page")
	@Authorization(permission = "sysAnnouncementReadInfo:list")
	public WebApiResponse<Page<SysAnnouncementReadInfoDto>> getSysAnnounceReadInfoPage(@Context SecurityContext context,
			SysAnnouncementReadInfoQueryDto query) {
		if (StringUtils.isEmpty(query.getPageIndex())) {
			query.setPageIndex(0);
		}
		if (StringUtils.isEmpty(query.getPageSize())) {
			query.setPageSize(10);
		}
		query.setUserId(getUsId(context));
		return response(readInfoService.getSysAnnouncementReadInfoPage(query));
	}

	@ApiOperation(value = "通告查看情况-获取列表，用于上拉加载更多", tags = { SwaggerTagConstants.USER })
	@POST
	@Path("list")
	@Authorization(permission = "sysAnnouncementReadInfo:list")
	public WebApiResponse<List<SysAnnouncementReadInfoDto>> getSysAnnounceReadInfoList(@Context SecurityContext context,
			SysAnnouncementReadInfoQueryDto query) {
		return response(readInfoService.getSysAnnouncementReadInfoPage(query).getContent());
	}

	@ApiOperation(value = "通告查看情况-通过Id查询", tags = { SwaggerTagConstants.ADMIN })
	@GET
	@Path("{id}")
	@Authorization(permission = "sysAnnouncementReadInfo:list")
	public WebApiResponse<SysAnnouncementReadInfoDto> getSysAnnounceReadInfo(@Context SecurityContext context,
			@ApiParam(required = true, value = "sysAnnouncementReadInfo Id") @PathParam("id") String id) {
		return response(readInfoService.get(id));
	}

	@ApiOperation(value = "通告查看情况-添加|更新", tags = { SwaggerTagConstants.ADMIN })
	@POST
	@Authorization(permission = { "sysAnnouncementReadInfo:add,sysAnnouncementReadInfo:update" })
	public WebApiResponse<SysAnnouncementReadInfoDto> addOrUpdateSysAnnounceReadInfo(
			SysAnnouncementReadInfoDto SysAnnounceReadInfoDto, @Context SecurityContext context) {
		return response(readInfoService.addOrUpdateSysAnnouncementReadInfo(SysAnnounceReadInfoDto));
	}

	@ApiOperation(value = "通告查看情况-通过Id删除", tags = { SwaggerTagConstants.ADMIN })
	@DELETE
	@Path("{id}")
	@Authorization(permission = "sysAnnouncementReadInfo:delete")
	public WebApiResponse deleteSysAnnounceReadInfo(@Context SecurityContext context,
			@ApiParam(required = true, value = "sysAnnouncementReadInfo Id") @PathParam("id") String id) {
		readInfoService.delete(id);
		return done();
	}

	@ApiOperation(value = "获取未读消息数量", tags = { SwaggerTagConstants.ADMIN })
	@GET
	@Path("announce/count")
	@Authorization(permission = "sysAnnouncementReadInfo:count")
	public WebApiResponse<SysAnnounceUnReadDto> countAnnounceReadInfo(@Context SecurityContext context) {

		SysAnnounceUnReadDto result = readInfoService.countAnnounceReadInfo(getUsId(context));
		return response(result);
	}
	@ApiOperation(value = "全部标为已读", tags = { SwaggerTagConstants.ADMIN })
	@GET
	@Path("read/all")
	@Authorization(permission = "sysAnnouncementReadInfo:read")
	public WebApiResponse<Boolean> readAll(@Context SecurityContext context) {
		boolean result = readInfoService.readAll(getUsId(context));
		return response(result);
	}

}
