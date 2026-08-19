CREATE TABLE IF NOT EXISTS account
(
    id       UUID NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    role     VARCHAR(255),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS course
(
    id       UUID NOT NULL,
    code     VARCHAR(255),
    name     VARCHAR(255),
    credits  INTEGER,
    path     VARCHAR(255),
    semester INTEGER,
    CONSTRAINT pk_course PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS course_group
(
    id           UUID NOT NULL,
    course_id    UUID,
    group_id     UUID,
    promotion_id UUID,
    CONSTRAINT pk_course_group PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS dummy
(
    id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_dummy PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS dummy_uuid
(
    id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_dummyuuid PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS exam
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

CREATE TABLE IF NOT EXISTS grade
(
    id         UUID NOT NULL,
    student_id UUID,
    exam_id    UUID,
    value      DOUBLE PRECISION,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_grade PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS grade_history
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

CREATE TABLE IF NOT EXISTS "group"
(
    id   UUID NOT NULL,
    name VARCHAR(255),
    path VARCHAR(255),
    CONSTRAINT pk_group PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS promotion
(
    id         UUID NOT NULL,
    name       VARCHAR(255),
    entry_year INTEGER,
    CONSTRAINT pk_promotion PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student
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

CREATE TABLE IF NOT EXISTS student_group
(
    id         UUID NOT NULL,
    student_id UUID,
    group_id   UUID,
    semester   INTEGER,
    CONSTRAINT pk_student_group PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS teacher
(
    id         UUID NOT NULL,
    account_id UUID,
    name       VARCHAR(255),
    first_name VARCHAR(255),
    email      VARCHAR(255),
    CONSTRAINT pk_teacher PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS teacher_course
(
    id           UUID NOT NULL,
    teacher_id   UUID,
    course_id    UUID,
    group_id     UUID,
    promotion_id UUID,
    CONSTRAINT pk_teacher_course PRIMARY KEY (id)
);

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_033c19a04f857581e3c06354a') THEN
        ALTER TABLE grade ADD CONSTRAINT uc_033c19a04f857581e3c06354a UNIQUE (student_id, exam_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_student_account') THEN
        ALTER TABLE student ADD CONSTRAINT uc_student_account UNIQUE (account_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uc_teacher_account') THEN
        ALTER TABLE teacher ADD CONSTRAINT uc_teacher_account UNIQUE (account_id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_course_group_on_course') THEN
        ALTER TABLE course_group ADD CONSTRAINT fk_course_group_on_course FOREIGN KEY (course_id) REFERENCES course (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_course_group_on_group') THEN
        ALTER TABLE course_group ADD CONSTRAINT fk_course_group_on_group FOREIGN KEY (group_id) REFERENCES "group" (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_course_group_on_promotion') THEN
        ALTER TABLE course_group ADD CONSTRAINT fk_course_group_on_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_exam_on_course') THEN
        ALTER TABLE exam ADD CONSTRAINT fk_exam_on_course FOREIGN KEY (course_id) REFERENCES course (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_exam_on_promotion') THEN
        ALTER TABLE exam ADD CONSTRAINT fk_exam_on_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grade_history_on_exam') THEN
        ALTER TABLE grade_history ADD CONSTRAINT fk_grade_history_on_exam FOREIGN KEY (exam_id) REFERENCES exam (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grade_history_on_grade') THEN
        ALTER TABLE grade_history ADD CONSTRAINT fk_grade_history_on_grade FOREIGN KEY (grade_id) REFERENCES grade (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grade_history_on_student') THEN
        ALTER TABLE grade_history ADD CONSTRAINT fk_grade_history_on_student FOREIGN KEY (student_id) REFERENCES student (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grade_on_exam') THEN
        ALTER TABLE grade ADD CONSTRAINT fk_grade_on_exam FOREIGN KEY (exam_id) REFERENCES exam (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_grade_on_student') THEN
        ALTER TABLE grade ADD CONSTRAINT fk_grade_on_student FOREIGN KEY (student_id) REFERENCES student (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_student_group_on_group') THEN
        ALTER TABLE student_group ADD CONSTRAINT fk_student_group_on_group FOREIGN KEY (group_id) REFERENCES "group" (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_student_group_on_student') THEN
        ALTER TABLE student_group ADD CONSTRAINT fk_student_group_on_student FOREIGN KEY (student_id) REFERENCES student (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_student_on_account') THEN
        ALTER TABLE student ADD CONSTRAINT fk_student_on_account FOREIGN KEY (account_id) REFERENCES account (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_student_on_promotion') THEN
        ALTER TABLE student ADD CONSTRAINT fk_student_on_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_teacher_course_on_course') THEN
        ALTER TABLE teacher_course ADD CONSTRAINT fk_teacher_course_on_course FOREIGN KEY (course_id) REFERENCES course (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_teacher_course_on_group') THEN
        ALTER TABLE teacher_course ADD CONSTRAINT fk_teacher_course_on_group FOREIGN KEY (group_id) REFERENCES "group" (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_teacher_course_on_promotion') THEN
        ALTER TABLE teacher_course ADD CONSTRAINT fk_teacher_course_on_promotion FOREIGN KEY (promotion_id) REFERENCES promotion (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_teacher_course_on_teacher') THEN
        ALTER TABLE teacher_course ADD CONSTRAINT fk_teacher_course_on_teacher FOREIGN KEY (teacher_id) REFERENCES teacher (id);
    END IF;
END $$;

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_teacher_on_account') THEN
        ALTER TABLE teacher ADD CONSTRAINT fk_teacher_on_account FOREIGN KEY (account_id) REFERENCES account (id);
    END IF;
END $$;
