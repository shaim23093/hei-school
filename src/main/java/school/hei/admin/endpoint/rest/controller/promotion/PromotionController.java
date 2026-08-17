package school.hei.admin.endpoint.rest.controller.promotion;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.hei.admin.dto.response.GraduatedStudentResponse;
import school.hei.admin.dto.response.PromotionResultsResponse;
import school.hei.admin.service.GraduationService;

@RestController
@AllArgsConstructor
public class PromotionController {
  private final GraduationService graduationService;

  @GetMapping("/promotions/{id}/diplomes")
  public List<GraduatedStudentResponse> getDiplomas(@PathVariable("id") UUID promotionId) {
    return graduationService.diplomas(promotionId);
  }

  @GetMapping("/promotions/{id}/results")
  public PromotionResultsResponse getResults(@PathVariable("id") UUID promotionId) {
    return graduationService.results(promotionId);
  }
}
