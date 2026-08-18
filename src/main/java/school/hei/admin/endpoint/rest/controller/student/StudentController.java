package school.hei.admin.endpoint.rest.controller.student;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.hei.admin.dto.request.StudentCreateRequest;
import school.hei.admin.dto.request.StudentUpdateRequest;
import school.hei.admin.dto.response.AcademicYearResult;
import school.hei.admin.dto.response.StudentGradesResponse;
import school.hei.admin.dto.response.TranscriptResponse;
import school.hei.admin.entity.Student;
import school.hei.admin.service.GradeService;
import school.hei.admin.service.StudentService;

@RestController
@AllArgsConstructor
public class StudentController {
  private final StudentService studentService;
  private final GradeService gradeService;

  @GetMapping("/students")
  public List<Student> list() {
    return studentService.list();
  }

  @GetMapping("/students/{id}")
  public Student getById(@PathVariable("id") UUID id) {
    return studentService.getById(id);
  }

  @PostMapping("/students")
  public Student create(@Valid @RequestBody StudentCreateRequest request) {
    return studentService.create(request);
  }

  @PutMapping("/students/{id}")
  public Student update(
      @PathVariable("id") UUID id, @Valid @RequestBody StudentUpdateRequest request) {
    return studentService.update(id, request);
  }

  @DeleteMapping("/students/{id}")
  public void delete(@PathVariable("id") UUID id) {
    studentService.delete(id);
  }

  @GetMapping("/students/{id}/grades")
  public StudentGradesResponse getGrades(
      @PathVariable("id") UUID id,
      @RequestParam(value = "semester", required = false) Integer semester) {
    return gradeService.getGrades(id, semester);
  }

  @GetMapping("/students/{id}/transcript")
  public TranscriptResponse getTranscript(
      @PathVariable("id") UUID id,
      @RequestParam(value = "semester", required = false) Integer semester) {
    return gradeService.getTranscript(id, semester);
  }

  @GetMapping("/students/{id}/academic-year")
  public AcademicYearResult getAcademicYearResults(
      @PathVariable("id") UUID id, @RequestParam("year") int year) {
    return gradeService.getAcademicYearResults(id, year);
  }
}
