/*
 * Copyright (c) 2016, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package htmlflow.test.model;

import htmlflow.test.model.Status;

import java.util.Date;

/**
 * @author Miguel Gamboa
 */
public class Task {
    private static int nrOfTasks = 0;
    private final int id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private Date creationDate;
    private Date completedDate;
    public Task(String title, String description, Priority priority) {
        this.id = ++nrOfTasks;
        this.title = title;
        this.description = description;
        this.priority = priority;
    }
    public Task(String title, String description, Priority priority, Status status) {
        this(title,description,priority);
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
