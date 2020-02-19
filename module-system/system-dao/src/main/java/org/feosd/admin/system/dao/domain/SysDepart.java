package org.feosd.admin.system.dao.domain;


import org.feosd.base.common.annotation.DBColumn;
import org.feosd.base.common.annotation.DBTable;
import org.feosd.base.jpa.domain.SnowIdEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Entity
@DBTable(comment = "系统部门表")
@Table(name = "sys_depart")
@Data // 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：http://172.16.28.234:4000/javahou-duan-xiang-guan-ff08-ge-ren-gong-xian-ff09/lombok.html
public class SysDepart extends SnowIdEntity {

    @DBColumn(comment = "部门编码")
    @Column(length = 32)
    private String orgCode;

    @DBColumn(comment = "部门名称")
    @Column(length = 64)
    private String departName;

    @DBColumn(comment = "部门描述")
    @Column(length = 400)
    private String description;

    @DBColumn(comment = "部门排序")
    private Integer sortOrder;

    @DBColumn(comment = "父部门Id")
    private String parentId;

    @DBColumn(comment = "删除状态（0正常，1已删除）")
    @Column(length = 2)
    private String delFlag = "0";

    @DBColumn(comment = "部门类型 1，2，3 一二三级部门")
    @Column(length = 2)
    private String orgType;

    @DBColumn(comment = "创建人")
    private Long createBy;

    @DBColumn(comment = "更新人")
    private Long updateBy;
}
