/**
 * create by 朱施健
 */
package com.flower.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

/**
 * @author 朱施健
 * 好友关系表
 */
@Entity
public class FriendRelation implements Serializable{
	@EmbeddedId
	public FriendRelationId id;
	
	public FriendRelation(){}
	public FriendRelation(String me,String friend){
		this.id = new FriendRelationId(me,friend);
	}
	
	@Embeddable
	public static class FriendRelationId implements Serializable{
		@Column(length = 50, nullable = false, updatable = false)
		public String me;
		
		@Index(name="index_friend")
		@Column(length = 50, nullable = false, updatable = false)
		public String friend;
		
		public FriendRelationId(){}
		public FriendRelationId(String me,String friend){
			this.me = me;
			this.friend = friend;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((friend == null) ? 0 : friend.hashCode());
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
			FriendRelationId other = (FriendRelationId) obj;
			if (friend == null) {
				if (other.friend != null)
					return false;
			} else if (!friend.equals(other.friend))
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
