package org.feosd.admin.system.service.api.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：https://www.jianshu.com/p/ae1cd9ad6729
@ApiModel(value = "SysAnnounceUnReadDto", description="未读公告数据")
public class SysAnnounceUnReadDto {
	@ApiModelProperty(value = "通知公告未读数量")
	private Integer announceTotal;
	@ApiModelProperty(value = "系统消息未读数量")
	private Integer systemTotal;
	@ApiModelProperty(value = "通知公告数据")
	private List<SysAnnouncementReadInfoDto> announce;
	@ApiModelProperty(value = "系统消息数据")
	private List<SysAnnouncementReadInfoDto> system;

}
