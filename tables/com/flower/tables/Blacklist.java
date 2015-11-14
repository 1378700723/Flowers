/**
 * create by 朱施健
 */
package com.flower.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * @author 朱施健
 * 黑名单
 */
@Entity
public class Blacklist implements Serializable{
	
	@EmbeddedId
	public BlacklistId id;
	
	@Embeddable
	public static class BlacklistId implements Serializable{
		@Column(length = 50, nullable = false, updatable = false)
		public String me;
		
		@Column(length = 50, nullable = false, updatable = false)
		public String blacker;

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((blacker == null) ? 0 : blacker.hashCode());
			result = prime * result + ((me == null) ? 0 : me.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BlacklistId other = (BlacklistId) obj;
			if (blacker == null) {
				if (other.blacker != null)
					return false;
			} else if (!blacker.equals(other.blacker))
				return false;
			if (me == null) {
				if (other.me != null)
					return false;
			} else if (!me.equals(other.me))
				return false;
			return true;
		}
	}
}
