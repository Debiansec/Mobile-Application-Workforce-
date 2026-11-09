-- ====================================================================
-- WORKFORCE HEALTH SA — OCCUPATIONAL HYGIENE DIGITAL PLATFORM
-- DATABASE SCHEMA: PostgreSQL / Supabase
-- Version: 1.0.0
-- Compliant with MHSA & POPIA Audit & Security Standards
-- ====================================================================

-- 1. Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Organizations & Tenants
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Users & Role-Based Access Control
CREATE TYPE user_role AS ENUM (
    'FIELD_ASSESSOR',
    'OCCUPATIONAL_HYGIENIST',
    'MINE_MANAGEMENT',
    'SYSTEM_ADMIN'
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'FIELD_ASSESSOR',
    registration_number VARCHAR(100), -- e.g. SAIOH ROH 0482
    phone VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. Customer Companies
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    company_code VARCHAR(100) UNIQUE NOT NULL,
    contact_person VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. Sites & Mines
CREATE TABLE sites (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID REFERENCES customers(id) ON DELETE CASCADE,
    site_name VARCHAR(255) NOT NULL,
    property_description TEXT, -- e.g. Farm Tarentaalrant 24LT
    location VARCHAR(255) NOT NULL, -- Tzaneen
    province VARCHAR(100) NOT NULL, -- Limpopo Province
    mine_code VARCHAR(100), -- 14466
    mine_manager VARCHAR(255),
    mine_manager_contact TEXT,
    site_occupational_hygienist VARCHAR(255),
    occupational_medical_practitioner VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 6. Facilities & Locations
CREATE TABLE facilities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    site_id UUID REFERENCES sites(id) ON DELETE CASCADE,
    facility_name VARCHAR(255) NOT NULL, -- Main Change House - Male
    facility_type VARCHAR(100) NOT NULL, -- MALE_ABLUTION, FEMALE_ABLUTION, TOILET_BASIN
    building_description TEXT,
    estimated_users INT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 7. Checklist Templates & Versions
CREATE TABLE checklist_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) UNIQUE NOT NULL, -- ABLUTION_MALE_MONTHLY
    title VARCHAR(255) NOT NULL,
    module_category VARCHAR(100) NOT NULL,
    current_version INT NOT NULL DEFAULT 1,
    is_published BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE checklist_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id UUID REFERENCES checklist_templates(id) ON DELETE CASCADE,
    section_code VARCHAR(50) NOT NULL, -- SECTION_A
    section_title VARCHAR(255) NOT NULL, -- SECTION A — BASINS
    display_order INT NOT NULL
);

CREATE TABLE checklist_questions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    section_id UUID REFERENCES checklist_sections(id) ON DELETE CASCADE,
    question_number INT NOT NULL,
    question_text TEXT NOT NULL,
    response_type VARCHAR(50) NOT NULL DEFAULT 'YES_NO',
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    allows_not_applicable BOOLEAN NOT NULL DEFAULT FALSE
);

-- 8. Assessments (Master Records)
CREATE TYPE assessment_status AS ENUM (
    'DRAFT',
    'IN_PROGRESS',
    'SUBMITTED_FOR_REVIEW',
    'RETURNED_FOR_CORRECTION',
    'APPROVED',
    'ARCHIVED'
);

CREATE TYPE sync_status_type AS ENUM (
    'SYNCED',
    'PENDING_SYNC',
    'SYNCING',
    'FAILED'
);

CREATE TABLE assessments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reference_number VARCHAR(100) UNIQUE NOT NULL, -- WFH-OH-2026-0042
    customer_id UUID REFERENCES customers(id) ON DELETE RESTRICT,
    site_id UUID REFERENCES sites(id) ON DELETE RESTRICT,
    facility_id UUID REFERENCES facilities(id) ON DELETE RESTRICT,
    template_id UUID REFERENCES checklist_templates(id) ON DELETE RESTRICT,
    assessor_id UUID REFERENCES users(id) ON DELETE RESTRICT,
    assessment_date DATE NOT NULL,
    assessment_time TIME NOT NULL,
    previous_visit_date DATE,
    previous_remedial_actions TEXT,
    hygienist_special_instructions TEXT,
    status assessment_status NOT NULL DEFAULT 'DRAFT',
    sync_status sync_status_type NOT NULL DEFAULT 'SYNCED',
    compliance_score INT NOT NULL DEFAULT 0,
    total_questions INT NOT NULL DEFAULT 0,
    answered_count INT NOT NULL DEFAULT 0,
    non_compliant_count INT NOT NULL DEFAULT 0,
    reviewed_by_user_id UUID REFERENCES users(id),
    reviewer_comments TEXT,
    reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 9. Assessment Answers
CREATE TYPE compliance_response_type AS ENUM (
    'YES',
    'NO',
    'NOT_ASSESSED',
    'NOT_APPLICABLE'
);

CREATE TABLE assessment_answers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id UUID REFERENCES assessments(id) ON DELETE CASCADE,
    question_id UUID REFERENCES checklist_questions(id) ON DELETE RESTRICT,
    response compliance_response_type NOT NULL DEFAULT 'NOT_ASSESSED',
    remarks TEXT,
    photo_url TEXT,
    photo_timestamp TIMESTAMPTZ,
    photo_caption TEXT,
    geo_latitude NUMERIC(10, 7),
    geo_longitude NUMERIC(10, 7),
    has_remedial_action BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(assessment_id, question_id)
);

-- 10. Chemicals & Safety Data Sheets (SDS)
CREATE TYPE cleaning_type_enum AS ENUM (
    'DEEP_CLEANING',
    'ROUTINE_CLEANING'
);

CREATE TABLE chemical_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id UUID REFERENCES assessments(id) ON DELETE CASCADE,
    cleaning_type cleaning_type_enum NOT NULL,
    chemical_name VARCHAR(255) NOT NULL,
    manufacturer_or_supplier VARCHAR(255),
    sds_returned BOOLEAN NOT NULL DEFAULT FALSE,
    sds_document_url TEXT,
    usage_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE cleaning_procedure_documentation (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id UUID REFERENCES assessments(id) ON DELETE CASCADE,
    cleaning_type cleaning_type_enum NOT NULL,
    procedure_obtained BOOLEAN NOT NULL DEFAULT FALSE,
    procedure_returned BOOLEAN NOT NULL DEFAULT FALSE,
    contractor_organization VARCHAR(255),
    remarks TEXT,
    UNIQUE(assessment_id, cleaning_type)
);

-- 11. Remedial Actions Management
CREATE TYPE action_priority_enum AS ENUM (
    'LOW',
    'MEDIUM',
    'HIGH',
    'CRITICAL'
);

CREATE TYPE action_status_enum AS ENUM (
    'OPEN',
    'IN_PROGRESS',
    'RESOLVED',
    'VERIFIED'
);

CREATE TABLE remedial_actions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    action_reference VARCHAR(100) UNIQUE NOT NULL, -- REM-2026-011
    assessment_id UUID REFERENCES assessments(id) ON DELETE CASCADE,
    question_id UUID REFERENCES checklist_questions(id),
    finding_description TEXT NOT NULL,
    recommended_action TEXT NOT NULL,
    responsible_person_or_dept VARCHAR(255) NOT NULL,
    target_date DATE NOT NULL,
    priority action_priority_enum NOT NULL DEFAULT 'HIGH',
    status action_status_enum NOT NULL DEFAULT 'OPEN',
    photo_url TEXT,
    closure_evidence TEXT,
    closure_date DATE,
    verified_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 12. Digital Signatures & Attestations
CREATE TABLE digital_signatures (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    assessment_id UUID REFERENCES assessments(id) ON DELETE CASCADE,
    signer_role VARCHAR(100) NOT NULL, -- Field Assessor, Client Rep
    signer_name VARCHAR(255) NOT NULL,
    signer_id_number VARCHAR(100),
    signer_designation VARCHAR(150),
    signature_vector_path TEXT,
    signed_timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    declaration_accepted BOOLEAN NOT NULL DEFAULT TRUE,
    device_fingerprint VARCHAR(255)
);

-- 13. Equipment & Instrument Register
CREATE TABLE equipment (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    manufacturer VARCHAR(255),
    model VARCHAR(255),
    serial_number VARCHAR(255) UNIQUE NOT NULL,
    asset_tag VARCHAR(100) UNIQUE NOT NULL,
    last_calibration_date DATE NOT NULL,
    calibration_due_date DATE NOT NULL,
    calibration_certificate_url TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'CALIBRATED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 14. Audit Logs (POPIA & MHSA Compliance)
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    entity_name VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, SUBMIT, APPROVE, DELETE
    changes_json JSONB,
    client_ip VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 15. Row Level Security (RLS) Policies
ALTER TABLE assessments ENABLE ROW LEVEL SECURITY;
ALTER TABLE remedial_actions ENABLE ROW LEVEL SECURITY;
ALTER TABLE chemical_records ENABLE ROW LEVEL SECURITY;

-- Policy: Field Assessor can view and edit their own assessments
CREATE POLICY assessor_own_assessments ON assessments
    FOR ALL
    TO authenticated
    USING (assessor_id = auth.uid());

-- Policy: Occupational Hygienist and Admin can view and approve all assessments
CREATE POLICY hygienist_manage_all ON assessments
    FOR ALL
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM users
            WHERE users.id = auth.uid()
            AND (users.role = 'OCCUPATIONAL_HYGIENIST' OR users.role = 'SYSTEM_ADMIN')
        )
    );
