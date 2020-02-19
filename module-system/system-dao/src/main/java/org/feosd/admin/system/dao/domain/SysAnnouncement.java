package org.feosd.admin.system.dao.domain;


import org.feosd.base.common.annotation.DBColumn;
import org.feosd.base.common.annotation.DBTable;
import org.feosd.base.jpa.domain.SnowIdEntity;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * code generator
 * author: Steven
 * version: 1.0.0
 */
@Entity
@DBTable(comment = "系统通告表")
@Table(name = "sys_announcement")
@Data
// 不用写get set 方法简化代码，需要安装lombok插件。 详见开发文档：http://172.16.28.234:4000/javahou-duan-xiang-guan-ff08-ge-ren-gong-xian-ff09/lombok.html
public class SysAnnouncement extends SnowIdEntity {

    @DBColumn(comment = "标题")
    @Column(length = 32)
    private String title;

    @DBColumn(comment = "消息类型1:通知公告2:系统消息")
    @Column(length = 2)
    private String msgCategory;

    @DBColumn(comment = "内容")
    @Lob
    private String msgContent;

    @DBColumn(comment = "通告对象类型（USER:指定用户，ALL:全体用户）")
    @Column(length = 4)
    private String msgType;

    @DBColumn(comment = "指定用户")
    @Lob
    @Type(type = "org.feosd.base.jpa.domain.usertype.LongListUserType")
    private List<Long> userIds;

    @DBColumn(comment = "发布状态（0未发布，1已发布，2已撤销）")
    @Column(length = 2)
    private String sendStatus;

    @DBColumn(comment = "开始时间")
    private LocalDateTime startTime;

    @DBColumn(comment = "结束时间")
    private LocalDateTime endTime;

    @DBColumn(comment = "消息优先级")
    private Integer msgLevel;


    @DBColumn(comment = "发布人id")
    private Long sender;
    @DBColumn(comment = "发布人name")
    @Column(length = 50)
    private String senderName;

    @DBColumn(comment = "发布时间")
    private LocalDateTime sendTime;
}
