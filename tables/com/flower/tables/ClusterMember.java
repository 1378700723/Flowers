/**
 * create by 朱施健
 */
package com.flower.tables;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

/**
 * @author 朱施健
 * 群成员
 */
@Entity
public class ClusterMember {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "persistenceGenerator", strategy = "increment")
	@Column(nullable=false,updatable=false)
	public int id;
	
	@Index(name="index_uid")
	@Column(length = 50, nullable = false, updatable = false)
	public String uid;
	
	//是否开启消息提醒
	@Column(nullable = false)
	public boolean isOpenMsgAlert = true;
	
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name="gid",nullable=false)
	public Cluster cluster;
}
