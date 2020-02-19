package org.feosd.admin.system.service.api.dto;


import java.time.LocalDateTime;

import org.feosd.base.common.annotation.Dict;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SysAnnouncementReadInfoDto{
	@ApiModelProperty(value = "主键ID")
	private String id;
	@ApiModelProperty(value = "系统通告主键ID")
    private String announceId;
	@ApiModelProperty(value = "用户主键ID")
    private Long userId;
	@ApiModelProperty(value = "消息优先级code 1 一般 2 重要 3紧急")
	private Integer msgLevel;
	@ApiModelProperty(value = "消息优先级name 1 一般 2 重要 3紧急")
	private String msgLevelStr;
	@ApiModelProperty(value = "已读 1  未读 0 默认0")
	@Dict(dicCode = "readState")
    private String read;
	@ApiModelProperty(value = "是否读取字符串")
	private String readStr;
	@ApiModelProperty(value = "标题")
	private String title;
	@ApiModelProperty(value = "消息类型1:通知公告2:系统消息")
    @Dict(dicCode = "msgCategory")
    private String msgCategory;
	@ApiModelProperty(value = "发布人id")
    private Long sender;
	@ApiModelProperty(value = "发布人name")
    private String senderName;
	@ApiModelProperty(value = "内容")
    private String msgContent;
    @ApiModelProperty(value = "发布时间")
    private LocalDateTime sendTime;
	@ApiModelProperty(value = "阅读时间")
    private LocalDateTime readDate;
	public SysAnnouncementReadInfoDto(Integer msgLevel,Long sender,String senderName,String id, String title, String msgCategory,
			String msgContent,String read, LocalDateTime sendTime) {
		this.msgLevel=msgLevel;
		this.sender = sender;
		this.senderName = senderName;
		this.id = id;
		this.title = title;
		this.msgCategory = msgCategory;
		this.msgContent = msgContent;
		this.sendTime = sendTime;
		this.read = read;
	}

}
