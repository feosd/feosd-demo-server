package org.feosd.admin.system.dao.domain;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.feosd.base.common.annotation.DBColumn;
import org.feosd.base.common.annotation.DBTable;
import org.feosd.base.jpa.domain.SnowIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Entity
@DBTable(comment = "系统通告查看情况表")
@Table(name = "sys_announce_read_info")
@Data
@EqualsAndHashCode(callSuper = false)
// 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：http://172.16.28.234:4000/javahou-duan-xiang-guan-ff08-ge-ren-gong-xian-ff09/lombok.html
public class SysAnnouncementReadInfo extends SnowIdEntity {

    @DBColumn(comment ="系统通告主键ID")
    @Column(length = 20)
    private String announceId;
    @DBColumn(comment ="用户主键ID")
    @Column(length = 20)
    private Long userId;
    @DBColumn(comment = "已读 1  未读 0 默认 未读")
    @Column(length = 20,name = "is_read")
    private String read;
    @DBColumn(comment = "阅读时间")
    private LocalDateTime readDate;
}
