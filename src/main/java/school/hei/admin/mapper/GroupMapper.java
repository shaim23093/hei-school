package school.hei.admin.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import school.hei.admin.entity.Group;
import school.hei.admin.repository.model.JGroup;

@Component
public class GroupMapper {
  public Group toModel(JGroup entity) {
    return Group.builder().id(entity.getId()).name(entity.getName()).path(entity.getPath()).build();
  }

  public List<Group> toModel(List<JGroup> entities) {
    return entities.stream().map(this::toModel).toList();
  }

  public JGroup toEntity(Group model) {
    return JGroup.builder().id(model.id()).name(model.name()).path(model.path()).build();
  }

  public List<JGroup> toEntity(List<Group> models) {
    return models.stream().map(this::toEntity).toList();
  }
}
