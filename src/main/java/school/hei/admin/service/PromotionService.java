package school.hei.admin.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.admin.dto.request.PromotionCreateRequest;
import school.hei.admin.dto.request.PromotionUpdateRequest;
import school.hei.admin.entity.Promotion;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.PromotionMapper;
import school.hei.admin.repository.PromotionRepository;
import school.hei.admin.repository.model.JPromotion;

@Service
@AllArgsConstructor
public class PromotionService {
  private final PromotionRepository promotionRepository;
  private final PromotionMapper promotionMapper;

  public List<Promotion> list() {
    return promotionMapper.toModel(promotionRepository.findAll());
  }

  public Promotion getById(UUID id) {
    JPromotion entity =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + id));
    return promotionMapper.toModel(entity);
  }

  public Promotion create(PromotionCreateRequest request) {
    JPromotion entity =
        JPromotion.builder()
            .id(UUID.randomUUID())
            .name(request.name())
            .entryYear(request.entryYear())
            .build();
    return promotionMapper.toModel(promotionRepository.save(entity));
  }

  public Promotion update(UUID id, PromotionUpdateRequest request) {
    JPromotion entity =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + id));

    if (request.name() != null) {
      entity.setName(request.name());
    }
    if (request.entryYear() != null) {
      entity.setEntryYear(request.entryYear());
    }

    return promotionMapper.toModel(promotionRepository.save(entity));
  }

  public void delete(UUID id) {
    JPromotion entity =
        promotionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Promotion not found: " + id));
    promotionRepository.delete(entity);
  }
}
