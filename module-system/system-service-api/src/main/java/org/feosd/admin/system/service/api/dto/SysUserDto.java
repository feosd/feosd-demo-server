package org.feosd.admin.system.service.api.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import org.feosd.auth.service.api.dto.MenuTreeDto;
import org.feosd.auth.service.api.dto.UserDto;
import org.feosd.base.common.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor // 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：https://www.jianshu.com/p/ae1cd9ad6729
@ApiModel(value = "SysUserDto", description="系统用户")
@ExcelTarget("sysUserDto")
public class SysUserDto extends UserDto {

    @ApiModelProperty(value = "性别 0-男，1-女")
    @Dict(dicCode = "sex")
    @Excel(name = "性别",replace = {"男_0","女_1"},orderNum = "6")
    private String sex;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "生日")
    @Excel(name = "生日",format = "yyyy-MM-dd",orderNum = "7")
    private LocalDateTime birthday;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱",orderNum = "8")
    private String email;

    @ApiModelProperty(value = "排序",required = true)
    @Excel(name = "排序",orderNum = "9")
    private Integer sortOrder;

    @ApiModelProperty(value = "部门Id",required = true)
    @NotNull(message = "部门Id不能为空")
    //@Dict(dicCode = "id",dictTable="sys_depart",dicText="depart_name")
    private String departId;

    @Excel(name = "所属部门*",orderNum = "10")
    @ApiModelProperty(value = "部门名称 回显用不必传")
    private String departName;

    @ApiModelProperty(value = "部门 回显用不必传")
    private SysDepartDto sysDepartDto;

    @ApiModelProperty(value = "更新人")
    private Long updateBy;

    @ApiModelProperty(value = "菜单树 回显用不必传")
    private List<MenuTreeDto> menus;

    @ApiModelProperty(value = "第rowNum行,用于Excel导入",hidden = true)
    private String rowNum = "";
}
