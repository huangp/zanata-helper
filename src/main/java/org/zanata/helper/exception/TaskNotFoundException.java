package org.zanata.helper.exception;

/**
 * @author Alex Eng <a href="aeng@redhat.com">aeng@redhat.com</a>
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String key) {
        super("Task not found:" + key);
    }
}
