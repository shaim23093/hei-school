package school.hei.admin.endpoint.rest.controller.ui;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import school.hei.admin.PojaGenerated;
import school.hei.admin.entity.Promotion;
import school.hei.admin.service.PromotionService;

@PojaGenerated
@Controller
@AllArgsConstructor
public class UiController {
  private final PromotionService promotionService;

  @GetMapping("/ui/promotions")
  public String promotions(Model model) {
    List<Promotion> promotions = promotionService.list();
    model.addAttribute("promotions", promotions);
    return "ui/promotions";
  }
}
