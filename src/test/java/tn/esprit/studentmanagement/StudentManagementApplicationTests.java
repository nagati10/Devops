package tn.esprit.studentmanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IStudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student1;
    private Student student2;
    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        student1 = new Student(1L, "John", "Doe", "john.doe@email.com");
        student2 = new Student(2L, "Jane", "Smith", "jane.smith@email.com");
        studentList = Arrays.asList(student1, student2);
    }

    @Test
    void testGetAllStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(studentList);

        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(studentList.size()))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void testGetStudentById_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(student1);

        mockMvc.perform(get("/students/getStudentById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetStudentById_NotFound() throws Exception {
        when(studentService.getStudentById(999L))
                .thenThrow(new NoSuchElementException("Student not found"));

        mockMvc.perform(get("/students/getStudentById/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateStudent_Success() throws Exception {
        Student newStudent = new Student(null, "New", "Student", "new.student@email.com");
        Student savedStudent = new Student(3L, "New", "Student", "new.student@email.com");
        
        when(studentService.saveStudent(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/students/createStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    void testUpdateStudent_Success() throws Exception {
        Student updatedStudent = new Student(1L, "John", "Updated", "john.updated@email.com");
        
        when(studentService.saveStudent(any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/students/updateStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }

    @Test
    void testDeleteStudent_Success() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/students/deleteStudent/1"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    void testDeleteStudent_NotFound() throws Exception {
        doThrow(new NoSuchElementException("Student not found"))
                .when(studentService).deleteStudent(999L);

        mockMvc.perform(delete("/students/deleteStudent/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStudentsByDepartment() throws Exception {
        when(studentService.getStudentsByDepartment(anyLong())).thenReturn(studentList);

        mockMvc.perform(get("/students/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}