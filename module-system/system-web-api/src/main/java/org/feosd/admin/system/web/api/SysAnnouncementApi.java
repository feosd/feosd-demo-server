package org.feosd.admin.system.web.api;

import com.alibaba.fastjson.JSON;
import org.feosd.admin.common.SwaggerTagConstants;
import org.feosd.admin.system.service.api.SysAnnouncementService;
import org.feosd.admin.system.service.api.SysUserService;
import org.feosd.admin.system.service.api.dto.SysAnnouncementDto;
import org.feosd.admin.system.service.api.dto.SysUserDto;
import org.feosd.auth.client.Authorization;
import org.feosd.base.service.api.query.ListQuery;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.web.api.jersey.IResponse;
import org.feosd.base.web.api.jersey.security.ISecurityContext;
import org.feosd.base.web.api.response.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Path("sysAnnouncement")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Api(tags = "") // swagger 生成的tag标签，用于前端代码分类生成。建议在common中统一定义常量。
//@RolesAllowed("*")// 大用户分类，比如一个货车匹配项目，具备四类用户：匿名（所有人都可访问）、 司机、货主、平台管理员。 每一类用户的接口相关隔离，非该用户身份不能访问。该注解可用于方法上。建议在common中统一定义常量。
//@Authorization(permission = {"sysAnnouncement:list", "sysAnnouncement:update", "sysAnnouncement:delete", "sysAnnouncement:add"}, logic = Logic.OR)// 权限管理。 需要具备的权限，logic.And 标识必须具备所有权限才可访问，logic.Or标识具备其中一个即可访问。 该注解可用于方法上。
@Slf4j
public class SysAnnouncementApi implements ISecurityContext, IResponse {
	private final static Websocket socket = new Websocket();

    @Autowired
    SysAnnouncementService sysAnnouncementService;
    @Autowired
    SysUserService sysUserService;

    @ApiOperation(value = "系统通告-获取分页", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("page")
    @Authorization(permission = "sysAnnouncement:list")
    public WebApiResponse<Page<SysAnnouncementDto>> getSysAnnouncementPage(@Context SecurityContext context,
    		PageQuery query) {
        return response(sysAnnouncementService.getSysAnnouncementPage(query));
    }

    @ApiOperation(value = "系统通告-获取列表，用于上拉加载更多", tags = {SwaggerTagConstants.USER})
    @POST
    @Path("list")
    @Authorization(permission = "sysAnnouncement:list")
    public WebApiResponse<List<SysAnnouncementDto>> getSysAnnouncementList(@Context SecurityContext context, ListQuery query) {
        return response(sysAnnouncementService.getSysAnnouncementPage(query.toPageQuery()).getContent());
    }


    @ApiOperation(value = "系统通告-通过Id查询", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("{id}")
    @Authorization(permission = "sysAnnouncement:list")
    public WebApiResponse<SysAnnouncementDto> getSysAnnouncement(@Context SecurityContext context,
    		@ApiParam(required = true, value = "sysAnnouncement Id") @PathParam("id") String id) {
        return response(sysAnnouncementService.getSysAnnouncement(id));
    }


    @ApiOperation(value = "系统通告-添加|更新", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Authorization(permission = {"sysAnnouncement:add,sysAnnouncement:update"})
    public WebApiResponse<SysAnnouncementDto> addOrUpdateSysAnnouncement(SysAnnouncementDto sysAnnouncementDto,
    		@Context SecurityContext context) {
    	if ("1".equals(sysAnnouncementDto.getSendStatus())) {

    		Long userId = getUsId(context);
			sysAnnouncementDto.setSender(userId);
			SysUserDto userDto = sysUserService.getSysUser(userId);
			if (userDto != null) {
				sysAnnouncementDto.setSenderName(userDto.getName());
			}
			if ("ALL".equals(sysAnnouncementDto.getMsgType())) {
				// 获取所有用户信息
				List<SysUserDto> users = sysUserService.findAll();
				List<Long> ids = users.stream().map(SysUserDto::getId).collect(Collectors.toList());
				sysAnnouncementDto.setUserIds(ids);
			}

                List<Long> userIds = sysAnnouncementDto.getUserIds();
                String message = JSON.toJSONString(sysAnnouncementDto);
                for (Long id : userIds) {
                    socket.sendOneMessage(id+"",message);
			}

		}


        return response(sysAnnouncementService.addOrUpdateSysAnnouncement(sysAnnouncementDto));
    }


    @ApiOperation(value = "系统通告-通过Id删除", tags = {SwaggerTagConstants.ADMIN})
    @DELETE
    @Path("{id}")
    @Authorization(permission = "sysAnnouncement:delete")
    public WebApiResponse deleteSysAnnouncement(@Context SecurityContext context, @ApiParam(required = true, value = "sysAnnouncement Id") @PathParam("id") String id) {
        sysAnnouncementService.delete(id);
        return done();
    }
    @ApiOperation(value = "发送指定消息到前台(测试websocket通讯)", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("/send/one/{userId}")
    @Authorization(permission = "sysAnnouncement:delete")
    public WebApiResponse testSend(@Context SecurityContext context,
    		@ApiParam(required = true, value = "用户ID")  @PathParam("userId") String userId) {
        socket.sendOneMessage(userId, "hello success");
        return done();
    }

}
