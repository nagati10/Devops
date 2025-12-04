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
public class StudentControllerTest {

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
    public void setUp() {
        // Create sample students
        student1 = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .build();
        
        student2 = Student.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@email.com")
                .build();
        
        studentList = Arrays.asList(student1, student2);
    }

    @Test
    public void testGetAllStudents_Success() throws Exception {
        // Arrange
        when(studentService.getAllStudents()).thenReturn(studentList);

        // Act & Assert
        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
        
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    public void testGetStudentById_Success() throws Exception {
        // Arrange
        when(studentService.getStudentById(1L)).thenReturn(student1);

        // Act & Assert
        mockMvc.perform(get("/students/getStudentById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@email.com"));
        
        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    public void testGetStudentById_NotFound() throws Exception {
        // Arrange
        when(studentService.getStudentById(999L))
                .thenThrow(new NoSuchElementException("Student not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/students/getStudentById/999"))
                .andExpect(status().isNotFound());
        
        verify(studentService, times(1)).getStudentById(999L);
    }

    @Test
    public void testCreateStudent_Success() throws Exception {
        // Arrange
        Student newStudent = Student.builder()
                .firstName("New")
                .lastName("Student")
                .email("new.student@email.com")
                .build();
        
        Student savedStudent = Student.builder()
                .id(3L)
                .firstName("New")
                .lastName("Student")
                .email("new.student@email.com")
                .build();
        
        when(studentService.saveStudent(any(Student.class))).thenReturn(savedStudent);

        // Act & Assert
        mockMvc.perform(post("/students/createStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Student"));
        
        verify(studentService, times(1)).saveStudent(any(Student.class));
    }

    @Test
    public void testUpdateStudent_Success() throws Exception {
        // Arrange
        Student updatedStudent = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Updated")
                .email("john.updated@email.com")
                .build();
        
        when(studentService.saveStudent(any(Student.class))).thenReturn(updatedStudent);

        // Act & Assert
        mockMvc.perform(put("/students/updateStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lastName").value("Updated"));
        
        verify(studentService, times(1)).saveStudent(any(Student.class));
    }

    @Test
    public void testDeleteStudent_Success() throws Exception {
        // Arrange
        doNothing().when(studentService).deleteStudent(1L);

        // Act & Assert
        mockMvc.perform(delete("/students/deleteStudent/1"))
                .andExpect(status().isOk());
        
        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    public void testDeleteStudent_NotFound() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Student not found"))
                .when(studentService).deleteStudent(999L);

        // Act & Assert
        mockMvc.perform(delete("/students/deleteStudent/999"))
                .andExpect(status().isNotFound());
        
        verify(studentService, times(1)).deleteStudent(999L);
    }

    @Test
    public void testGetStudentsByDepartmentId_Success() throws Exception {
        // Arrange
        when(studentService.getStudentsByDepartment(1L)).thenReturn(studentList);

        // Act & Assert
        mockMvc.perform(get("/students/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        
        verify(studentService, times(1)).getStudentsByDepartment(1L);
    }

    @Test
    public void testGetStudentsByDepartmentId_Empty() throws Exception {
        // Arrange
        when(studentService.getStudentsByDepartment(999L)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/students/department/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        
        verify(studentService, times(1)).getStudentsByDepartment(999L);
    }

    @Test
    public void testCreateStudent_InvalidInput() throws Exception {
        // Arrange - Student without required fields
        Student invalidStudent = Student.builder()
                .firstName("")  // Empty first name
                .lastName("")   // Empty last name
                .build();

        // Act & Assert
        mockMvc.perform(post("/students/createStudent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllStudents_EmptyList() throws Exception {
        // Arrange
        when(studentService.getAllStudents()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        
        verify(studentService, times(1)).getAllStudents();
    }
}