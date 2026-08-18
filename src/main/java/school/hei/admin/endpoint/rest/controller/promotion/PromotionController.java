package school.hei.admin.endpoint.rest.controller.promotion;

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
import school.hei.admin.dto.request.PromotionCreateRequest;
import school.hei.admin.dto.request.PromotionUpdateRequest;
import school.hei.admin.dto.response.GraduatedStudentResponse;
import school.hei.admin.dto.response.PromotionResultsResponse;
import school.hei.admin.entity.Promotion;
import school.hei.admin.service.GraduationService;
import school.hei.admin.service.PromotionService;

@RestController
@AllArgsConstructor
public class PromotionController {
  private final PromotionService promotionService;
  private final GraduationService graduationService;

  @GetMapping("/promotions")
  public List<Promotion> list() {
    return promotionService.list();
  }

  @GetMapping("/promotions/{id}")
  public Promotion getById(@PathVariable("id") UUID id) {
    return promotionService.getById(id);
  }

  @PostMapping("/promotions")
  public Promotion create(@Valid @RequestBody PromotionCreateRequest request) {
    return promotionService.create(request);
  }

  @PutMapping("/promotions/{id}")
  public Promotion update(
      @PathVariable("id") UUID id, @Valid @RequestBody PromotionUpdateRequest request) {
    return promotionService.update(id, request);
  }

  @DeleteMapping("/promotions/{id}")
  public void delete(@PathVariable("id") UUID id) {
    promotionService.delete(id);
  }

  @GetMapping("/promotions/{id}/diplomes")
  public List<GraduatedStudentResponse> getDiplomas(@PathVariable("id") UUID promotionId) {
    return graduationService.diplomas(promotionId);
  }

  @GetMapping("/promotions/{id}/results")
  public PromotionResultsResponse getResults(@PathVariable("id") UUID promotionId) {
    return graduationService.results(promotionId);
  }
}
