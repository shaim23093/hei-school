package school.hei.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.hei.admin.dto.request.TeacherCreateRequest;
import school.hei.admin.dto.request.TeacherUpdateRequest;
import school.hei.admin.entity.Teacher;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.TeacherMapper;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.TeacherRepository;
import school.hei.admin.repository.model.JTeacher;

class TeacherServiceTest {
  private final TeacherRepository teacherRepository = mock(TeacherRepository.class);
  private final AccountRepository accountRepository = mock(AccountRepository.class);
  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
  private final TeacherMapper teacherMapper = new TeacherMapper();

  private final TeacherService service =
      new TeacherService(teacherRepository, accountRepository, passwordEncoder, teacherMapper);

  private JTeacher teacher;

  @BeforeEach
  void setUp() {
    teacher =
        JTeacher.builder()
            .id(UUID.randomUUID())
            .name("Rakoto")
            .firstName("Mamy")
            .email("mamy.rakoto@hei.school")
            .build();
  }

  @Test
  void list_returns_all_teachers() {
    when(teacherRepository.findAll()).thenReturn(List.of(teacher));

    List<Teacher> result = service.list();

    assertEquals(1, result.size());
    assertEquals("Rakoto", result.get(0).name());
  }

  @Test
  void list_returns_empty_when_no_teachers() {
    when(teacherRepository.findAll()).thenReturn(List.of());

    assertEquals(List.of(), service.list());
  }

  @Test
  void getById_returns_teacher() {
    when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

    Teacher result = service.getById(teacher.getId());

    assertEquals("Rakoto", result.name());
    assertEquals("Mamy", result.firstName());
    assertEquals("mamy.rakoto@hei.school", result.email());
  }

  @Test
  void getById_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(teacherRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getById(unknown));
  }

  @Test
  void create_saves_account_with_teacher_role_and_hashes_password() {
    when(passwordEncoder.encode("secret123")).thenReturn("$2a$hashed");
    when(accountRepository.save(argThat(a -> a.getRole() == Role.TEACHER)))
        .thenAnswer(i -> i.getArgument(0));
    when(teacherRepository.save(argThat(t -> "Rakoto".equals(t.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    TeacherCreateRequest request =
        TeacherCreateRequest.builder()
            .username("teacher3")
            .password("secret123")
            .name("Rakoto")
            .firstName("Mamy")
            .email("mamy.rakoto@hei.school")
            .build();

    Teacher result = service.create(request);

    assertEquals("Rakoto", result.name());
    verify(passwordEncoder).encode("secret123");
    verify(accountRepository)
        .save(argThat(a -> a.getRole() == Role.TEACHER && "$2a$hashed".equals(a.getPassword())));
  }

  @Test
  void update_modifies_only_provided_fields() {
    when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
    when(teacherRepository.save(argThat(t -> "NewName".equals(t.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    TeacherUpdateRequest request = TeacherUpdateRequest.builder().name("NewName").build();

    Teacher result = service.update(teacher.getId(), request);

    assertEquals("NewName", result.name());
    assertEquals("Mamy", result.firstName());
    assertEquals("mamy.rakoto@hei.school", result.email());
  }

  @Test
  void update_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(teacherRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> service.update(unknown, TeacherUpdateRequest.builder().name("X").build()));
  }

  @Test
  void delete_removes_teacher() {
    when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

    service.delete(teacher.getId());

    verify(teacherRepository).delete(teacher);
  }

  @Test
  void delete_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(teacherRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.delete(unknown));
    verify(teacherRepository, never()).delete(argThat(t -> true));
  }
}
