package org.feosd.admin.system.web.api;

import org.feosd.admin.common.SwaggerTagConstants;
import org.feosd.admin.system.service.api.SysTestService;
import org.feosd.admin.system.service.api.dto.SysTestDto;
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

@Path("sysTest")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@RolesAllowed("*")// 大用户分类，比如一个货车匹配项目，具备四类用户：匿名（所有人都可访问）、 司机、货主、平台管理员。 每一类用户的接口相关隔离，非该用户身份不能访问。该注解可用于方法上。建议在common中统一定义常量。
//@Authorization(permission = {"sysTest:list", "sysTest:update", "sysTest:delete", "sysTest:add"}, logic = Logic.OR)// 权限管理。 需要具备的权限，logic.And 标识必须具备所有权限才可访问，logic.Or标识具备其中一个即可访问。 该注解可用于方法上。
@Slf4j
public class SysTestApi implements ISecurityContext, IResponse {

    @Autowired
    SysTestService sysTestService;

    @ApiOperation(value = "系统测试-获取分页", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("page")
    @Authorization(permission = "sysTest:list")
    public WebApiResponse<Page<SysTestDto>> getSysTestPage(@Context SecurityContext context,
    		PageQuery query) {
        return response(sysTestService.getSysTestPage(query));
    }

    @ApiOperation(value = "系统测试-获取列表，用于上拉加载更多", tags = {SwaggerTagConstants.USER})
    @GET
    @Path("list")
    @Authorization(permission = "sysTest:list")
    public WebApiResponse<List<SysTestDto>> getSysTestList(@Context SecurityContext context) {
        ListQuery query = new ListQuery();
        return response(sysTestService.getSysTestPage(query.toPageQuery()).getContent());
    }


    @ApiOperation(value = "系统测试-通过Id查询", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("{id}")
    @Authorization(permission = "sysTest:list")
    public WebApiResponse<SysTestDto> getSysTest(@Context SecurityContext context,
    		@ApiParam(required = true, value = "sysTest Id") @PathParam("id") String id) {
        return response(sysTestService.get(id));
    }


    @ApiOperation(value = "系统测试-添加|更新", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Authorization(permission = {"sysTest:add,sysTest:update"})
    public WebApiResponse<SysTestDto> addOrUpdateSysTest(SysTestDto sysTestDto,
    		@Context SecurityContext context) {
        return response(sysTestService.addOrUpdateSysTest(sysTestDto));
    }


    @ApiOperation(value = "系统测试-通过Id删除", tags = {SwaggerTagConstants.ADMIN})
    @DELETE
    @Path("{id}")
    @Authorization(permission = "sysTest:delete")
    public WebApiResponse deleteSysTest(@Context SecurityContext context, @ApiParam(required = true, value = "sysTest Id") @PathParam("id") String id) {
        sysTestService.delete(id);
        return done();
    }

}
