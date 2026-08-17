package school.hei.admin.endpoint.rest.controller.student;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.hei.admin.dto.response.StudentGradesResponse;
import school.hei.admin.dto.response.TranscriptResponse;
import school.hei.admin.service.GradeService;

@RestController
@AllArgsConstructor
public class StudentController {
  private final GradeService gradeService;

  @GetMapping("/students/{id}/grades")
  public StudentGradesResponse getGrades(
      @PathVariable("id") UUID studentId,
      @RequestParam(value = "semester", required = false) Integer semester) {
    return gradeService.getGrades(studentId, semester);
  }

  @GetMapping("/students/{id}/transcript")
  public TranscriptResponse getTranscript(
      @PathVariable("id") UUID studentId,
      @RequestParam(value = "semester", required = false) Integer semester) {
    return gradeService.getTranscript(studentId, semester);
  }
}
