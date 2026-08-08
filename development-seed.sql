-- =============================================================================
-- Flow Deck Task Management System - Development Seed Data
-- Idempotent MySQL Script using INSERT IGNORE
-- =============================================================================

USE flow_deck_db;

-- 1. Ensure ROLE_EMPLOYEE Master Data
INSERT IGNORE INTO role (id, name, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(6, 'ROLE_EMPLOYEE', 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 2. Development Users Seed Data (20 Users)
-- BCrypt encoded password for "Password@123": $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a

INSERT IGNORE INTO users (id, first_name, last_name, email, password, mobile, gender, dob, address, city_id, department_id, designation_id, is_active, approval_status, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
(1, 'Aarav', 'Sharma', 'admin@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543210', 'Male', '1988-05-15', '101 MG Road, Indiranagar', 6, 1, 3, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(2, 'Rajesh', 'Verma', 'rajesh.verma@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543211', 'Male', '1990-08-20', '45 Civil Lines', 1, 1, 7, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 'Priya', 'Malhotra', 'priya.malhotra@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543212', 'Female', '1992-03-12', '72 Bandra West', 4, 2, 4, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 'Vikramaditya', 'Singh', 'vikram.singh@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543213', 'Male', '1989-11-05', '88 Koregaon Park', 5, 5, 7, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(5, 'Amit', 'Kumar', 'amit.kumar@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543214', 'Male', '1995-01-25', '12 Vaishali Nagar', 1, 1, 1, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 'Sneha', 'Patel', 'sneha.patel@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543215', 'Female', '1994-07-18', '304 Viman Nagar', 5, 1, 2, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(7, 'Rohan', 'Gupta', 'rohan.gupta@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543216', 'Male', '1996-09-30', '56 HSR Layout', 6, 1, 1, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(8, 'Ananya', 'Roy', 'ananya.roy@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543217', 'Female', '1993-12-10', '15 Andheri East', 4, 2, 4, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(9, 'Deepak', 'Joshi', 'deepak.joshi@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543218', 'Male', '1995-04-05', '22 Hiran Magri', 2, 3, 5, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(10, 'Pooja', 'Reddy', 'pooja.reddy@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543219', 'Female', '1997-02-14', '89 Whitefield', 6, 3, 5, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(11, 'Manish', 'Choudhary', 'manish.choudhary@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543220', 'Male', '1991-08-22', '44 Ratanada', 3, 4, 1, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(12, 'Kavita', 'Saxena', 'kavita.saxena@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543221', 'Female', '1993-06-17', '67 Malviya Nagar', 1, 4, 2, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(13, 'Rahul', 'Nair', 'rahul.nair@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543222', 'Male', '1994-10-11', '19 Hinjawadi Phase 1', 5, 5, 6, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(14, 'Neha', 'Agarwal', 'neha.agarwal@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543223', 'Female', '1996-01-29', '81 Powai', 4, 5, 6, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(15, 'Suresh', 'Kulkarni', 'suresh.kulkarni@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543224', 'Male', '1992-09-08', '53 Kothrud', 5, 6, 8, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(16, 'Divya', 'Deshmukh', 'divya.deshmukh@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543225', 'Female', '1995-11-23', '27 Thane West', 4, 6, 8, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(17, 'Sanjay', 'Mehta', 'sanjay.mehta@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543226', 'Male', '1990-04-19', '14 C-Scheme', 1, 7, 2, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(18, 'Ritu', 'Sharma', 'ritu.sharma@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543227', 'Female', '1993-08-04', '90 Koramangala', 6, 7, 3, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(19, 'Alok', 'Pandey', 'alok.pandey@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543228', 'Male', '1991-12-15', '38 Baner', 5, 1, 3, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(20, 'Meenakshi', 'Sundaram', 'meenakshi.s@flowdeck.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', '9876543229', 'Female', '1996-07-07', '74 Electronic City', 6, 3, 5, 1, 'APPROVED', NOW(), NOW(), 'SYSTEM', 'SYSTEM');

-- 3. User Roles Assignment Seed Data (1 ADMIN, 3 PROJECT MANAGER, 16 EMPLOYEE)
-- Role IDs: 1 = ROLE_ADMIN, 2 = ROLE_PROJECT_MANAGER, 6 = ROLE_EMPLOYEE

INSERT IGNORE INTO user_role (id, user_id, role_id, is_active, created_at, updated_at, audit_created_by, audit_updated_by) VALUES
-- Admin (1)
(1, 1, 1, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

-- Project Managers (3)
(2, 2, 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(3, 3, 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(4, 4, 2, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

-- Employees (16)
(5, 5, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(6, 6, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(7, 7, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(8, 8, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(9, 9, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(10, 10, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(11, 11, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(12, 12, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(13, 13, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(14, 14, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(15, 15, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(16, 16, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(17, 17, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(18, 18, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(19, 19, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),
(20, 20, 6, 1, NOW(), NOW(), 'SYSTEM', 'SYSTEM');
