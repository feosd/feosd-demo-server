package org.feosd.admin.system.web.api;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.feosd.admin.common.SwaggerTagConstants;
import org.feosd.admin.system.service.api.SysUserService;
import org.feosd.admin.system.service.api.dto.SysUserDto;
import org.feosd.admin.system.service.api.dto.UserTreeDto;
import org.feosd.admin.system.service.api.vo.SysUserVo;
import org.feosd.auth.client.Authorization;
import org.feosd.base.service.DataCache;
import org.feosd.base.service.api.query.ListQuery;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.query.Query;
import org.feosd.base.web.api.jersey.IResponse;
import org.feosd.base.web.api.jersey.security.ISecurityContext;
import org.feosd.base.web.api.jersey.security.SessionContext;
import org.feosd.base.web.api.response.WebApiResponse;
import org.feosd.base.web.api.utils.ResponseUtil;
import org.feosd.common.syslog.web.aspect.SysLog;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.io.InputStream;
import java.util.List;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Path("sysUser")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Api(tags = "") // swagger 生成的tag标签，用于前端代码分类生成。建议在common中统一定义常量。
@RolesAllowed("*")// 大用户分类，比如一个货车匹配项目，具备四类用户：匿名（所有人都可访问）、 司机、货主、平台管理员。 每一类用户的接口相关隔离，非该用户身份不能访问。该注解可用于方法上。建议在common中统一定义常量。
//@Authorization(permission = {"sysUser:list", "sysUser:update", "sysUser:delete", "sysUser:add"}, logic = Logic.OR)// 权限管理。 需要具备的权限，logic.And 标识必须具备所有权限才可访问，logic.Or标识具备其中一个即可访问。 该注解可用于方法上。
@Slf4j
public class SysUserApi implements ISecurityContext, IResponse {

    @Autowired
    SysUserService sysUserService;
    @Autowired
    SessionContext sessionContext;
    @Autowired
    DataCache dataCache;

    @ApiOperation(value = "用户-获取分页", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("page")
    @Authorization(permission = "sysUser:list")
    public WebApiResponse<Page<SysUserDto>> getSysUserPage(@Context SecurityContext context, PageQuery query) {
        return response(sysUserService.getSysUserPage(query));
    }

    @ApiOperation(value = "用户-获取列表，用于上拉加载更多", tags = {SwaggerTagConstants.USER})
    @POST
    @Path("list")
    @Authorization(permission = "sysUser:list")
    public WebApiResponse<List<SysUserDto>> getSysUserList(@Context SecurityContext context, ListQuery query) {
        return response(sysUserService.getSysUserPage(query.toPageQuery()).getContent());
    }

    @ApiOperation(value = "用户-导入", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("import")
    @Consumes({"multipart/form-data"})
    @Authorization(permission = "sysUser:add,sysUser:update")
    public WebApiResponse importSysUser(@Context SecurityContext context, @FormDataParam("file") InputStream inputStream) {
        sysUserService.importExcel(getUsId(context),inputStream);
        return done();
    }

    @ApiOperation(value = "用户-导出", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("export")
    @Authorization(permission = "sysUser:list")
    public void exportSysUserList(@Context SecurityContext context,@Context HttpServletResponse response, Query query) {
        String title = "用户信息";
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(
                title, null, title), SysUserDto.class, sysUserService.getSysUserList(query));
        //ResponseUtil.excelOut(workbook,"/Users/zhangpw/file/测试.xls");
        ResponseUtil.excelOut(workbook,title+".xls",response);
    }

    @ApiOperation(value = "用户-通过Id查询", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("{id}")
    @Authorization(permission = "sysUser:list")
    public WebApiResponse<SysUserDto> getSysUser(@Context SecurityContext context, @ApiParam(required = true, value = "sysUser Id") @PathParam("id") Long id) {
        return response(sysUserService.get(id));
    }

    @ApiOperation(value = "用户-通过用户名查询", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("username/{username}")
    @Authorization(permission = "sysUser:list")
    public WebApiResponse<SysUserDto> getSysUserByUserName(@Context SecurityContext context, @ApiParam(required = true, value = "username") @PathParam("username") String username) {
        return response(sysUserService.findByUsername(username));
    }

    @SysLog("添加/更新用户")
    @ApiOperation(value = "用户-添加|更新", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Authorization(permission = {"sysUser:add,sysUser:update"})
    public WebApiResponse<SysUserDto> addOrUpdateSysUser(SysUserDto sysUserDto, @Context SecurityContext context) {
        if (sysUserDto.getId() == null) sysUserDto.setCreateUserId(getUsId(context));
        sysUserDto.setUpdateBy(getUsId(context));
        return response(sysUserService.addOrUpdateSysUser(sysUserDto));
    }

    @SysLog("删除用户")
    @ApiOperation(value = "用户-通过Id删除", tags = {SwaggerTagConstants.ADMIN})
    @DELETE
    @Path("{id}")
    @Authorization(permission = "sysUser:delete")
    public WebApiResponse deleteSysUser(@Context SecurityContext context, @ApiParam(required = true, value = "sysUser Id") @PathParam("id") Long id) {
        sysUserService.delete(id);
        return done();
    }

    @SysLog("获取个人信息")
    @ApiOperation(value = "用户-获取个人信息", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("ownInfo")
    public WebApiResponse<SysUserDto> getSysUserInfo(@Context SecurityContext context) {
        long start = System.currentTimeMillis();
        SysUserDto sysUserDto = sysUserService.getSysUser(getUsId(context));
        long end = System.currentTimeMillis();
        log.info("获取用户信息耗时:"+ (end - start)+"ms");
        return response(sysUserDto);
    }

    @SysLog("修改个人信息")
    @ApiOperation(value = "用户-修改个人信息", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("updateSysUser")
    public WebApiResponse<SysUserDto> updateSysUser(@Context SecurityContext context, SysUserVo sysUserVo) {
        SysUserDto sysUserDto = sysUserService.updateSysUser(sysUserVo);
        return response(sysUserDto);
    }

    @GET
    @Path("tree")
    @ApiOperation(value = "用户-获取用户树", tags = {SwaggerTagConstants.ADMIN})
    public WebApiResponse<List<UserTreeDto>> getUserTree(@Context SecurityContext context) {
        return response(sysUserService.getUserTree());
    }
}
