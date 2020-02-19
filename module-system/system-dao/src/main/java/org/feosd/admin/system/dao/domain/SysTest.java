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
@Table(name = "sys_test")
@DBTable(comment = "系统测试表")
@Data
public class SysTest extends SnowIdEntity {

    @DBColumn(comment = "手机号")
    @Column(length = 20)
    private String mobile;

    @DBColumn(comment = "经度")
    private Double lng;

    @DBColumn(comment = "纬度")
    private Double lat;

    @DBColumn(comment = "地址")
    private String address;

}
