/**
 * create by 朱施健
 */
package com.flower.tables;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * @author 朱施健
 *
 */
@Entity
public class Alias implements java.io.Serializable{
	
	@EmbeddedId
	public AliasId id;
	
	@Column(length = 50, nullable = false)
	public String alias;
	
	public Alias() {}

	public Alias(AliasId id, String alias) {
		this.id = id;
		this.alias = alias;
	}
	
	@Embeddable
	public static class AliasId implements java.io.Serializable {
		@Column(length = 50, nullable = false, updatable = false)
		public String uid;
		
		@Column(length = 50, nullable = false, updatable = false)
		public String dstUid;

		public AliasId() {}

		public AliasId(String uid, String dstUid) {
			this.uid = uid;
			this.dstUid = dstUid;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((dstUid == null) ? 0 : dstUid.hashCode());
			result = prime * result + ((uid == null) ? 0 : uid.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AliasId other = (AliasId) obj;
			if (dstUid == null) {
				if (other.dstUid != null)
					return false;
			} else if (!dstUid.equals(other.dstUid))
				return false;
			if (uid == null) {
				if (other.uid != null)
					return false;
			} else if (!uid.equals(other.uid))
				return false;
			return true;
		}
	}
}
