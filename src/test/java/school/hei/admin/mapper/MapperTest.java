package school.hei.admin.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import school.hei.admin.entity.Account;
import school.hei.admin.entity.Course;
import school.hei.admin.entity.CourseGroup;
import school.hei.admin.entity.Exam;
import school.hei.admin.entity.Grade;
import school.hei.admin.entity.GradeHistory;
import school.hei.admin.entity.Group;
import school.hei.admin.entity.Promotion;
import school.hei.admin.entity.Student;
import school.hei.admin.entity.StudentGroup;
import school.hei.admin.entity.Teacher;
import school.hei.admin.entity.TeacherCourse;
import school.hei.admin.entity.enums.Path;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JCourseGroup;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JGradeHistory;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;
import school.hei.admin.repository.model.JStudentGroup;
import school.hei.admin.repository.model.JTeacher;
import school.hei.admin.repository.model.JTeacherCourse;

class MapperTest {

  private final AccountMapper accountMapper = new AccountMapper();
  private final GroupMapper groupMapper = new GroupMapper();
  private final PromotionMapper promotionMapper = new PromotionMapper();
  private final CourseMapper courseMapper = new CourseMapper();
  private final ExamMapper examMapper = new ExamMapper();
  private final GradeMapper gradeMapper = new GradeMapper();
  private final GradeHistoryMapper gradeHistoryMapper = new GradeHistoryMapper();
  private final TeacherMapper teacherMapper = new TeacherMapper();
  private final StudentMapper studentMapper = new StudentMapper();
  private final TeacherCourseMapper teacherCourseMapper = new TeacherCourseMapper();
  private final StudentGroupMapper studentGroupMapper = new StudentGroupMapper();
  private final CourseGroupMapper courseGroupMapper = new CourseGroupMapper();

  private final UUID id = UUID.randomUUID();
  private final UUID accountId = UUID.randomUUID();
  private final UUID promotionId = UUID.randomUUID();
  private final UUID courseId = UUID.randomUUID();
  private final UUID studentId = UUID.randomUUID();
  private final UUID teacherId = UUID.randomUUID();
  private final UUID groupId = UUID.randomUUID();
  private final UUID examId = UUID.randomUUID();
  private final UUID gradeId = UUID.randomUUID();

  private final JAccount jAccount =
      JAccount.builder().id(accountId).username("user").password("pw").role(Role.ADMIN).build();
  private final JPromotion jPromotion =
      JPromotion.builder().id(promotionId).name("Promo 2024").entryYear(2024).build();
  private final JCourse jCourse =
      JCourse.builder()
          .id(courseId)
          .code("PROG1")
          .name("Programming")
          .credits(6)
          .path(Path.EL)
          .semester(1)
          .academicYear(2024)
          .build();
  private final JStudent jStudent =
      JStudent.builder()
          .id(studentId)
          .std("STD24001")
          .name("Andria")
          .firstName("Tiana")
          .email("tiana@hei.school")
          .build();
  private final JTeacher jTeacher =
      JTeacher.builder()
          .id(teacherId)
          .name("Rakoto")
          .firstName("Mamy")
          .email("mamy@hei.school")
          .build();
  private final JGroup jGroup = JGroup.builder().id(groupId).name("Group1").path(Path.EL).build();
  private final JExam jExam =
      JExam.builder()
          .id(examId)
          .course(jCourse)
          .promotion(jPromotion)
          .title("Exam")
          .dateTime(Instant.now())
          .durationMinutes(120)
          .coefficient(0.5)
          .build();
  private final JGrade jGrade =
      JGrade.builder()
          .id(gradeId)
          .student(jStudent)
          .exam(jExam)
          .value(15)
          .createdAt(Instant.now())
          .build();
  private final JGradeHistory jGradeHistory =
      JGradeHistory.builder()
          .id(UUID.randomUUID())
          .grade(jGrade)
          .student(jStudent)
          .exam(jExam)
          .value(15)
          .modifiedAt(Instant.now())
          .author("admin")
          .build();
  private final JTeacherCourse jTeacherCourse =
      JTeacherCourse.builder()
          .id(UUID.randomUUID())
          .teacher(jTeacher)
          .course(jCourse)
          .group(jGroup)
          .promotion(jPromotion)
          .build();
  private final JStudentGroup jStudentGroup =
      JStudentGroup.builder()
          .id(UUID.randomUUID())
          .student(jStudent)
          .group(jGroup)
          .semester(1)
          .build();
  private final JCourseGroup jCourseGroup =
      JCourseGroup.builder()
          .id(UUID.randomUUID())
          .course(jCourse)
          .group(jGroup)
          .promotion(jPromotion)
          .build();

  @Test
  void accountMapper_toModel_and_toEntity_roundtrip() {
    Account model = accountMapper.toModel(jAccount);
    assertEquals(accountId, model.id());
    assertEquals("user", model.username());
    assertEquals("pw", model.password());
    assertEquals(Role.ADMIN, model.role());

    JAccount entity = accountMapper.toEntity(model);
    assertEquals(accountId, entity.getId());
    assertEquals("user", entity.getUsername());
    assertEquals(Role.ADMIN, entity.getRole());
  }

  @Test
  void accountMapper_list_roundtrip() {
    List<Account> models = accountMapper.toModel(List.of(jAccount));
    assertEquals(1, models.size());
    List<JAccount> entities = accountMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void groupMapper_toModel_and_toEntity_roundtrip() {
    Group model = groupMapper.toModel(jGroup);
    assertEquals(groupId, model.id());
    assertEquals("Group1", model.name());
    assertEquals(Path.EL, model.path());

    JGroup entity = groupMapper.toEntity(model);
    assertEquals(groupId, entity.getId());
    assertEquals("Group1", entity.getName());
  }

  @Test
  void groupMapper_list_roundtrip() {
    List<Group> models = groupMapper.toModel(List.of(jGroup));
    assertEquals(1, models.size());
    List<JGroup> entities = groupMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void promotionMapper_toModel_and_toEntity_roundtrip() {
    Promotion model = promotionMapper.toModel(jPromotion);
    assertEquals(promotionId, model.id());
    assertEquals("Promo 2024", model.name());
    assertEquals(2024, model.entryYear());

    JPromotion entity = promotionMapper.toEntity(model);
    assertEquals(promotionId, entity.getId());
    assertEquals("Promo 2024", entity.getName());
  }

  @Test
  void promotionMapper_list_roundtrip() {
    List<Promotion> models = promotionMapper.toModel(List.of(jPromotion));
    assertEquals(1, models.size());
    List<JPromotion> entities = promotionMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void courseMapper_toModel_and_toEntity_roundtrip() {
    Course model = courseMapper.toModel(jCourse);
    assertEquals(courseId, model.id());
    assertEquals("PROG1", model.code());
    assertEquals("Programming", model.name());
    assertEquals(6, model.credits());
    assertEquals(Path.EL, model.path());
    assertEquals(1, model.semester());
    assertEquals(2024, model.academicYear());

    JCourse entity = courseMapper.toEntity(model);
    assertEquals(courseId, entity.getId());
    assertEquals("Programming", entity.getName());
  }

  @Test
  void courseMapper_list_roundtrip() {
    List<Course> models = courseMapper.toModel(List.of(jCourse));
    assertEquals(1, models.size());
    List<JCourse> entities = courseMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void examMapper_toModel_and_toEntity_roundtrip() {
    Exam model = examMapper.toModel(jExam);
    assertEquals(examId, model.id());
    assertEquals(courseId, model.courseId());
    assertEquals(promotionId, model.promotionId());
    assertEquals("Exam", model.title());
    assertEquals(120, model.durationMinutes());
    assertEquals(0.5, model.coefficient());

    JExam entity = examMapper.toEntity(model);
    assertEquals(examId, entity.getId());
    assertNotNull(entity.getCourse());
    assertEquals(courseId, entity.getCourse().getId());
  }

  @Test
  void examMapper_toModel_handles_null_relations() {
    JExam exam = JExam.builder().id(examId).title("Isolated").build();
    Exam model = examMapper.toModel(exam);
    assertEquals(examId, model.id());
    assertNull(model.courseId());
    assertNull(model.promotionId());
  }

  @Test
  void examMapper_toEntity_handles_null_relations() {
    Exam model =
        Exam.builder()
            .id(examId)
            .title("Isolated")
            .title("Isolated")
            .dateTime(Instant.now())
            .durationMinutes(60)
            .coefficient(1.0)
            .build();
    JExam entity = examMapper.toEntity(model);
    assertEquals(examId, entity.getId());
    assertNull(entity.getCourse().getId());
    assertNull(entity.getPromotion().getId());
  }

  @Test
  void examMapper_list_roundtrip() {
    List<Exam> models = examMapper.toModel(List.of(jExam));
    assertEquals(1, models.size());
    List<JExam> entities = examMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void gradeMapper_toModel_and_toEntity_roundtrip() {
    Grade model = gradeMapper.toModel(jGrade);
    assertEquals(gradeId, model.id());
    assertEquals(studentId, model.studentId());
    assertEquals(examId, model.examId());
    assertEquals(15, model.value());

    JGrade entity = gradeMapper.toEntity(model);
    assertEquals(gradeId, entity.getId());
    assertNotNull(entity.getStudent());
    assertEquals(studentId, entity.getStudent().getId());
  }

  @Test
  void gradeMapper_toModel_handles_null_relations() {
    JGrade grade = JGrade.builder().id(gradeId).value(10).build();
    Grade model = gradeMapper.toModel(grade);
    assertEquals(gradeId, model.id());
    assertNull(model.studentId());
    assertNull(model.examId());
  }

  @Test
  void gradeMapper_toEntity_handles_null_relations() {
    Grade model = Grade.builder().id(gradeId).value(10).createdAt(Instant.now()).build();
    JGrade entity = gradeMapper.toEntity(model);
    assertEquals(gradeId, entity.getId());
    assertNull(entity.getStudent().getId());
    assertNull(entity.getExam().getId());
  }

  @Test
  void gradeMapper_list_roundtrip() {
    List<Grade> models = gradeMapper.toModel(List.of(jGrade));
    assertEquals(1, models.size());
    List<JGrade> entities = gradeMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void gradeHistoryMapper_toModel_and_toEntity_roundtrip() {
    GradeHistory model = gradeHistoryMapper.toModel(jGradeHistory);
    assertEquals(jGradeHistory.getId(), model.id());
    assertEquals(gradeId, model.gradeId());
    assertEquals(studentId, model.studentId());
    assertEquals(examId, model.examId());
    assertEquals(15, model.value());
    assertEquals("admin", model.author());

    JGradeHistory entity = gradeHistoryMapper.toEntity(model);
    assertEquals(jGradeHistory.getId(), entity.getId());
    assertNotNull(entity.getGrade());
    assertEquals(gradeId, entity.getGrade().getId());
  }

  @Test
  void gradeHistoryMapper_toModel_handles_null_relations() {
    JGradeHistory history =
        JGradeHistory.builder()
            .id(UUID.randomUUID())
            .value(10)
            .modifiedAt(Instant.now())
            .author("u")
            .build();
    GradeHistory model = gradeHistoryMapper.toModel(history);
    assertNull(model.gradeId());
    assertNull(model.studentId());
    assertNull(model.examId());
  }

  @Test
  void gradeHistoryMapper_toEntity_handles_null_relations() {
    GradeHistory model =
        GradeHistory.builder()
            .id(UUID.randomUUID())
            .value(10)
            .modifiedAt(Instant.now())
            .author("u")
            .build();
    JGradeHistory entity = gradeHistoryMapper.toEntity(model);
    assertNull(entity.getGrade().getId());
    assertNull(entity.getStudent().getId());
    assertNull(entity.getExam().getId());
  }

  @Test
  void gradeHistoryMapper_list_roundtrip() {
    List<GradeHistory> models = gradeHistoryMapper.toModel(List.of(jGradeHistory));
    assertEquals(1, models.size());
    List<JGradeHistory> entities = gradeHistoryMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void teacherMapper_toModel_and_toEntity_roundtrip() {
    Teacher model = teacherMapper.toModel(jTeacher);
    assertEquals(teacherId, model.id());
    assertEquals("Rakoto", model.name());
    assertEquals("Mamy", model.firstName());
    assertEquals("mamy@hei.school", model.email());
    assertNull(model.accountId());

    jTeacher.setAccount(jAccount);
    Teacher modelWithAccount = teacherMapper.toModel(jTeacher);
    assertEquals(accountId, modelWithAccount.accountId());

    JTeacher entity = teacherMapper.toEntity(modelWithAccount);
    assertEquals(teacherId, entity.getId());
    assertNotNull(entity.getAccount());
    assertEquals(accountId, entity.getAccount().getId());
  }

  @Test
  void teacherMapper_toEntity_handles_null_account() {
    Teacher model =
        Teacher.builder()
            .id(teacherId)
            .name("Rakoto")
            .firstName("Mamy")
            .email("mamy@hei.school")
            .accountId(null)
            .build();
    JTeacher entity = teacherMapper.toEntity(model);
    assertNull(entity.getAccount().getId());
  }

  @Test
  void teacherMapper_list_roundtrip() {
    List<Teacher> models = teacherMapper.toModel(List.of(jTeacher));
    assertEquals(1, models.size());
    List<JTeacher> entities = teacherMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void studentMapper_toModel_and_toEntity_roundtrip() {
    jStudent.setAccount(jAccount);
    jStudent.setPromotion(jPromotion);
    Student model = studentMapper.toModel(jStudent);
    assertEquals(studentId, model.id());
    assertEquals(accountId, model.accountId());
    assertEquals(promotionId, model.promotionId());
    assertEquals("STD24001", model.std());
    assertEquals("Andria", model.name());
    assertEquals("Tiana", model.firstName());
    assertEquals("tiana@hei.school", model.email());

    JStudent entity = studentMapper.toEntity(model);
    assertEquals(studentId, entity.getId());
    assertNotNull(entity.getAccount());
    assertEquals(accountId, entity.getAccount().getId());
  }

  @Test
  void studentMapper_toModel_handles_null_relations() {
    JStudent student =
        JStudent.builder()
            .id(studentId)
            .std("STD24001")
            .name("Andria")
            .firstName("Tiana")
            .email("a@b.c")
            .build();
    Student model = studentMapper.toModel(student);
    assertNull(model.accountId());
    assertNull(model.promotionId());
  }

  @Test
  void studentMapper_toEntity_handles_null_relations() {
    Student model =
        Student.builder().id(studentId).std("S1").name("N").firstName("F").email("a@b.c").build();
    JStudent entity = studentMapper.toEntity(model);
    assertNull(entity.getAccount().getId());
    assertNull(entity.getPromotion().getId());
  }

  @Test
  void studentMapper_list_roundtrip() {
    List<Student> models = studentMapper.toModel(List.of(jStudent));
    assertEquals(1, models.size());
    List<JStudent> entities = studentMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void teacherCourseMapper_toModel_and_toEntity_roundtrip() {
    TeacherCourse model = teacherCourseMapper.toModel(jTeacherCourse);
    assertEquals(jTeacherCourse.getId(), model.id());
    assertEquals(teacherId, model.teacherId());
    assertEquals(courseId, model.courseId());
    assertEquals(groupId, model.groupId());
    assertEquals(promotionId, model.promotionId());

    JTeacherCourse entity = teacherCourseMapper.toEntity(model);
    assertEquals(jTeacherCourse.getId(), entity.getId());
    assertNotNull(entity.getTeacher());
    assertEquals(teacherId, entity.getTeacher().getId());
  }

  @Test
  void teacherCourseMapper_toModel_handles_null_relations() {
    JTeacherCourse tc = JTeacherCourse.builder().id(UUID.randomUUID()).build();
    TeacherCourse model = teacherCourseMapper.toModel(tc);
    assertNull(model.teacherId());
    assertNull(model.courseId());
    assertNull(model.groupId());
    assertNull(model.promotionId());
  }

  @Test
  void teacherCourseMapper_toEntity_handles_null_relations() {
    TeacherCourse model = TeacherCourse.builder().id(UUID.randomUUID()).build();
    JTeacherCourse entity = teacherCourseMapper.toEntity(model);
    assertNull(entity.getTeacher().getId());
    assertNull(entity.getCourse().getId());
    assertNull(entity.getGroup().getId());
    assertNull(entity.getPromotion().getId());
  }

  @Test
  void teacherCourseMapper_list_roundtrip() {
    List<TeacherCourse> models = teacherCourseMapper.toModel(List.of(jTeacherCourse));
    assertEquals(1, models.size());
    List<JTeacherCourse> entities = teacherCourseMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void studentGroupMapper_toModel_and_toEntity_roundtrip() {
    StudentGroup model = studentGroupMapper.toModel(jStudentGroup);
    assertEquals(jStudentGroup.getId(), model.id());
    assertEquals(studentId, model.studentId());
    assertEquals(groupId, model.groupId());
    assertEquals(1, model.semester());

    JStudentGroup entity = studentGroupMapper.toEntity(model);
    assertEquals(jStudentGroup.getId(), entity.getId());
    assertNotNull(entity.getStudent());
    assertEquals(studentId, entity.getStudent().getId());
  }

  @Test
  void studentGroupMapper_toModel_handles_null_relations() {
    JStudentGroup sg = JStudentGroup.builder().id(UUID.randomUUID()).semester(2).build();
    StudentGroup model = studentGroupMapper.toModel(sg);
    assertNull(model.studentId());
    assertNull(model.groupId());
  }

  @Test
  void studentGroupMapper_toEntity_handles_null_relations() {
    StudentGroup model = StudentGroup.builder().id(UUID.randomUUID()).semester(2).build();
    JStudentGroup entity = studentGroupMapper.toEntity(model);
    assertNull(entity.getStudent().getId());
    assertNull(entity.getGroup().getId());
  }

  @Test
  void studentGroupMapper_list_roundtrip() {
    List<StudentGroup> models = studentGroupMapper.toModel(List.of(jStudentGroup));
    assertEquals(1, models.size());
    List<JStudentGroup> entities = studentGroupMapper.toEntity(models);
    assertEquals(1, entities.size());
  }

  @Test
  void courseGroupMapper_toModel_and_toEntity_roundtrip() {
    CourseGroup model = courseGroupMapper.toModel(jCourseGroup);
    assertEquals(jCourseGroup.getId(), model.id());
    assertEquals(courseId, model.courseId());
    assertEquals(groupId, model.groupId());
    assertEquals(promotionId, model.promotionId());

    JCourseGroup entity = courseGroupMapper.toEntity(model);
    assertEquals(jCourseGroup.getId(), entity.getId());
    assertNotNull(entity.getCourse());
    assertEquals(courseId, entity.getCourse().getId());
  }

  @Test
  void courseGroupMapper_toModel_handles_null_relations() {
    JCourseGroup cg = JCourseGroup.builder().id(UUID.randomUUID()).build();
    CourseGroup model = courseGroupMapper.toModel(cg);
    assertNull(model.courseId());
    assertNull(model.groupId());
    assertNull(model.promotionId());
  }

  @Test
  void courseGroupMapper_toEntity_handles_null_relations() {
    CourseGroup model = CourseGroup.builder().id(UUID.randomUUID()).build();
    JCourseGroup entity = courseGroupMapper.toEntity(model);
    assertNull(entity.getCourse().getId());
    assertNull(entity.getGroup().getId());
    assertNull(entity.getPromotion().getId());
  }

  @Test
  void courseGroupMapper_list_roundtrip() {
    List<CourseGroup> models = courseGroupMapper.toModel(List.of(jCourseGroup));
    assertEquals(1, models.size());
    List<JCourseGroup> entities = courseGroupMapper.toEntity(models);
    assertEquals(1, entities.size());
  }
}
