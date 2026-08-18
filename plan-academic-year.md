# Plan: Add Academic Year Concept (S1+S2 = 1 Year)

## Context
The current model only has `semester` (1 or 2) on `Course`, but there's no way to group semesters into academic years. The subject requires computing **annual averages** and **annual credits** (S1+S2=1 year). This feature adds that concept.

## Approach
Add an `academicYear` integer field (1, 2, or 3) to `Course`. The rule is: `academicYear = (semester + 1) / 2`. This allows filtering courses/exams by academic year and computing annual summaries.

## Files to Modify

### 1. `src/main/java/school/hei/admin/entity/Course.java`
- Add `int academicYear` field to the record

### 2. `src/main/java/school/hei/admin/repository/model/JCourse.java`
- Add `@Column(name = "academic_year") private int academicYear` field

### 3. `src/main/java/school/hei/admin/mapper/CourseMapper.java`
- Map the new `academicYear` field in both `toModel()` and `toEntity()`

### 4. `src/main/java/school/hei/admin/dto/response/AcademicYearResult.java`
- **New file**: DTO with fields: `int academicYear`, `Double average`, `int validatedCredits`, `int totalCredits`, `String status`, `List<CourseGradeResult> results`

### 5. `src/main/java/school/hei/admin/service/GradeService.java`
- Add `getAcademicYearResults(UUID studentId, int academicYear)` method:
  - Fetch all course results for the student
  - Filter by `academicYear` (using `CourseGradeResult.semester` to derive year)
  - Return `AcademicYearResult` with summary (average, validated credits, total credits, status)

### 6. `src/main/java/school/hei/admin/endpoint/rest/controller/student/StudentController.java`
- Add endpoint: `GET /students/{id}/academic-year?year=1`

### 7. `src/main/resources/db/migration/V2__Add_academic_year_to_course.sql`
- New Flyway migration: `ALTER TABLE course ADD COLUMN academic_year INTEGER DEFAULT 1`

### 8. `src/test/java/school/hei/admin/service/AcademicYearServiceTest.java`
- **New file**: Unit tests for the academic year feature
  - Test: courses from S1+S2 are grouped into year 1
  - Test: average is computed weighted by credits across both semesters
  - Test: validated credits count only courses with average >= 10
  - Test: status is COMPLET when all courses graded, PROVISOIRE otherwise
  - Test: filtering by academic year works correctly

## Commit
- Branch name: `feature/academic-year-concept`
- Commit message: `feat: add academic year concept for annual grade computation`
