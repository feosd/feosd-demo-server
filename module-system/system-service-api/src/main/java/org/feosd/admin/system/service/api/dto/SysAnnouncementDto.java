package org.feosd.admin.system.service.api.dto;


import org.feosd.base.common.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@ApiModel(value = "SysAnnouncementDto", description="系统通告")
public class SysAnnouncementDto {
    @ApiModelProperty(value = "通告Id")
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "消息类型1:通知公告2:系统消息")
    @Dict(dicCode = "msgCategory")
    private String msgCategory;

    @ApiModelProperty(value = "内容")
    private String msgContent;

    @ApiModelProperty(value = "通告对象类型（USER:指定用户，ALL:全体用户）")
    @Dict(dicCode = "msgType")
    private String msgType;
    @ApiModelProperty(value = "通告优先级 1一般 2重要 3紧急 code")
    @Dict(dicCode = "msgLevel")
    private Integer msgLevel;
    @ApiModelProperty(value = "通告优先级 1一般 2重要 3紧急 name")
    private String msgLevelStr;

    @ApiModelProperty(value = "指定用户Ids")
    private List<Long> userIds;

    @ApiModelProperty(value = "发布状态（0未发布，1已发布，2已撤销）")
    @Dict(dicCode = "sendStatus")
    private String sendStatus;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "发布人")
    private Long sender;
    @ApiModelProperty(value = "发布人姓名")
    private String senderName;

    @ApiModelProperty(value = "发布时间")
    private LocalDateTime sendTime;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "用戶信息")
    private List<SysUserDto> userDtos;

    public SysAnnouncementDto(Integer msgLevel, String title, String msgCategory,String msgType,LocalDateTime sendTime) {

    	this.msgLevel = msgLevel;
    	this.title = title;
    	this.msgCategory = msgCategory;
    	this.msgType = msgType;
    	this.sendTime = sendTime;
    }

}
