package school.hei.admin.endpoint.rest.controller.group;

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
import school.hei.admin.dto.request.GroupCreateRequest;
import school.hei.admin.dto.request.GroupUpdateRequest;
import school.hei.admin.entity.Group;
import school.hei.admin.service.GroupService;

@RestController
@AllArgsConstructor
public class GroupController {
  private final GroupService groupService;

  @GetMapping("/groups")
  public List<Group> list() {
    return groupService.list();
  }

  @GetMapping("/groups/{id}")
  public Group getById(@PathVariable("id") UUID id) {
    return groupService.getById(id);
  }

  @PostMapping("/groups")
  public Group create(@Valid @RequestBody GroupCreateRequest request) {
    return groupService.create(request);
  }

  @PutMapping("/groups/{id}")
  public Group update(@PathVariable("id") UUID id, @Valid @RequestBody GroupUpdateRequest request) {
    return groupService.update(id, request);
  }

  @DeleteMapping("/groups/{id}")
  public void delete(@PathVariable("id") UUID id) {
    groupService.delete(id);
  }
}
