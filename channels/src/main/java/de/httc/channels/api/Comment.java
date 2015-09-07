package de.httc.channels.api;

import javax.persistence.Entity;

@Entity
public class Comment extends JpaObject {
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
