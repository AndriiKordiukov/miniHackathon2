import jakarta.persistence.NoResultException;
import model.Department;
import model.Teacher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class manyToOneInteractive {
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();

        System.out.println("=====================================");
        System.out.println("   Welcome to ManyToOneInteractive!  ");
        System.out.println("=====================================");
        try {
            manyToOneInteractive(factory, session);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }

    public static void manyToOneInteractive(SessionFactory factory, Session session) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n0. Exit");
                System.out.println("1. Manage Departments");
                System.out.println("2. Manage Teachers");
                System.out.println("3. Assign Teacher to Department");
                System.out.println("4. List Teachers");
                System.out.println("5. List Department\n");
                System.out.print("Choose an option:\n\n");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 0:
                        System.out.println("Exiting...");
                        return;
                    case 1:
                        manageDepartments(scanner, factory);
                        break;
                    case 2:
                        manageTeachers(scanner, factory);
                        break;
                    case 3:
                        assignTeacherToDepartment(scanner, session);
                        break;
                    case 4:
                        listTeachers(session);
                        break;
                    case 5:
                        listDepts(session);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
            factory.close();
        }
    }

    private static void manageDepartments(Scanner scanner, SessionFactory factory) {


        System.out.println("\n1. Add Departments");
        System.out.println("2. Delete Department");
        System.out.println("3. Modify Department");
        System.out.println("4. Go back to menu");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        switch (choice) {
            case 1: createDepartment(scanner, factory); return;
            case 2: deleteDepartment(scanner, factory); return;
            case 3: updateDepartment(scanner, factory); return;
            case 4: return;
            default: System.out.println("Invalid option. Please try again.");
        }


    }

    private static void manageTeachers(Scanner scanner, SessionFactory factory) {

        // YOUR CODE HERE

        System.out.println("\n1. Add Teachers");
        System.out.println("2. Delete Teacher");
        System.out.println("3. Modify Teacher");
        System.out.println("4. Go back to menu");

        switch (scanner.nextInt()) {
            case 1: createTeacher(scanner, factory); return;
            case 2: deleteTeacher(scanner, factory); return;
            case 3: updateTeacher(scanner, factory); return;
            case 4: return;
            default: System.out.println("Invalid option. Please try again.");
        }

    }

    private static void assignTeacherToDepartment(Scanner scanner, Session session) {
        int state = 0;
        while(state != -1) {
            Transaction t = session.beginTransaction();
            System.out.println("\nWhich Teacher would you like to modify?");
            System.out.println("Enter Teacher ID: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            try {
                Query<Teacher> query1 = session.createNamedQuery("selectTeacherById", Teacher.class);
                Query<Department> query2 = session.createNamedQuery("selectDeptById", Department.class);
                query1.setParameter("id", choice);

                Teacher teacher = query1.getSingleResult();
                state = -1;
                System.out.println("Teacher " + teacher.getTeacherName() + " is selected\n");

                System.out.println("Which department would you like to assign to Teacher?");
                System.out.println("Enter Department ID: ");
                choice = scanner.nextInt();
                scanner.nextLine();
                query2.setParameter("deptId", choice);
                Department dept = query2.getSingleResult();
                teacher.setDepartment(dept);
                t.commit();

            } catch (NoResultException e) {
                System.out.println("Invalid ID. Please try again.");
                t.rollback();
                //session.close();
            }

        }
    }

    private static void listDepts(Session session) {

        Transaction t = session.beginTransaction();
        //List<Department> departments = session.createQuery("FROM Department", Department.class)
//        Query<Department> query = session.createNamedQuery("selectDeptNames", Department.class);

        List<Department> departments = session.createNamedQuery("Department.findAll", Department.class)
                .getResultList();
        System.out.printf("\n%5s %s%n", "Id", " Department");
        System.out.println("===========================================");
        for(Department department : departments) {
            System.out.printf("%5s. %s%n", department.getDeptId(), department.getDeptName());
        }

        t.commit();
    }

    private static void listTeachers(Session session) {
        Transaction t = session.beginTransaction();
        List<Teacher> teachers = session.createNamedQuery("Teacher.findAll", Teacher.class)
                .getResultList();

        System.out.printf("\n%5s %10s %10s%n", "Id", " Teacher Name", " Department");
        System.out.println("===========================================");

        for(Teacher teacher : teachers) {
            Query<String> query = session.createQuery(""+
                    "SELECT d.deptName " +
                    "FROM Teacher t " +
                    "JOIN t.department d " +
                    "WHERE t.teacherId = :id",
                    String.class);
            String departmentName;
            query.setParameter("id", teacher.getTeacherId());
            try{
                departmentName = query.getSingleResult();
            } catch (jakarta.persistence.NoResultException e) {
                departmentName = "-";
            }

            System.out.printf("%5s. %10s %13s%n", teacher.getTeacherId(), teacher.getTeacherName(), departmentName);
        }

        t.commit();
    }

    //======================================================================================
    //================================= Department =========================================
    //======================================================================================

    private static void createDepartment(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        String hql1 = "SELECT deptName FROM Department d WHERE d.deptName = :departmentName";

        System.out.println("How many Departments would you like to create: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        for(int i = 0; i < choice; i++) {
            System.out.println("Enter Department Name: ");
            String name = scanner.nextLine();
            //scanner.nextLine(); // Consume newline character
            try {
                Department dept = session.createQuery(hql1, Department.class).setParameter("departmentName", name).getSingleResult();
                System.out.println("Department " + name + " is already exists");
            }
            catch (jakarta.persistence.NoResultException e) {
                // handle the case when no result is found
                Department dept = new Department(name);
                session.persist(dept);
                System.out.println("Department " + name + " is created");
            }
        }
        t.commit();
    }

    private static void deleteDepartment(Scanner scanner, SessionFactory factory) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        System.out.println("Input Department ID to delete: ");
        int departmentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        try {
            Department department = session.get(Department.class, departmentId);
            System.out.println(department.getDeptName() + " Department is detected by id = " + departmentId);
            session.remove(department);
            session.flush();
            t.commit();
            System.out.println("Department " + departmentId + " is deleted");
        } catch (jakarta.persistence.NoResultException e) {
            System.out.println("Department " + departmentId + " does not exist");
        }
    }

    private static void updateDepartment(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        int choice;
        do {
            System.out.println("How do you want to choose which department to update: ");
            System.out.println("\n1. By Id");
            System.out.println("2. By Department name");
            System.out.println("3. Go back to menu");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character
            switch (choice) {
                case 1: updateDepartmentById(scanner, factory); return;
                case 2: updateDepartmentByName(scanner, factory); return;
                case 3: System.out.println("Exiting...");
                        manyToOneInteractive(factory, session);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);

    }

    private static void updateDepartmentById(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        String hql1 = "FROM Department d WHERE d.deptId = :deptId";


        System.out.println("Input Department ID to update it: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        try {
            Department dept = session.createQuery(hql1, Department.class).setParameter("deptId", choice).getSingleResult();
            System.out.println("Found Department " + dept.getDeptName() + " by id = " + choice);
            System.out.println("\nInput new Department name:\n");
            String name = scanner.nextLine();

            //Query<Department> query = session.createNamedQuery("updateDeptById", Department.class);
            //"SELECT deptName FROM Department d WHERE d.deptId = :deptId"
//            Query query = session.createQuery("UPDATE Department SET deptName = :newDeptName WHERE deptId = :deptId");
            Query query = session.createNamedQuery("updateDeptById");
            query.setParameter("newDeptName", name);
            query.setParameter("deptId", choice);
            int rowsAffected = query.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n"+ rowsAffected + "(s) were inserted");
            }
            t.commit();
            System.out.println("successfully saved");

            System.out.println("New Department name - " + dept.getDeptName() + " by id = " + dept.getDeptId());
        } catch (jakarta.persistence.NoResultException e) {
            // handle the case when no result is found
            System.out.println("Department " + choice + " does not exist.\nDo you want to create it? (Yes/No)\n");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("yes")) {
                createDepartment(scanner, factory);
            } else if (answer.equalsIgnoreCase("no")) {
                manyToOneInteractive(factory, session);
            } else System.out.println("Invalid option. Exiting...");
        }
    }

    private static void updateDepartmentByName(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        String hql1 = "FROM Department d WHERE d.deptName = :deptName";

        System.out.println("Input Department Name to update it: ");
        String choice = scanner.nextLine();

        try {
            Department dept = session.createQuery(hql1, Department.class).setParameter("deptName", choice).getSingleResult();
            System.out.println("Found Department " + dept.getDeptName() + " by id = " + dept.getDeptId());
            System.out.println("Input new Department name:");
            String name = scanner.nextLine();

//            Query query = session.createQuery("UPDATE Department set deptName = :newDeptName where deptName = :deptName");
            Query query = session.createNamedQuery("updateDeptByName");
            query.setParameter("newDeptName", name);
            query.setParameter("deptName", choice);
            session.persist(dept);
            int rowsAffected = query.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n"+ rowsAffected + "(s) were inserted");
            }
            t.commit();
            System.out.println("successfully saved");

            System.out.println("New Department name - " + dept.getDeptName() + " by id = " + dept.getDeptId());
        } catch (jakarta.persistence.NoResultException e) {
            // handle the case when no result is found
            System.out.println("Department " + choice + " does not exist.\nDo you want to create it? (Yes/No)\n");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("yes")) {
                createDepartment(scanner, factory);
            } else if (answer.equalsIgnoreCase("no")) {
                manyToOneInteractive(factory, session);
            } else System.out.println("Invalid option. Exiting...");
        }
    }

    //======================================================================================
    //================================== TEACHER ===========================================
    //======================================================================================

    public static void createTeacher(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        String hql1 = "SELECT teacherName FROM Teacher t WHERE t.teacherName = :teacherName";

        System.out.println("How many Teachers would you like to create: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        for(int i = 0; i < choice; i++) {
            System.out.println("Enter Teacher Name: ");
            String name = scanner.nextLine();
            //scanner.nextLine(); // Consume newline character
            try {
                Teacher newTeacher = session
                        .createQuery(hql1, Teacher.class)
                        .setParameter("teacherName", name)
                        .getSingleResult();
                System.out.println("Teacher " + name + " is already exists");
            }
            catch (jakarta.persistence.NoResultException e) {
                // handle the case when no result is found
                Teacher newTeacher = new Teacher(name);
                session.persist(newTeacher);
                System.out.println("Teacher " + name + " is created");
            }
        }
        t.commit();
    }

    public static void deleteTeacher(Scanner scanner, SessionFactory factory) {
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        System.out.println("Input Teacher ID to delete: ");
        int teacherId = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        try {
            Teacher teacher = session.get(Teacher.class, teacherId);
            System.out.println(teacher.getTeacherName() + " Teacher is detected by id = " + teacherId);
            session.remove(teacher);
            session.flush();
            t.commit();
            System.out.println("Teacher " + teacherId + " is deleted");
        } catch (jakarta.persistence.NoResultException e) {
            System.out.println("Teacher " + teacherId + " does not exist");
        }

    }

    public static void updateTeacher(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();
        int choice;
        do {
            System.out.println("How do you want to choose Teacher to update: \n");
            System.out.println("1. By Id");
            System.out.println("2. By Name");
            System.out.println("3. Go back to Menu");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character
            switch (choice) {
                case 1: updateTeacherById(scanner, factory); return;
                case 2: updateTeacherByName(scanner, factory); return;
                case 3: System.out.println("Exiting...");
                    manyToOneInteractive(factory, session);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);

    }

    private static void updateTeacherById(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        String hql1 = "FROM Teacher WHERE teacherId = :teacherId";


        System.out.println("Input Teacher ID to update it: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character

        try {
            Teacher teacher = session.createQuery(hql1, Teacher.class).setParameter("teacherId", choice).getSingleResult();
            System.out.println("Found Teacher " + teacher.getTeacherName() + " by id = " + choice);
            System.out.println("\nInput new Teacher name:\n");
            String name = scanner.nextLine();

            //Query query = session.createQuery("UPDATE Department SET deptName = :newDeptName WHERE deptId = :deptId");
            //Query<Teacher> query = (Query) session.createMutationQuery("UPDATE Teacher SET teacherName = :newTeacherName WHERE teacherId = :id");
//            Query query = session.createNamedQuery("updateTeacherById");
//            query.setParameter("newTeacherName", name);
//            query.setParameter("id", choice);
//            int rowsAffected = query.executeUpdate();
//            if (rowsAffected > 0) {
//                System.out.println("\n"+ rowsAffected + " row was updated");
//            }
            teacher.setTeacherName(name);
            System.out.println(teacher.getTeacherName() + " was updated");
            session.merge(teacher);
            t.commit();
            System.out.println("successfully saved\n");

            System.out.println("New Teacher name - " + teacher.getTeacherName() + " by id = " + teacher.getTeacherId());
        } catch (jakarta.persistence.NoResultException e) {
            // handle the case when no result is found
            System.out.println("Teacher " + choice + " does not exist.\nDo you want to create it? (Yes/No)\n");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("yes")) {
                createTeacher(scanner, factory);
            } else if (answer.equalsIgnoreCase("no")) {
                manyToOneInteractive(factory, session);
            } else System.out.println("Invalid option. Exiting...");
        }

    }

    private static void updateTeacherByName(Scanner scanner, SessionFactory factory) {

        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        String hql1 = "FROM Teacher WHERE teacherName = :teacherName";

        System.out.println("Input Teacher Name to update it: ");
        String choice = scanner.nextLine();

        try {
            Teacher teacher = session.createQuery(hql1, Teacher.class).setParameter("teacherName", choice).getSingleResult();
            System.out.println("Found Teacher " + teacher.getTeacherName() + " by id = " + choice);
            System.out.println("\nInput new Teacher name:\n");

            String name = scanner.nextLine();
//
//            Query query = session.createNamedQuery("updateTeacherByName");
//            query.setParameter("newTeacherName", name);
//            query.setParameter("teacherName", choice);
//            int rowsAffected = query.executeUpdate();
//            if (rowsAffected > 0) {
//                System.out.println("\n"+ rowsAffected + "(s) were inserted");
//            }
            teacher.setTeacherName(name);
            System.out.println(teacher.getTeacherName() + " was updated");
            session.merge(teacher);
            t.commit();

            System.out.println("successfully saved");

            System.out.println("New Teacher name - " + teacher.getTeacherName() + " by id = " + teacher.getTeacherId());
        } catch (jakarta.persistence.NoResultException e) {
            // handle the case when no result is found
            System.out.println("Teacher " + choice + " does not exist.\nDo you want to create it? (Yes/No)\n");
            String answer = scanner.nextLine();
            if (answer.equalsIgnoreCase("yes")) {
                createDepartment(scanner, factory);
            } else if (answer.equalsIgnoreCase("no")) {
                manyToOneInteractive(factory, session);
            } else System.out.println("Invalid option. Exiting...");
        }

    }

}
