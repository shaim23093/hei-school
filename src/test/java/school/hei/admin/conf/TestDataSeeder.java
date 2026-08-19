package school.hei.admin.conf;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.hei.admin.entity.enums.Path;
import school.hei.admin.entity.enums.Role;
import school.hei.admin.repository.AccountRepository;
import school.hei.admin.repository.CourseGroupRepository;
import school.hei.admin.repository.CourseRepository;
import school.hei.admin.repository.ExamRepository;
import school.hei.admin.repository.GradeRepository;
import school.hei.admin.repository.GroupRepository;
import school.hei.admin.repository.PromotionRepository;
import school.hei.admin.repository.StudentGroupRepository;
import school.hei.admin.repository.StudentRepository;
import school.hei.admin.repository.TeacherCourseRepository;
import school.hei.admin.repository.TeacherRepository;
import school.hei.admin.repository.model.JAccount;
import school.hei.admin.repository.model.JCourse;
import school.hei.admin.repository.model.JCourseGroup;
import school.hei.admin.repository.model.JExam;
import school.hei.admin.repository.model.JGrade;
import school.hei.admin.repository.model.JGroup;
import school.hei.admin.repository.model.JPromotion;
import school.hei.admin.repository.model.JStudent;
import school.hei.admin.repository.model.JStudentGroup;
import school.hei.admin.repository.model.JTeacher;
import school.hei.admin.repository.model.JTeacherCourse;

@Configuration
@AllArgsConstructor
public class TestDataSeeder implements CommandLineRunner {
  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final PromotionRepository promotionRepository;
  private final GroupRepository groupRepository;
  private final CourseRepository courseRepository;
  private final CourseGroupRepository courseGroupRepository;
  private final StudentGroupRepository studentGroupRepository;
  private final StudentRepository studentRepository;
  private final TeacherRepository teacherRepository;
  private final ExamRepository examRepository;
  private final GradeRepository gradeRepository;
  private final TeacherCourseRepository teacherCourseRepository;

  @Override
  public void run(String... args) {
    if (accountRepository.findByUsername("student1").isPresent()) {
      return;
    }

    account("admin", "admin123", Role.ADMIN);
    JAccount teacher1Account = account("teacher1", "teacher123", Role.TEACHER);
    JAccount teacher2Account = account("teacher2", "teacher123", Role.TEACHER);
    JAccount teacher3Account = account("teacher3", "teacher123", Role.TEACHER);
    JAccount teacher4Account = account("teacher4", "teacher123", Role.TEACHER);
    JAccount student1Account = account("student1", "student123", Role.STUDENT);
    JAccount student2Account = account("student2", "student123", Role.STUDENT);
    for (int i = 3; i <= 15; i++) {
      account("student" + i, "student123", Role.STUDENT);
    }

    JPromotion promotion =
        JPromotion.builder().id(UUID.randomUUID()).name("Promo 2024").entryYear(2024).build();
    promotionRepository.save(promotion);

    JGroup group1 = JGroup.builder().id(UUID.randomUUID()).name("Group1").path(Path.EL).build();
    JGroup group2 = JGroup.builder().id(UUID.randomUUID()).name("Group2").path(Path.TN).build();
    groupRepository.save(group1);
    groupRepository.save(group2);

    JCourse prog1 = course("PROG1", "Programmation 1", 6, 1, Path.EL);
    JCourse prog2 = course("PROG2", "Programmation 2", 5, 1, Path.EL);
    JCourse prog3 = course("PROG3", "Programmation 3", 5, 2, Path.EL);
    JCourse prog4 = course("PROG4", "Programmation 4", 5, 2, Path.EL);
    JCourse prog5 = course("PROG5", "Programmation 5", 4, 2, Path.EL);
    JCourse web1 = course("WEB1", "Developpement web 1", 5, 2, Path.EL);
    JCourse web2 = course("WEB2", "Developpement web 2", 4, 2, Path.EL);
    JCourse web3 = course("WEB3", "Developpement web 3", 4, 1, Path.EL);
    JCourse donnee1 = course("DONNEE1", "Gestion des donnees 1", 4, 1, Path.EL);
    JCourse donnee2 = course("DONNEE2", "Gestion des donnees 2", 4, 2, Path.EL);
    JCourse pro1 = course("PRO1", "Projet 1", 3, 1, Path.EL);
    JCourse pro2 = course("PRO2", "Projet 2", 3, 1, Path.EL);
    JCourse pro3 = course("PRO3", "Projet 3", 3, 2, Path.EL);
    JCourse pro4 = course("PRO4", "Projet 4", 3, 2, Path.EL);
    JCourse metier1 = course("METIER1", "Preparation metier 1", 4, 2, Path.EL);
    JCourse metier2 = course("METIER2", "Preparation metier 2", 4, 1, Path.EL);
    JCourse sys1 = course("SYS1", "Systeme 1", 5, 1, Path.TN);
    JCourse sys2 = course("SYS2", "Systeme 2", 4, 2, Path.TN);
    JCourse sys3 = course("SYS3", "Systeme 3", 4, 2, Path.TN);
    JCourse tn1 = course("TN1", "Reseaux 1", 5, 1, Path.TN);
    JCourse tn2 = course("TN2", "Reseaux 2", 5, 2, Path.TN);
    JCourse secu1 = course("SECU1", "Securite 1", 4, 2, Path.TN);
    JCourse secu2 = course("SECU2", "Securite 2", 4, 1, Path.TN);
    JCourse ia1 = course("IA1", "Intelligence artificielle 1", 4, 1, Path.TN);
    JCourse ia2 = course("IA2", "Intelligence artificielle 2", 4, 2, Path.TN);
    JCourse mgt1 = course("MGT1", "Management 1", 3, 1, Path.TN);
    JCourse mgt2 = course("MGT2", "Management 2", 3, 2, Path.TN);
    JCourse lv1 = course("LV1", "Langue vivante 1", 3, 1, Path.TN);
    JCourse lv2 = course("LV2", "Langue vivante 2", 3, 2, Path.TN);
    List<JCourse> courses =
        List.of(
            prog1, prog2, prog3, prog4, prog5, web1, web2, web3, donnee1, donnee2, pro1, pro2, pro3,
            pro4, metier1, metier2, sys1, sys2, sys3, tn1, tn2, secu1, secu2, ia1, ia2, mgt1, mgt2,
            lv1, lv2);
    courseRepository.saveAll(courses);
    courses.stream()
        .filter(course -> course.getPath() == Path.EL)
        .forEach(course -> courseGroup(group1, promotion, course));
    courses.stream()
        .filter(course -> course.getPath() == Path.TN)
        .forEach(course -> courseGroup(group2, promotion, course));

    JTeacher teacher1 =
        JTeacher.builder()
            .id(UUID.randomUUID())
            .account(teacher1Account)
            .name("Rakoto")
            .firstName("Mamy")
            .email("mamy.rakoto@hei.school")
            .build();
    JTeacher teacher2 =
        JTeacher.builder()
            .id(UUID.randomUUID())
            .account(teacher2Account)
            .name("Rabe")
            .firstName("Hery")
            .email("hery.rabe@hei.school")
            .build();
    teacherRepository.save(teacher1);
    teacherRepository.save(teacher2);

    JStudent student1 =
        JStudent.builder()
            .id(UUID.randomUUID())
            .account(student1Account)
            .promotion(promotion)
            .std("STD24001")
            .name("Andria")
            .firstName("Tiana")
            .email("tiana.andria@hei.school")
            .build();
    JStudent student2 =
        JStudent.builder()
            .id(UUID.randomUUID())
            .account(student2Account)
            .promotion(promotion)
            .std("STD24002")
            .name("Randria")
            .firstName("Naina")
            .email("naina.randria@hei.school")
            .build();
    studentRepository.save(student1);
    studentRepository.save(student2);

    studentGroup(student1, group1, 1);
    studentGroup(student1, group1, 2);
    studentGroup(student2, group2, 1);

    teacherCourse(teacher1, prog1, group1, promotion);
    teacherCourse(teacher1, web1, group1, promotion);
    teacherCourse(teacher2, tn1, group2, promotion);

    JExam prog1Cc1 = exam(prog1, promotion, "CC1", 0.5);
    JExam prog1Exam = exam(prog1, promotion, "Exam", 0.5);
    JExam web1Cc1 = exam(web1, promotion, "CC1", 0.5);
    JExam web1Exam = exam(web1, promotion, "Exam", 0.5);
    JExam tn1Cc1 = exam(tn1, promotion, "CC1", 0.5);
    JExam tn1Exam = exam(tn1, promotion, "Exam", 0.5);

    grade(student1, prog1Cc1, 12);
    grade(student1, prog1Exam, 14);
    grade(student1, web1Cc1, 10);
    grade(student2, tn1Cc1, 10);
    grade(student2, tn1Exam, 12);
  }

  private JAccount account(String username, String password, Role role) {
    JAccount account =
        JAccount.builder()
            .id(UUID.randomUUID())
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(role)
            .build();
    return accountRepository.save(account);
  }

  private JCourse course(String code, String name, int credits, int semester, Path path) {
    return JCourse.builder()
        .id(UUID.randomUUID())
        .code(code)
        .name(name)
        .credits(credits)
        .semester(semester)
        .path(path)
        .build();
  }

  private void courseGroup(JGroup group, JPromotion promotion, JCourse course) {
    courseGroupRepository.save(
        JCourseGroup.builder()
            .id(UUID.randomUUID())
            .group(group)
            .promotion(promotion)
            .course(course)
            .build());
  }

  private void studentGroup(JStudent student, JGroup group, int semester) {
    studentGroupRepository.save(
        JStudentGroup.builder()
            .id(UUID.randomUUID())
            .student(student)
            .group(group)
            .semester(semester)
            .build());
  }

  private void teacherCourse(JTeacher teacher, JCourse course, JGroup group, JPromotion promotion) {
    teacherCourseRepository.save(
        JTeacherCourse.builder()
            .id(UUID.randomUUID())
            .teacher(teacher)
            .course(course)
            .group(group)
            .promotion(promotion)
            .build());
  }

  private JExam exam(JCourse course, JPromotion promotion, String title, double coefficient) {
    JExam exam =
        JExam.builder()
            .id(UUID.randomUUID())
            .course(course)
            .promotion(promotion)
            .title(title)
            .dateTime(Instant.now())
            .durationMinutes(120)
            .coefficient(coefficient)
            .build();
    return examRepository.save(exam);
  }

  private void grade(JStudent student, JExam exam, double value) {
    gradeRepository.save(
        JGrade.builder()
            .id(UUID.randomUUID())
            .student(student)
            .exam(exam)
            .value(value)
            .createdAt(Instant.now())
            .build());
  }
}
