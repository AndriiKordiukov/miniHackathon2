package model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@NamedQuery(name="updateDeptById", query = "UPDATE Department set deptName = :newDeptName where deptId = :deptId")
@NamedQuery(name="updateDeptByName", query = "UPDATE Department set deptName = :newDeptName where deptName = :deptName")

@NamedQuery(name="selectDeptNames", query = "SELECT d.deptName FROM Department d")

@NamedQuery(name = "Department.findAll", query = "SELECT d FROM Department d")
@NamedQuery(name="selectDeptById", query = "FROM Department d WHERE d.deptId = :deptId")

@NamedQuery(name="selectDeptNameById", query = "SELECT deptName FROM Department d WHERE d.deptId = :deptId")
@NamedQuery(name="selectDeptNameByName", query = "SELECT deptName FROM Department d WHERE d.deptName = :departmentName")

public class Department implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deptId;
    private String deptName;
//        @OneToMany(targetEntity= Teacher.class, cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "department")
    private List<Teacher> teacherList = new ArrayList<>();




public Department(int deptId, String deptName) {
        super();
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public List<Teacher> getTeacherList() {
        return teacherList;
    }

    public void setTeacherList(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }


    public Department() {
    }

    public Department(String deptName) {
        this.deptName = deptName;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

//    @Override
//    public String toString() {
//        return "Department{" + "deptId=" + deptId + ", deptName='" + deptName + '\'' + ", teacherList=" + teacherList + '}';
//    }
}
