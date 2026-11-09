-- ==============================================================================
-- WORKFORCE HEALTH SA — WordPress Sample Data Import
-- Database: MySQL / MariaDB (WordPress wp_posts, wp_postmeta)
-- Target Domain: https://wfhsa.com
-- ==============================================================================

-- Sample Assessment 1: Letaba Crushers
INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, post_status, comment_status, ping_status, post_name, post_type)
VALUES (1, NOW(), UTC_TIMESTAMP(), 'Monthly Male Ablution Facilities Inspection for Letaba Crushers Main Change House.', 'WFH-OH-2026-0042', 'publish', 'closed', 'closed', 'wfh-oh-2026-0042', 'wfhsa_assessment');

SET @last_post_id = LAST_INSERT_ID();

INSERT INTO wp_postmeta (post_id, meta_key, meta_value) VALUES
(@last_post_id, 'wfhsa_score', '85'),
(@last_post_id, 'wfhsa_customer', 'Letaba Crushers (Pty) Ltd'),
(@last_post_id, 'wfhsa_site', 'Main Quarry & Plant (Farm Tarentaalrant 24LT, Tzaneen, Limpopo)'),
(@last_post_id, 'wfhsa_facility', 'Monthly Ablution Facilities Checklist — Male (Main Change House)'),
(@last_post_id, 'wfhsa_status', 'SUBMITTED_FOR_REVIEW'),
(@last_post_id, 'wfhsa_assessor', 'Nomvula Khumalo (SAIOH OH-Asst 1892)'),
(@last_post_id, 'wfhsa_mine_manager', 'Pieter Botha (Pr.Eng, Mine #14466)'),
(@last_post_id, 'wfhsa_total_items', '50'),
(@last_post_id, 'wfhsa_compliant_items', '43'),
(@last_post_id, 'wfhsa_action_items', '7');

-- Sample Remedial Action for Assessment 1
INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, post_status, comment_status, ping_status, post_name, post_type)
VALUES (1, NOW(), UTC_TIMESTAMP(), 'Mount SANS approved hand-wash infographics at wash stations #3 and #4.', 'REM-2026-001: Hand Washing Posters Missing', 'publish', 'closed', 'closed', 'rem-2026-001', 'wfhsa_remedial');

SET @last_rem_id = LAST_INSERT_ID();

INSERT INTO wp_postmeta (post_id, meta_key, meta_value) VALUES
(@last_rem_id, 'wfhsa_assessment_ref', 'WFH-OH-2026-0042'),
(@last_rem_id, 'wfhsa_priority', 'HIGH'),
(@last_rem_id, 'wfhsa_responsible', 'Site Safety Officer'),
(@last_rem_id, 'wfhsa_target_date', '2026-11-05'),
(@last_rem_id, 'wfhsa_status', 'OPEN');

-- Sample Chemical Register Entry
INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, post_status, comment_status, ping_status, post_name, post_type)
VALUES (1, NOW(), UTC_TIMESTAMP(), 'Viro-Cleanse Ultra QAC - 16-point GHS SDS approved for bi-weekly deep sanitization.', 'Viro-Cleanse Ultra QAC', 'publish', 'closed', 'closed', 'viro-cleanse-ultra', 'wfhsa_chemical');

SET @last_chem_id = LAST_INSERT_ID();

INSERT INTO wp_postmeta (post_id, meta_key, meta_value) VALUES
(@last_chem_id, 'wfhsa_cleaning_type', 'DEEP_CLEANING'),
(@last_chem_id, 'wfhsa_supplier', 'Ecolab SA (Pty) Ltd'),
(@last_chem_id, 'wfhsa_sds_present', '1'),
(@last_chem_id, 'wfhsa_sds_revision', '2026-04-12');
