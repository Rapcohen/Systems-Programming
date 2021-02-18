package bgu.spl.net;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
    private final ConcurrentHashMap<Short, Course> courseMap;
    private final ConcurrentHashMap<String, User> userMap;
    private final List<Short> courseList;
    private static class SingletonClassHolder {
        static final Database instance = new Database();
    }

    private Database() {
        courseMap = new ConcurrentHashMap<>();
        userMap = new ConcurrentHashMap<>();
        courseList = new ArrayList<>();
        initialize("Courses.txt");
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return SingletonClassHolder.instance;
    }

    /**
     * loads the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFilePath))) {
            int counter = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] stringArr = line.split("\\|");
                short courseNum = Short.parseShort(stringArr[0]);
                String courseName = stringArr[1];
                List<Short> kdamList = parseList(stringArr[2]);
                int maxStudents = Integer.parseInt(stringArr[3]);
                courseMap.put(courseNum, new Course(courseNum, courseName, kdamList, maxStudents, counter++));
                courseList.add(courseNum); //Added in order of courses in the input file
            }
            for (Course course : courseMap.values()) {
                course.getKdamCoursesList().sort(Comparator.comparing((item) -> courseList.indexOf(item)));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * parses the Kdam-Course list received from input file.
     *
     * @param list the Kdam-course list as a String.
     * @return @code{List<Short>} containing the Kdam-course numbers.
     */
    private List<Short> parseList(String list) {
        if (list.equals("[]")) {
            return new ArrayList<>();
        }
        list = list.substring(1, list.length() - 1);
        String[] strings = list.split(",");
        List<Short> output = new ArrayList<>();
        for (String str : strings) {
            output.add(Short.parseShort(str));
        }
        return output;
    }

    /**
     * Adds new {@link User} to the DB if the given username is available.
     *
     * @param name
     * @param password
     * @param isAdmin
     * @return {@code true} if user was added successfully, {@code false} otherwise.
     */
    public boolean addUser(String name, String password, boolean isAdmin) {
        User user =userMap.putIfAbsent(name, new User(name, password, isAdmin));
        return user==null;
    }

    /**
     * Logs the specified {@link User} into the service.
     * fails if the user is already logged in, or if given username/password is incorrect.
     *
     * @param name
     * @param password
     * @return {@code true} if user was logged in successfully, {@code false} otherwise.
     */
    public boolean login(String name, String password) {
        User user = userMap.get(name);
        if (user != null && user.verifyPassword(password)) {
            return user.login();
        }
        return false;
    }

    /**
     * @param name
     * @return the specified {@link User}.
     */
    public User getUser(String name) {
        return userMap.get(name);
    }

    /**
     * Logs the specified {@link User} out of the service.
     *
     * @param name
     */
    public void logout(String name) {
        userMap.get(name).logout();
    }

    /**
     * Registers the specified {@link User} to the specified {@link Course}.
     *
     * @param courseNum
     * @param user
     * @return {@code true} if user was registered successfully, {@code false} otherwise.
     */
    public boolean courseReg(short courseNum, User user) {
        Course course = courseMap.get(courseNum);
        if (course != null && !user.isAdmin() && user.hasKdamCourses(course.getKdamCoursesList()) ) {
            if(course.addStudent(user.getUsername())){
                user.addCourse(course);
                return true;
            }
        }
        return false;
    }

    /**
     * @param courseNum the number of the {@link Course} for which the Kdam-course list reefers to.
     * @return {@code List<Short>} containing the Kdam-course numbers of the specified {@code Course},
     * {@code null} if the {@code Course} does not exist in the DB.
     */
    public List<Short> getKdamCourses(short courseNum) {
        Course course = courseMap.get(courseNum);
        if (course != null) {
            return course.getKdamCoursesList();
        }
        return null;
    }

    /**
     * @param courseNum
     * @return the status of the specified {@link Course},
     * {@code null} if the {@code Course} does not exist in the DB.
     */
    public String getCourseStat(short courseNum) {
        Course course = courseMap.get(courseNum);
        if (course != null)
            return course.toString();
        return null;
    }

    /**
     * @param username
     * @return the status of the specified {@link User},
     * {@code null} if the {@code User} does not exist in the DB.
     */
    public String getStudentStat(String username) {
        User user = userMap.get(username);
        if (user != null)
            return user.toString();
        return null;
    }

    /**
     * Checks if the specified {@link User} is registered to the specified {@link Course}.
     *
     * @param courseNum
     * @param user
     * @return {@code true} if {@code User} is registered to the {@code Course},
     * {@code false} if {@code User} is not registered to the {@code Course},
     * {@code null} if the {@code Course} does not exist in the DB.
     */
    public Boolean isRegistered(short courseNum, User user) {
        Course course = courseMap.get(courseNum);
        if (course != null) {
            return course.isRegistered(user.getUsername());
        }
        return null;
    }

    /**
     * Unregisters the specified {@link User} from the specified {@link Course}.
     *
     * @param courseNum
     * @param user
     * @return {@code true} if user was unregistered successfully, {@code false} otherwise.
     */
    public boolean unReg(short courseNum, User user) {
        Course course = courseMap.get(courseNum);
        if (course != null) {
            return course.unReg(user) && user.removeCourse(course);
        }
        return false;
    }


}