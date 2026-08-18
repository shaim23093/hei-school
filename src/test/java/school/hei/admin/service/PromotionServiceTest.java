package school.hei.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.admin.dto.request.PromotionCreateRequest;
import school.hei.admin.dto.request.PromotionUpdateRequest;
import school.hei.admin.entity.Promotion;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.PromotionMapper;
import school.hei.admin.repository.PromotionRepository;
import school.hei.admin.repository.model.JPromotion;

class PromotionServiceTest {
  private final PromotionRepository promotionRepository = mock(PromotionRepository.class);
  private final PromotionMapper promotionMapper = new PromotionMapper();

  private final PromotionService service =
      new PromotionService(promotionRepository, promotionMapper);

  private JPromotion promotion;

  @BeforeEach
  void setUp() {
    promotion =
        JPromotion.builder().id(UUID.randomUUID()).name("Promo 2024").entryYear(2024).build();
  }

  @Test
  void list_returns_all_promotions() {
    when(promotionRepository.findAll()).thenReturn(List.of(promotion));

    List<Promotion> result = service.list();

    assertEquals(1, result.size());
    assertEquals("Promo 2024", result.get(0).name());
  }

  @Test
  void list_returns_empty_when_no_promotions() {
    when(promotionRepository.findAll()).thenReturn(List.of());

    assertEquals(List.of(), service.list());
  }

  @Test
  void getById_returns_promotion() {
    when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));

    Promotion result = service.getById(promotion.getId());

    assertEquals("Promo 2024", result.name());
    assertEquals(2024, result.entryYear());
  }

  @Test
  void getById_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(promotionRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getById(unknown));
  }

  @Test
  void create_saves_promotion() {
    when(promotionRepository.save(argThat(p -> "Promo 2025".equals(p.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    PromotionCreateRequest request =
        PromotionCreateRequest.builder().name("Promo 2025").entryYear(2025).build();

    Promotion result = service.create(request);

    assertEquals("Promo 2025", result.name());
    assertEquals(2025, result.entryYear());
  }

  @Test
  void update_modifies_only_provided_fields() {
    when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));
    when(promotionRepository.save(argThat(p -> "NewName".equals(p.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    PromotionUpdateRequest request = PromotionUpdateRequest.builder().name("NewName").build();

    Promotion result = service.update(promotion.getId(), request);

    assertEquals("NewName", result.name());
    assertEquals(2024, result.entryYear());
  }

  @Test
  void update_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(promotionRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> service.update(unknown, PromotionUpdateRequest.builder().name("X").build()));
  }

  @Test
  void delete_removes_promotion() {
    when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));

    service.delete(promotion.getId());

    verify(promotionRepository).delete(promotion);
  }

  @Test
  void delete_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(promotionRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.delete(unknown));
    verify(promotionRepository, never()).delete(argThat(p -> true));
  }
}
