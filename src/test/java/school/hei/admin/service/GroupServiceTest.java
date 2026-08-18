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
import school.hei.admin.dto.request.GroupCreateRequest;
import school.hei.admin.dto.request.GroupUpdateRequest;
import school.hei.admin.entity.Group;
import school.hei.admin.entity.enums.Path;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.GroupMapper;
import school.hei.admin.repository.GroupRepository;
import school.hei.admin.repository.model.JGroup;

class GroupServiceTest {
  private final GroupRepository groupRepository = mock(GroupRepository.class);
  private final GroupMapper groupMapper = new GroupMapper();

  private final GroupService service = new GroupService(groupRepository, groupMapper);

  private JGroup group;

  @BeforeEach
  void setUp() {
    group = JGroup.builder().id(UUID.randomUUID()).name("EL1").path(Path.EL).build();
  }

  @Test
  void list_returns_all_groups() {
    when(groupRepository.findAll()).thenReturn(List.of(group));

    List<Group> result = service.list();

    assertEquals(1, result.size());
    assertEquals("EL1", result.get(0).name());
    assertEquals(Path.EL, result.get(0).path());
  }

  @Test
  void list_returns_empty_when_no_groups() {
    when(groupRepository.findAll()).thenReturn(List.of());

    assertEquals(List.of(), service.list());
  }

  @Test
  void getById_returns_group() {
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    Group result = service.getById(group.getId());

    assertEquals("EL1", result.name());
    assertEquals(Path.EL, result.path());
  }

  @Test
  void getById_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(groupRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.getById(unknown));
  }

  @Test
  void create_saves_group() {
    when(groupRepository.save(argThat(g -> "TN1".equals(g.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    GroupCreateRequest request = GroupCreateRequest.builder().name("TN1").path(Path.TN).build();

    Group result = service.create(request);

    assertEquals("TN1", result.name());
    assertEquals(Path.TN, result.path());
  }

  @Test
  void update_modifies_only_provided_fields() {
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    when(groupRepository.save(argThat(g -> "NewName".equals(g.getName()))))
        .thenAnswer(i -> i.getArgument(0));

    GroupUpdateRequest request = GroupUpdateRequest.builder().name("NewName").build();

    Group result = service.update(group.getId(), request);

    assertEquals("NewName", result.name());
    assertEquals(Path.EL, result.path());
  }

  @Test
  void update_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(groupRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class,
        () -> service.update(unknown, GroupUpdateRequest.builder().name("X").build()));
  }

  @Test
  void delete_removes_group() {
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    service.delete(group.getId());

    verify(groupRepository).delete(group);
  }

  @Test
  void delete_throws_not_found() {
    UUID unknown = UUID.randomUUID();
    when(groupRepository.findById(unknown)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.delete(unknown));
    verify(groupRepository, never()).delete(argThat(g -> true));
  }
}
