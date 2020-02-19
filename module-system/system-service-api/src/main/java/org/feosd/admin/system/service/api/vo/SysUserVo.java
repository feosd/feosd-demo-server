package org.feosd.admin.system.service.api.vo;

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
@ApiModel(value = "SysUserVo", description="个人信息")
public class SysUserVo {
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value="真实姓名")
    private String name;

    @ApiModelProperty(value="性别 0-男，1-女")
    private String sex;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "生日")
    private LocalDateTime birthday;

    @ApiModelProperty(value="手机号")
    private String mobile;

    @ApiModelProperty(value="邮箱")
    private String email;

}
