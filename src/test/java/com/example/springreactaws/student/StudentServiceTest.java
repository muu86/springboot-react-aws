package com.example.springreactaws.student;

import com.example.springreactaws.student.exception.BadRequestException;
import com.example.springreactaws.student.exception.StudentNotFoundException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();
        // then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student capturedStudent = studentArgumentCaptor.getValue();
        AssertionsForClassTypes.assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );
        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        // when
        // then
        Assertions.assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void shouldDeleteStudent() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);

        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);

        // when
        // then
        Assertions.assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");
    }
}