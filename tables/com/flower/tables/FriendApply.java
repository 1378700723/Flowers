/**
 * create by 朱施健
 */
package com.flower.tables;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.annotations.Index;

import com.flower.enums.ApplyState;

/**
 * @author 朱施健
 * 好友申请表
 */
@Entity
public class FriendApply implements Serializable{
	public FriendApplyId id;
	public String applyMsg;
	public ApplyState applyState = ApplyState.等待认证;

	@EmbeddedId
	public FriendApplyId getId() {
		return id;
	}
	public void setId(FriendApplyId id) {
		this.id = id;
	}

	@Column(length = 50)
	public String getApplyMsg() {
		return applyMsg;
	}
	public void setApplyMsg(String applyMsg) {
		this.applyMsg = applyMsg;
	}

	@Column
	public byte getApplyState() {
		return applyState.flag;
	}

	public void setApplyState(byte applyState) {
		this.applyState = ApplyState.getEnum(applyState);
	}
	
	@Embeddable
	public static class FriendApplyId implements Serializable{
		public String me;
		public String applyTarget;
		
		public FriendApplyId(){}
		public FriendApplyId(String me,String applyTarget){
			this.me =me;
			this.applyTarget = applyTarget;
		}
		
		
		@Column(length = 50, nullable = false, updatable = false)
		public String getMe() {
			return me;
		}
		public void setMe(String me) {
			this.me = me;
		}

		@Index(name="index_applyTarget")
		@Column(length = 50, nullable = false, updatable = false)
		public String getApplyTarget() {
			return applyTarget;
		}
		public void setApplyTarget(String applyTarget) {
			this.applyTarget = applyTarget;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((applyTarget == null) ? 0 : applyTarget.hashCode());
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
			FriendApplyId other = (FriendApplyId) obj;
			if (applyTarget == null) {
				if (other.applyTarget != null)
					return false;
			} else if (!applyTarget.equals(other.applyTarget))
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
