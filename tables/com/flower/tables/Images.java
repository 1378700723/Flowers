/**
 * create by 朱施健
 */
package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author 朱施健
 *
 */
@Entity
public class Images {
	@Id
	@Column(length=100,nullable=false,updatable=false)
	public String name;
	
	@Column(length=32*1024*1024,nullable=false)
	public byte[] datas;
}
