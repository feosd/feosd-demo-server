package org.feosd.admin.system.web.api;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.feosd.admin.common.SwaggerTagConstants;
import org.feosd.admin.system.service.api.SysDepartService;
import org.feosd.admin.system.service.api.dto.SysDepartDto;
import org.feosd.auth.client.Authorization;
import org.feosd.auth.service.api.group.Insert;
import org.feosd.base.service.api.query.ListQuery;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.query.Query;
import org.feosd.base.web.api.jersey.IResponse;
import org.feosd.base.web.api.jersey.security.ISecurityContext;
import org.feosd.base.web.api.response.WebApiResponse;
import org.feosd.base.web.api.utils.ResponseUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

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
@Path("sysDepart")
@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
//@Api(tags = "") // swagger 生成的tag标签，用于前端代码分类生成。建议在common中统一定义常量。
@RolesAllowed("*")// 大用户分类，比如一个货车匹配项目，具备四类用户：匿名（所有人都可访问）、 司机、货主、平台管理员。 每一类用户的接口相关隔离，非该用户身份不能访问。该注解可用于方法上。建议在common中统一定义常量。
//@Authorization(permission = {"sysDepart:list", "sysDepart:update", "sysDepart:delete", "sysDepart:add"}, logic = Logic.OR)// 权限管理。 需要具备的权限，logic.And 标识必须具备所有权限才可访问，logic.Or标识具备其中一个即可访问。 该注解可用于方法上。
@Slf4j
public class SysDepartApi implements ISecurityContext, IResponse {

    @Autowired
    SysDepartService sysDepartService;

    @ApiOperation(value = "部门-获取分页", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("page")
    @Authorization(permission = "sysDepart:list")
    public WebApiResponse<Page<SysDepartDto>> getSysDepartPage(@Context SecurityContext context, PageQuery query) {
        return response(sysDepartService.getSysDepartPage(query));
    }

    @ApiOperation(value = "部门-获取列表，用于上拉加载更多", tags = {SwaggerTagConstants.USER})
    @POST
    @Path("list")
    @Authorization(permission = "sysDepart:list")
    public WebApiResponse<List<SysDepartDto>> getSysDepartList(@Context SecurityContext context, ListQuery query) {
        return response(sysDepartService.getSysDepartPage(query.toPageQuery()).getContent());
    }

    @ApiOperation(value = "部门-导入", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("import")
    @Consumes({"multipart/form-data"})
    @Authorization(permission = "sysDepart:add,sysDepart:update")
    public WebApiResponse importSysDepart(@Context SecurityContext context, @FormDataParam("file") InputStream inputStream) {
        sysDepartService.importExcel(inputStream);
        return done();
    }

    @ApiOperation(value = "部门-导出", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Path("export")
    @Authorization(permission = "sysDepart:list")
    public void exportSysDepartList(@Context SecurityContext context, @Context HttpServletResponse response, Query query) {
        String title = "部门信息";
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(
                title, null, title), SysDepartDto.class, sysDepartService.getSysDepartList(query));
        ResponseUtil.excelOut(workbook,title+".xls",response);
    }

    @ApiOperation(value = "部门-通过Id查询", tags = {SwaggerTagConstants.ADMIN})
    @GET
    @Path("{id}")
    @Authorization(permission = "sysDepart:list")
    public WebApiResponse<SysDepartDto> getSysDepart(@Context SecurityContext context, @ApiParam(required = true, value = "sysDepart Id") @PathParam("id") String id) {
        return response(sysDepartService.get(id));
    }


    @ApiOperation(value = "部门-添加|更新", tags = {SwaggerTagConstants.ADMIN})
    @POST
    @Authorization(permission = {"sysDepart:add,sysDepart:update"})
    public WebApiResponse<SysDepartDto> addOrUpdateSysDepart(@Validated({Insert.class}) SysDepartDto sysDepartDto, @Context SecurityContext context) {
        if(sysDepartDto.getId() == null) sysDepartDto.setCreateBy(getUsId(context));
        sysDepartDto.setUpdateBy(getUsId(context));
        return response(sysDepartService.addOrUpdateSysDepart(sysDepartDto));
    }


    @ApiOperation(value = "部门-通过Id删除", tags = {SwaggerTagConstants.ADMIN})
    @DELETE
    @Path("{id}")
    @Authorization(permission = "sysDepart:delete")
    public WebApiResponse deleteSysDepart(@Context SecurityContext context, @ApiParam(required = true, value = "sysDepart Id") @PathParam("id") String id) {
        sysDepartService.delete(id);
        return done();
    }

    @GET
    @Path("tree")
    @ApiOperation(value = "部门-获取部门树", tags = {SwaggerTagConstants.ADMIN})
    public WebApiResponse<List<SysDepartDto>> getDepartTree(@Context SecurityContext context) {
        return response(sysDepartService.getSysDepartTreeByUsId(getUsId(context)));
    }

}
