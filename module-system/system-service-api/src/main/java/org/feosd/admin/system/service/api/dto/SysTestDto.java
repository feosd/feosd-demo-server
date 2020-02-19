package org.feosd.admin.system.service.api.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "SysTestDto", description = "系统测试")
public class SysTestDto {

    @ApiModelProperty(value = "纬度")
    @Excel(name = "纬度")
    private java.lang.Double lat;

    @ApiModelProperty(value = "经度")
    @Excel(name = "经度")
    private java.lang.Double lng;

    @ApiModelProperty(value = "手机号")
    @Excel(name = "手机号")
    @NotNull(message = "手机号不能为空")
    private java.lang.String mobile;

    @ApiModelProperty(value = "地址")
    @Excel(name = "地址")
    private java.lang.String address;

    @ApiModelProperty(value = "Id")
    @Excel(name = "Id")
    private java.lang.String id;

    private LocalDateTime createTime;

}
