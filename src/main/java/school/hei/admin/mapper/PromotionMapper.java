package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Promotion;
import school.hei.admin.repository.model.JPromotion;

@Component
public class PromotionMapper {
  public Promotion toModel(JPromotion entity) {
    return Promotion.builder()
        .id(entity.getId())
        .name(entity.getName())
        .entryYear(entity.getEntryYear())
        .build();
  }

  public List<Promotion> toModel(List<JPromotion> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JPromotion toEntity(Promotion model) {
    return JPromotion.builder()
        .id(model.id())
        .name(model.name())
        .entryYear(model.entryYear())
        .build();
  }

  public List<JPromotion> toEntity(List<Promotion> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
