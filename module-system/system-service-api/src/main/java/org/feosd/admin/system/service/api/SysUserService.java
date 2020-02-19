package org.feosd.admin.system.service.api;

import org.feosd.admin.system.service.api.dto.SysUserDto;
import org.feosd.admin.system.service.api.dto.UserTreeDto;
import org.feosd.admin.system.service.api.vo.SysUserVo;
import org.feosd.base.service.api.BaseService;
import org.feosd.base.service.api.query.PageQuery;
import org.feosd.base.service.api.query.Query;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
public interface SysUserService extends BaseService<SysUserDto, Long> {
    //已具备基本的增删改查功能，自定义业务请在以下区域书写。

    Page<SysUserDto> getSysUserPage(PageQuery query);

    List<SysUserDto> getSysUserList(Query query);

    void importExcel(Long usId,InputStream inputStream);

    SysUserDto addOrUpdateSysUser(SysUserDto sysUserDto);

    SysUserDto getSysUser(Long usId);

    SysUserDto findByUsername(String username);

    //修改个人信息
    SysUserDto updateSysUser(SysUserVo sysUserVo);

    List<SysUserDto> findByOrgCodeLike(String orgCode);

    //获取所有用户
    List<SysUserDto> findAll();

    List<SysUserDto> findByManageUserId(Long usId);

    //获取用户树
    List<UserTreeDto> getUserTree();
}
