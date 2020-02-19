package org.feosd.admin.system.service.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "查看情况查询DTO")
public class SysAnnouncementReadInfoQueryDto {
	private Integer pageIndex;
	private Integer pageSize;
	private Long userId;
	private String title;
	private String senderName;

}
