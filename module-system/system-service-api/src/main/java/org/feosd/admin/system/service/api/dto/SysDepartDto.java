package org.feosd.admin.system.service.api.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import org.feosd.auth.service.api.group.Insert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;


/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor // 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：https://www.jianshu.com/p/ae1cd9ad6729
@ApiModel(value = "SysDepartDto", description="系统通告")
public class SysDepartDto {
    @ApiModelProperty(value = "部门Id")
    private String id;

    @ApiModelProperty(value = "部门编码 回显用不必传")
    @Excel(name = "部门编码",orderNum = "1")
    private String orgCode;

    @NotEmpty(message = "部门名称不能为空",groups = {Insert.class})
    @ApiModelProperty(value = "部门名称")
    @Excel(name = "部门名称*",orderNum = "2")
    private String departName;

    @ApiModelProperty(value = "部门描述")
    @Excel(name = "部门描述",orderNum = "3")
    private String description;

    @ApiModelProperty(value = "部门排序")
    @Excel(name = "部门排序",orderNum = "4")
    private Integer sortOrder;

    @ApiModelProperty(value = "父部门Id")
    private String parentId;

    @ApiModelProperty(value = "父部门名称 回显用不必传")
    @Excel(name = "父部门名称",orderNum = "5")
    private String parentName;

    @ApiModelProperty(value = "子部门 回显用不必传")
    private List<SysDepartDto> children;

    @ApiModelProperty(value = "部门类型 回显用不必传")
    private String orgType;

    @ApiModelProperty(value = "创建人",hidden = true)
    private Long createBy;

    @ApiModelProperty(value = "更新人",hidden = true)
    private Long updateBy;

    @ApiModelProperty(value = "第rowNum行,用于Excel导入",hidden = true)
    private String rowNum = "";
}
