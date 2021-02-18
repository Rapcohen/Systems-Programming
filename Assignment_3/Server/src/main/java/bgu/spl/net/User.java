package bgu.spl.net;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class User {
    private final String username;
    private final String password;
    private final TreeSet<Course> courses;
    private final boolean isAdmin;
    private final AtomicBoolean isLoggedIn;

    public User(String username, String password,boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isLoggedIn = new AtomicBoolean(false);
        this.courses = new TreeSet<>(Comparator.comparingInt(Course::getOrderNum));
    }
    public boolean login(){
        return isLoggedIn.compareAndSet(false,true);
    }
    public void logout(){
        isLoggedIn.compareAndSet(true, false);
    }

    public String getUsername() {
        return username;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public boolean removeCourse(Course course) {
        return courses.remove(course);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean hasKdamCourses(List<Short> kdams){
        Set<Short> nums = courses.stream().map(Course::getCourseNum).collect(Collectors.toSet());
        for (Short kdam : kdams){
            if (!nums.contains(kdam)) return false;
        }
        return true;
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public Set<Short> getCourseNums() {
        return courses.stream().map(Course::getCourseNum).collect(Collectors.toSet());
    }


    public String coursesToString() {
        String output = "[";
        for (Course course : courses) {
            output += course.getCourseNum() + ",";
        }
        if (!courses.isEmpty()) {
            output = output.substring(0, output.length() -1);
        }
        return output + "]";
    }

    @Override
    public String toString() {
        return "Student: " + username + "\n" +
                "Courses: " + coursesToString();
    }
}
