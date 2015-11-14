/**
 * create by 朱施健
 */
package com.flower.tables;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * @author 朱施健
 * 群
 */
@Entity
@DynamicUpdate
@DynamicInsert
public class Cluster {
	
	@Id
	@Column(length = 50, nullable = false, updatable = false)
	public String gid;
	
	//群名称
	@Column(nullable=false)
	public String name;
	
	//群描述
	@Column
	public String description;
	
	//创建者
	@Column(nullable=false)
	public String creater;
	
	//创建时间
	@Column(length=19,nullable=false,updatable=false)
	public String createTime;
	
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="gid")
	public Set<ClusterMember> members;
}
