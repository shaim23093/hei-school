package school.hei.admin.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.hei.admin.dto.request.StudentCreateRequest;
import school.hei.admin.dto.request.StudentUpdateRequest;
import school.hei.admin.entity.Student;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.StudentMapper;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.PromotionRepository;
import school.hei.admin.repository.StudentRepository;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;

@Service
@AllArgsConstructor
public class StudentService {
  private final StudentRepository studentRepository;
  private final AccountRepository accountRepository;
  private final PromotionRepository promotionRepository;
  private final PasswordEncoder passwordEncoder;
  private final StudentMapper studentMapper;

  public List<Student> list() {
    return studentMapper.toModel(studentRepository.findAll());
  }

  public Student getById(UUID id) {
    JStudent entity =
        studentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found: " + id));
    return studentMapper.toModel(entity);
  }

  public Student create(StudentCreateRequest request) {
    JAccount account =
        JAccount.builder()
            .id(UUID.randomUUID())
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.STUDENT)
            .build();
    accountRepository.save(account);

    JPromotion promotion = null;
    if (request.promotionId() != null) {
      promotion =
          promotionRepository
              .findById(request.promotionId())
              .orElseThrow(
                  () -> new NotFoundException("Promotion not found: " + request.promotionId()));
    }

    JStudent entity =
        JStudent.builder()
            .id(UUID.randomUUID())
            .account(account)
            .promotion(promotion)
            .std(request.std())
            .name(request.name())
            .firstName(request.firstName())
            .email(request.email())
            .build();
    return studentMapper.toModel(studentRepository.save(entity));
  }

  public Student update(UUID id, StudentUpdateRequest request) {
    JStudent entity =
        studentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found: " + id));

    if (request.name() != null) {
      entity.setName(request.name());
    }
    if (request.firstName() != null) {
      entity.setFirstName(request.firstName());
    }
    if (request.email() != null) {
      entity.setEmail(request.email());
    }
    if (request.std() != null) {
      entity.setStd(request.std());
    }
    if (request.promotionId() != null) {
      JPromotion promotion =
          promotionRepository
              .findById(request.promotionId())
              .orElseThrow(
                  () -> new NotFoundException("Promotion not found: " + request.promotionId()));
      entity.setPromotion(promotion);
    }

    return studentMapper.toModel(studentRepository.save(entity));
  }

  public void delete(UUID id) {
    JStudent entity =
        studentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found: " + id));
    studentRepository.delete(entity);
  }
}
