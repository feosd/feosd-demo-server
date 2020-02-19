package org.feosd.admin.system.dao.domain;

import org.feosd.auth.dao.domain.User;
import org.feosd.base.common.annotation.DBColumn;
import org.feosd.base.common.annotation.DBTable;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Entity
@Table(name = "sys_user")
@DBTable(comment = "系统用户表")
@Data
// 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：http://172.16.28.234:4000/javahou-duan-xiang-guan-ff08-ge-ren-gong-xian-ff09/lombok.html
public class SysUser extends User implements Serializable {

    @DBColumn(comment = "性别 0-男，1-女")
    @Column(length = 2)
    private String sex;

    @DBColumn(comment = "头像")
    @Column(length = 400)
    private String avatar;

    @DBColumn(comment = "生日")
    private LocalDateTime birthday;

    @DBColumn(comment = "邮箱")
    @Column(length = 24)
    private String email;

    @DBColumn(comment = "排序")
    private Integer sortOrder;

    //级联实体分离操作,通过程序来控制级联操作
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "sys_depart_id")
    @DBColumn(comment = "部门")
    private SysDepart sysDepart;

}
