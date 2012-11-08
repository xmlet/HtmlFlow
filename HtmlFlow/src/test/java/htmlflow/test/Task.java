package htmlflow.test;

import java.util.Date;

public class Task {
	private static int nrOfTasks = 0; 
	private final int id;
	private String title;
	private String description;
	private Priority priority;
	private Status status;
	private Date creationDate;
	private Date completedDate;
	public Task(String title, String description, Priority priority, Status status) {
		this.id = ++nrOfTasks;
		this.title = title;
		this.description = description;
		this.priority = priority;
		this.status = status;
	}
	
	public static int getNrOfTasks() {
		return nrOfTasks;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Priority getPriority() {
		return priority;
	}

	public Status getStatus() {
		return status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", title=" + title + ", description="
				+ description + ", priority=" + priority + ", status=" + status
				+ ", creationDate=" + creationDate + ", completedDate="
				+ completedDate + "]";
	}
	
}
