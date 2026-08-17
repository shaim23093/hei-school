package school.hei.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import school.hei.admin.dto.request.StudentCreateRequest;
import school.hei.admin.dto.request.StudentUpdateRequest;
import school.hei.admin.entity.Student;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.StudentMapper;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.PromotionRepository;
import school.hei.admin.repository.StudentRepository;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;

class StudentServiceTest {
  private final StudentRepository studentRepository = mock(StudentRepository.class);
  private final AccountRepository accountRepository = mock(AccountRepository.class);
  private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
  private final StudentMapper studentMapper = new StudentMapper();

  private final StudentService service =
      new StudentService(
          studentRepository,
          accountRepository,
          promotionRepository,
          passwordEncoder,
          studentMapper);

  private JPromotion promotion;
  private JStudent student;
  private Student studentModel;

  @BeforeEach
  void setUp() {
    promotion =
        JPromotion.builder().id(UUID.randomUUID()).name("Promo 2024").entryYear(2024).build();
    student =
        JStudent.builder()
            .id(UUID.randomUUID())
            .std("STD24001")
            .name("Andria")
            .firstName("Tiana")
            .email("tiana.andria@hei.school")
            .promotion(promotion)
            .build();
    studentModel = studentMapper.toModel(student);
  }

  @Test
  void list_returns_all_students() {
    when(studentRepository.findAll()).thenReturn(List.of(student));

    List<Student> result = service.list();

    assertEquals(1, result.size());
    assertEquals("STD24001", result.get(0).std());
  }

  @Test
  void list_returns_empty_when_no_students() {
    when(studentRepository.findAll()).thenReturn(List.of());

    assertEquals(List.of(), service.list());
  }

  @Test
  void getById_returns_student() {
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

    Student result = service.getById(student.getId());

    assertEquals("STD24001", result.std());
    assertEquals("Andria", result.name());
    assertEquals("Tiana", result.firstName());
  }

  @Test
  void getById_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(studentRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getById(unknown));
  }

  @Test
  void create_saves_account_with_student_role_and_hashes_password() {
    when(passwordEncoder.encode("secret123")).thenReturn("$2a$hashed");
    when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));
    when(accountRepository.save(argThat(a -> a.getRole() == Role.STUDENT)))
        .thenAnswer(i -> i.getArgument(0));
    when(studentRepository.save(argThat(s -> "STD24003".equals(s.getStd()))))
        .thenAnswer(i -> i.getArgument(0));

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("student3")
            .password("secret123")
            .promotionId(promotion.getId())
            .std("STD24003")
            .name("Rabe")
            .firstName("Hery")
            .email("hery.rabe@hei.school")
            .build();

    Student result = service.create(request);

    assertEquals("STD24003", result.std());
    verify(passwordEncoder).encode("secret123");
    verify(accountRepository)
        .save(argThat(a -> a.getRole() == Role.STUDENT && "$2a$hashed".equals(a.getPassword())));
  }

  @Test
  void create_links_promotion() {
    when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));
    when(passwordEncoder.encode("secret123")).thenReturn("$2a$hashed");
    when(accountRepository.save(argThat(a -> a.getRole() == Role.STUDENT)))
        .thenAnswer(i -> i.getArgument(0));
    when(studentRepository.save(argThat(s -> s.getPromotion() != null)))
        .thenAnswer(i -> i.getArgument(0));

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("student3")
            .password("secret123")
            .promotionId(promotion.getId())
            .std("STD24003")
            .name("Rabe")
            .firstName("Hery")
            .email("hery.rabe@hei.school")
            .build();

    Student result = service.create(request);

    assertEquals(promotion.getId(), result.promotionId());
    verify(promotionRepository).findById(promotion.getId());
  }

  @Test
  void create_throws_not_found_for_invalid_promotion() {
    UUID unknownPromo = UUID.randomUUID();
    when(passwordEncoder.encode("secret123")).thenReturn("$2a$hashed");
    when(accountRepository.save(argThat(a -> a.getRole() == Role.STUDENT)))
        .thenAnswer(i -> i.getArgument(0));
    when(promotionRepository.findById(unknownPromo)).thenReturn(Optional.empty());

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("student3")
            .password("secret123")
            .promotionId(unknownPromo)
            .std("STD24003")
            .name("Rabe")
            .firstName("Hery")
            .email("hery.rabe@hei.school")
            .build();

    assertThrows(NotFoundException.class, () -> service.create(request));
  }

  @Test
  void create_works_without_promotion() {
    when(passwordEncoder.encode("secret123")).thenReturn("$2a$hashed");
    when(accountRepository.save(argThat(a -> a.getRole() == Role.STUDENT)))
        .thenAnswer(i -> i.getArgument(0));
    when(studentRepository.save(argThat(s -> s.getPromotion() == null)))
        .thenAnswer(i -> i.getArgument(0));

    StudentCreateRequest request =
        StudentCreateRequest.builder()
            .username("student3")
            .password("secret123")
            .std("STD24003")
            .name("Rabe")
            .firstName("Hery")
            .email("hery.rabe@hei.school")
            .build();

    Student result = service.create(request);

    assertEquals("STD24003", result.std());
    assertNull(result.promotionId());
  }

  @Test
  void update_modifies_only_provided_fields() {
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(studentRepository.save(argThat(s -> "NewName".equals(s.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    StudentUpdateRequest request = StudentUpdateRequest.builder().name("NewName").build();

    Student result = service.update(student.getId(), request);

    assertEquals("NewName", result.name());
    assertEquals("Tiana", result.firstName());
    assertEquals("STD24001", result.std());
  }

  @Test
  void update_replaces_promotion() {
    JPromotion newPromo =
        JPromotion.builder().id(UUID.randomUUID()).name("Promo 2025").entryYear(2025).build();
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
    when(promotionRepository.findById(newPromo.getId())).thenReturn(Optional.of(newPromo));
    when(studentRepository.save(argThat(s -> newPromo.getId().equals(s.getPromotion().getId()))))
        .thenAnswer(i -> i.getArgument(0));

    StudentUpdateRequest request =
        StudentUpdateRequest.builder().promotionId(newPromo.getId()).build();

    Student result = service.update(student.getId(), request);

    assertEquals(newPromo.getId(), result.promotionId());
  }

  @Test
  void update_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(studentRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> service.update(unknown, StudentUpdateRequest.builder().name("X").build()));
  }

  @Test
  void delete_removes_student() {
    when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));

    service.delete(student.getId());

    verify(studentRepository).delete(student);
  }

  @Test
  void delete_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(studentRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.delete(unknown));
    verify(studentRepository, never()).delete(argThat(s -> true));
  }
}
