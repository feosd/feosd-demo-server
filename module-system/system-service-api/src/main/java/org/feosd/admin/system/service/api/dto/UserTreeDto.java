package org.feosd.admin.system.service.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "UserTreeDto", description="部门用户树")
public class UserTreeDto {

	@ApiModelProperty(value = "id")
	private String id;
	@ApiModelProperty(value = "父id")
	private String pid;
	@ApiModelProperty(value = "用户名")
	private String username;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "手机号")
	private String mobile;
	@ApiModelProperty(value = "头像")
	private String avatar;
	@ApiModelProperty(value = "类型 0-部门，1-用户")
	private String type;

	private List<UserTreeDto> children = new ArrayList<>();

	public UserTreeDto(String id, String pid) {
		this.id = id;
		this.pid = pid;
	}
}
