package school.hei.admin.endpoint.rest.controller.teacher;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.hei.admin.dto.request.TeacherCreateRequest;
import school.hei.admin.dto.request.TeacherUpdateRequest;
import school.hei.admin.entity.Teacher;
import school.hei.admin.service.TeacherService;

@RestController
@AllArgsConstructor
public class TeacherController {
  private final TeacherService teacherService;

  @GetMapping("/teachers")
  public List<Teacher> list() {
    return teacherService.list();
  }

  @GetMapping("/teachers/{id}")
  public Teacher getById(@PathVariable("id") UUID id) {
    return teacherService.getById(id);
  }

  @PostMapping("/teachers")
  public Teacher create(@Valid @RequestBody TeacherCreateRequest request) {
    return teacherService.create(request);
  }

  @PutMapping("/teachers/{id}")
  public Teacher update(
      @PathVariable("id") UUID id, @Valid @RequestBody TeacherUpdateRequest request) {
    return teacherService.update(id, request);
  }

  @DeleteMapping("/teachers/{id}")
  public void delete(@PathVariable("id") UUID id) {
    teacherService.delete(id);
  }
}
