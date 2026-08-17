package school.hei.admin.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.hei.admin.dto.request.TeacherCreateRequest;
import school.hei.admin.dto.request.TeacherUpdateRequest;
import school.hei.admin.entity.Teacher;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.TeacherMapper;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.TeacherRepository;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JTeacher;

@Service
@AllArgsConstructor
public class TeacherService {
  private final TeacherRepository teacherRepository;
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final TeacherMapper teacherMapper;

  public List<Teacher> list() {
    return teacherMapper.toModel(teacherRepository.findAll());
  }

  public Teacher getById(UUID id) {
    JTeacher entity =
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + id));
    return teacherMapper.toModel(entity);
  }

  public Teacher create(TeacherCreateRequest request) {
    JAccount account =
        JAccount.builder()
            .id(UUID.randomUUID())
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .role(Role.TEACHER)
            .build();
    accountRepository.save(account);

    JTeacher entity =
        JTeacher.builder()
            .id(UUID.randomUUID())
            .account(account)
            .name(request.name())
            .firstName(request.firstName())
            .email(request.email())
            .build();
    return teacherMapper.toModel(teacherRepository.save(entity));
  }

  public Teacher update(UUID id, TeacherUpdateRequest request) {
    JTeacher entity =
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + id));

    if (request.name() != null) {
      entity.setName(request.name());
    }
    if (request.firstName() != null) {
      entity.setFirstName(request.firstName());
    }
    if (request.email() != null) {
      entity.setEmail(request.email());
    }

    return teacherMapper.toModel(teacherRepository.save(entity));
  }

  public void delete(UUID id) {
    JTeacher entity =
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Teacher not found: " + id));
    teacherRepository.delete(entity);
  }
}
