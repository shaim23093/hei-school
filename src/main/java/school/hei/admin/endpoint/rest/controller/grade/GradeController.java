package school.hei.admin.endpoint.rest.controller.grade;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.hei.admin.dto.request.GradeUpdateRequest;
import school.hei.admin.dto.response.GradeHistoryResponse;
import school.hei.admin.dto.response.GradeResponse;
import school.hei.admin.service.GradeService;

@RestController
@AllArgsConstructor
public class GradeController {
  private final GradeService gradeService;

  @PutMapping("/grades/{id}")
  public GradeResponse updateGrade(
      @PathVariable("id") UUID gradeId, @RequestBody GradeUpdateRequest request) {
    return gradeService.updateGrade(gradeId, request);
  }

  @GetMapping("/grades/{id}/history")
  public List<GradeHistoryResponse> getHistory(@PathVariable("id") UUID gradeId) {
    return gradeService.getHistory(gradeId);
  }
}
