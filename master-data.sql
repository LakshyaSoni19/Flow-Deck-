-- =============================================================================
-- Flow Deck Task Management System - Master Data Seed Script
-- Idempotent MySQL Script using INSERT IGNORE
-- =============================================================================

USE flow_deck_db;

-- 1. Country Master Data
INSERT IGNORE INTO country (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'India', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'United States', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'United Kingdom', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Canada', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'Australia', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 2. State Master Data
INSERT IGNORE INTO state (id, name, country_id, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Rajasthan', 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Maharashtra', 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'Karnataka', 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'California', 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'New York', 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'Texas', 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(7, 'Ontario', 4, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 3. City Master Data
INSERT IGNORE INTO city (id, name, state_id, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Jaipur', 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Udaipur', 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'Jodhpur', 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Mumbai', 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'Pune', 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'Bengaluru', 3, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(7, 'Los Angeles', 4, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(8, 'San Francisco', 4, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(9, 'New York City', 5, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(10, 'Toronto', 7, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 4. Department Master Data
INSERT IGNORE INTO department (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Engineering', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Product Management', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'Quality Assurance', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Human Resources', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'DevOps & Infrastructure', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'UI/UX Design', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(7, 'Sales & Marketing', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 5. Designation Master Data
INSERT IGNORE INTO designation (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Software Engineer', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Senior Software Engineer', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'Lead Developer', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Product Owner', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'QA Engineer', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'DevOps Engineer', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(7, 'Project Manager', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(8, 'UI/UX Designer', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 6. Role Master Data
INSERT IGNORE INTO role (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'ROLE_ADMIN', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'ROLE_PROJECT_MANAGER', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'ROLE_DEVELOPER', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'ROLE_TESTER', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'ROLE_USER', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'ROLE_EMPLOYEE', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 7. Task Status Master Data
INSERT IGNORE INTO task_status (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Backlog', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'To Do', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'In Progress', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'In Review', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'Completed', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'Blocked', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 8. Task Priority Master Data
INSERT IGNORE INTO task_priority (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Low', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Medium', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'High', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Urgent', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 9. Task Type Master Data
INSERT IGNORE INTO task_type (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Feature', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Bug', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'Improvement', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Task', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'Subtask', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');
