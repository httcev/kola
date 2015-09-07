package de.httc.channels.api;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class JpaObject {
	private Date lastModified;
	private Date dateCreated;

	public Date getDateCreated() {
		return dateCreated;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	
	@PreUpdate
	@PrePersist
	private void updateTimeStamps() {
		lastModified = new Date();
		if (dateCreated == null) {
			dateCreated = new Date();
		}
	}
}
