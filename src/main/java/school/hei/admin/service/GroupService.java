package school.hei.admin.service;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.admin.dto.request.GroupCreateRequest;
import school.hei.admin.dto.request.GroupUpdateRequest;
import school.hei.admin.entity.Group;
import school.hei.admin.exception.NotFoundException;
import school.hei.admin.mapper.GroupMapper;
import school.hei.admin.repository.GroupRepository;
import school.hei.admin.repository.model.JGroup;

@Service
@AllArgsConstructor
public class GroupService {
  private final GroupRepository groupRepository;
  private final GroupMapper groupMapper;

  public List<Group> list() {
    return groupMapper.toModel(groupRepository.findAll());
  }

  public Group getById(UUID id) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Group not found: " + id));
    return groupMapper.toModel(entity);
  }

  public Group create(GroupCreateRequest request) {
    JGroup entity =
        JGroup.builder().id(UUID.randomUUID()).name(request.name()).path(request.path()).build();
    return groupMapper.toModel(groupRepository.save(entity));
  }

  public Group update(UUID id, GroupUpdateRequest request) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Group not found: " + id));

    if (request.name() != null) {
      entity.setName(request.name());
    }
    if (request.path() != null) {
      entity.setPath(request.path());
    }

    return groupMapper.toModel(groupRepository.save(entity));
  }

  public void delete(UUID id) {
    JGroup entity =
        groupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Group not found: " + id));
    groupRepository.delete(entity);
  }
}
