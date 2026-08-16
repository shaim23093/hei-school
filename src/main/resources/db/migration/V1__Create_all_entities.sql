CREATE TABLE account
(
    id       UUID NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    role     VARCHAR(255),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE course
(
    id       UUID NOT NULL,
    code     VARCHAR(255),
    name     VARCHAR(255),
    credits  INTEGER,
    path     VARCHAR(255),
    semester INTEGER,
    CONSTRAINT pk_course PRIMARY KEY (id)
);

CREATE TABLE course_group
(
    id           UUID NOT NULL,
    course_id    UUID,
    group_id     UUID,
    promotion_id UUID,
    CONSTRAINT pk_course_group PRIMARY KEY (id)
);

CREATE TABLE dummy
(
    id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_dummy PRIMARY KEY (id)
);

CREATE TABLE dummy_uuid
(
    id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_dummyuuid PRIMARY KEY (id)
);

CREATE TABLE exam
(
    id               UUID NOT NULL,
    course_id        UUID,
    promotion_id     UUID,
    title            VARCHAR(255),
    date_time        TIMESTAMP WITHOUT TIME ZONE,
    duration_minutes INTEGER,
    coefficient      DOUBLE PRECISION,
    CONSTRAINT pk_exam PRIMARY KEY (id)
);

CREATE TABLE grade
(
    id         UUID NOT NULL,
    student_id UUID,
    exam_id    UUID,
    value      DOUBLE PRECISION,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_grade PRIMARY KEY (id)
);

CREATE TABLE grade_history
(
    id          UUID NOT NULL,
    grade_id    UUID,
    student_id  UUID,
    exam_id     UUID,
    value       DOUBLE PRECISION,
    modified_at TIMESTAMP WITHOUT TIME ZONE,
    author      VARCHAR(255),
    CONSTRAINT pk_grade_history PRIMARY KEY (id)
);

CREATE TABLE "group"
(
    id   UUID NOT NULL,
    name VARCHAR(255),
    path VARCHAR(255),
    CONSTRAINT pk_group PRIMARY KEY (id)
);

CREATE TABLE promotion
(
    id         UUID NOT NULL,
    name       VARCHAR(255),
    entry_year INTEGER,
    CONSTRAINT pk_promotion PRIMARY KEY (id)
);

CREATE TABLE student
(
    id           UUID NOT NULL,
    account_id   UUID,
    promotion_id UUID,
    std          VARCHAR(255),
    name         VARCHAR(255),
    first_name   VARCHAR(255),
    email        VARCHAR(255),
    CONSTRAINT pk_student PRIMARY KEY (id)
);

CREATE TABLE student_group
(
    id         UUID NOT NULL,
    student_id UUID,
    group_id   UUID,
    semester   INTEGER,
    CONSTRAINT pk_student_group PRIMARY KEY (id)
);

CREATE TABLE teacher
(
    id         UUID NOT NULL,
    account_id UUID,
    name       VARCHAR(255),
    first_name VARCHAR(255),
    email      VARCHAR(255),
    CONSTRAINT pk_teacher PRIMARY KEY (id)
);

CREATE TABLE teacher_course
(
    id           UUID NOT NULL,
    teacher_id   UUID,
    course_id    UUID,
    group_id     UUID,
    promotion_id UUID,
    CONSTRAINT pk_teacher_course PRIMARY KEY (id)
);

ALTER TABLE grade
    ADD CONSTRAINT uc_033c19a04f857581e3c06354a UNIQUE (student_id, exam_id);

ALTER TABLE student
    ADD CONSTRAINT uc_student_account UNIQUE (account_id);

ALTER TABLE teacher
    ADD CONSTRAINT uc_teacher_account UNIQUE (account_id);

ALTER TABLE course_group
    ADD CONSTRAINT FK_COURSE_GROUP_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE course_group
    ADD CONSTRAINT FK_COURSE_GROUP_ON_GROUP FOREIGN KEY (group_id) REFERENCES "group" (id);

ALTER TABLE course_group
    ADD CONSTRAINT FK_COURSE_GROUP_ON_PROMOTION FOREIGN KEY (promotion_id) REFERENCES promotion (id);

ALTER TABLE exam
    ADD CONSTRAINT FK_EXAM_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE exam
    ADD CONSTRAINT FK_EXAM_ON_PROMOTION FOREIGN KEY (promotion_id) REFERENCES promotion (id);

ALTER TABLE grade_history
    ADD CONSTRAINT FK_GRADE_HISTORY_ON_EXAM FOREIGN KEY (exam_id) REFERENCES exam (id);

ALTER TABLE grade_history
    ADD CONSTRAINT FK_GRADE_HISTORY_ON_GRADE FOREIGN KEY (grade_id) REFERENCES grade (id);

ALTER TABLE grade_history
    ADD CONSTRAINT FK_GRADE_HISTORY_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE grade
    ADD CONSTRAINT FK_GRADE_ON_EXAM FOREIGN KEY (exam_id) REFERENCES exam (id);

ALTER TABLE grade
    ADD CONSTRAINT FK_GRADE_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student_group
    ADD CONSTRAINT FK_STUDENT_GROUP_ON_GROUP FOREIGN KEY (group_id) REFERENCES "group" (id);

ALTER TABLE student_group
    ADD CONSTRAINT FK_STUDENT_GROUP_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_PROMOTION FOREIGN KEY (promotion_id) REFERENCES promotion (id);

ALTER TABLE teacher_course
    ADD CONSTRAINT FK_TEACHER_COURSE_ON_COURSE FOREIGN KEY (course_id) REFERENCES course (id);

ALTER TABLE teacher_course
    ADD CONSTRAINT FK_TEACHER_COURSE_ON_GROUP FOREIGN KEY (group_id) REFERENCES "group" (id);

ALTER TABLE teacher_course
    ADD CONSTRAINT FK_TEACHER_COURSE_ON_PROMOTION FOREIGN KEY (promotion_id) REFERENCES promotion (id);

ALTER TABLE teacher_course
    ADD CONSTRAINT FK_TEACHER_COURSE_ON_TEACHER FOREIGN KEY (teacher_id) REFERENCES teacher (id);

ALTER TABLE teacher
    ADD CONSTRAINT FK_TEACHER_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES account (id);