package com.kubepay.webflux.literx;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Task {
	
	private final String id;
    private final String title;
    private final TaskType type;
    private final LocalDate createdOn;
    private boolean done = false;
    private Set<String> tags = new HashSet<>();
    private LocalDate dueOn;
    
    Task(String id, String title, TaskType type, LocalDate createdOn) {
		this.id = id;
		this.type=type;
		this.title=title;
		this.createdOn=createdOn;
	}
	
	Task(String title, TaskType type, LocalDate createdOn) {
		id = UUID.randomUUID().toString();
		this.type=type;
		this.title=title;
		this.createdOn=createdOn;
	}
	
	Task addTag(String tag) {
		tags.add(tag);
		return this;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public LocalDate getDueOn() {
		return dueOn;
	}

	public void setDueOn(LocalDate dueOn) {
		this.dueOn = dueOn;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public TaskType getType() {
		return type;
	}

	public LocalDate getCreatedOn() {
		return createdOn;
	}

}
