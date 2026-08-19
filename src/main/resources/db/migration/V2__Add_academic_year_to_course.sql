DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'course' AND column_name = 'academic_year') THEN
        ALTER TABLE course ADD COLUMN academic_year INTEGER DEFAULT 1;
    END IF;
END $$;
